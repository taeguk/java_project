import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

class User {
	public static final int IN_LOBBY = 1;
	public static final int IN_ROOM_NOT_READY = 2;
	public static final int IN_ROOM_READY = 3;
	public static final int ING_GAME = 4;
	
	private Socket socket;
	private String userName;
	private int status;
	private Room room = null;
	
	User(Socket socket, String userName) {
		this.socket = socket;
		this.userName = userName;
		this.status = IN_LOBBY;
	}
	
	void setStatus(int status) {
		this.status = status;
	}
	void setRoom(Room room) {
		this.room = room;
	}
	
	void enterRoom(Room room) {
		this.room = room;
		this.status = IN_ROOM_NOT_READY;
	}
	
	public Socket getSocket() { return socket; }
	public String getUserName() { return userName; }
	public int getStatus() { return status; }
	public Room getRoom() { return room; }
}

class Room {
	public static final int ONE_WINS = 1;	// ´ÜÆÇ½ÂºÎ
	public static final int TWO_WINS = 2;	// 3ÆÇ 2½ÂÁ¦
	public static final int THREE_WINS = 3;	// 5ÆÇ 3½ÂÁ¦
	public static final int FOUR_WINS = 4;	// 7ÆÇ 4½ÂÁ¦
	
	private static int id = 0;
	
	private int roomId;
	private String roomName;
	private User admin = null, guest = null;
	private boolean isOpened = true;
	private int gameMode;
	
	public static final int FIELD_HEIGHT = 9;
	public static final int FIELD_WIDTH = 7;
	
	public static final int NOTHING = 0;
	public static final int ADMIN = 1;
	public static final int GUEST = 2;
	
	public static final int NO_BALL = NOTHING;
	public static final int RED_BALL = ADMIN;
	public static final int BLUE_BALL = GUEST;
	
	private boolean isFinishByVictory = false;
	private boolean isGameStart = false;
	private int[][] field;
	private int turn;
	private int recentPos = -1;
	
	Room(String roomName, int gameMode) {
		this.roomId = ++id;
		this.roomName = roomName;
		this.gameMode = gameMode;
	}
	
	Room(User admin, String roomName, int gameMode) {
		this.roomId = ++id;
		this.roomName = roomName;
		this.admin = admin;
		this.gameMode = gameMode;
	}
	
	public int getRoomId() { return roomId; }
	public String getRoomName() { return roomName; }
	public User getAdmin() { return admin; }
	public User getGuest() { return guest; }
	public boolean isOpened() { return isOpened; }
	public int getGameMode() { return gameMode; }
	public boolean isGameStart() { return isGameStart; }
	public boolean isFinishByVictory() { return isFinishByVictory; }
	
	public boolean setAdmin(User admin) {
		if(this.admin == null) {
			this.admin = admin;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean setGuest(User guest) {
		if(this.guest == null) {
			this.guest = guest;
			isOpened = false;
			return true;
		} else {
			return false;
		}
	}
	
	public User deleteUser(User user) {
		if(isGameStart) gameFinish(false);
		if(user == admin) {
			admin = guest;
			guest = null;
		} else if(user == guest) {
			guest = null;
		}
		if(admin != null)
			admin.setStatus(User.IN_ROOM_NOT_READY);
		isOpened = true;
		return admin;
	}
	
	public int getUserNum() {
		return (admin==null?0:1) + (guest==null?0:1);
	}
	
	public NetworkRoom toNetworkRoom() {
		int userNum = getUserNum();
		String[] userNames = new String[userNum];
		if(userNum==1) {
			userNames[0] = admin.getUserName();
		} else if(userNum==2) {
			userNames[0] = admin.getUserName();
			userNames[1] = guest.getUserName();
		} else {
			userNames = null;
		}
		NetworkRoom networkRoom = new NetworkRoom(roomId, roomName, userNames, isOpened, gameMode);
		return networkRoom;
	}
	
	public void initializeGame() {
		System.out.println("[Log] Game Start!");
		isGameStart = true;
		field = new int[FIELD_HEIGHT][];
		for(int i=0; i<FIELD_HEIGHT; ++i)
			field[i] = new int[FIELD_WIDTH];
		for(int i=0; i<FIELD_HEIGHT; ++i)
			for(int j=0; j<FIELD_WIDTH; ++j)
				field[i][j] = NO_BALL;
		turn = ADMIN;
		recentPos = -1;
	}
	
	public boolean dropBall(User user, int xPos) {
		int who = who(user);
		int yPos;
		for(yPos=FIELD_HEIGHT-1; yPos>=0; --yPos) {
			if(field[yPos][xPos] == NO_BALL) break;
		}
		field[yPos][xPos] = who;
		recentPos = xPos;
		if(turn == ADMIN)
			turn = GUEST;
		else 
			turn = ADMIN;
		if(isGameFinishByVictory(who, yPos, xPos)) {
			gameFinish(true);
			return true;
		} else {
			return false;
		}
	}
	
	private void gameFinish(boolean isFinishByVictory) {
		System.out.println("[Log] Game Finish!");
		admin.setStatus(User.IN_ROOM_NOT_READY);
		guest.setStatus(User.IN_ROOM_NOT_READY);
		isGameStart = false;
		this.isFinishByVictory = isFinishByVictory;
		field = null;
	}
	
	private boolean isGameFinishByVictory(int who, int yPos, int xPos){
		// check horizontal ----
		for(int x = xPos-3; x <= xPos; ++x) {
			if(x < 0 || x+3 >= FIELD_WIDTH)
				continue;
			int i;
			for(i=0; i<4; ++i) {
				if(field[yPos][x+i] != who)
					break;
			}
			if(i == 4)
				return true;
		}
		
		// check vertical |
		for(int y = yPos-3; y <= xPos; ++y) {
			if(y < 0 || y+3 >= FIELD_WIDTH)
				continue;
			int i;
			for(i=0; i<4; ++i) {
				if(field[y+i][xPos] != who)
					break;
			}
			if(i == 4)
				return true;
		}
		
		// check diagonal line 1 \
		for(int y = yPos-3, x = xPos - 3; y <= xPos ; ++y, ++x) {
			if(y < 0 || y+3 >= FIELD_WIDTH || x < 0 || x+3 >= FIELD_WIDTH)
				continue;
			int i;
			for(i=0; i<4; ++i) {
				if(field[y+i][x+i] != who)
					break;
			}
			if(i == 4)
				return true;
		}
		
		// check diagonal line 2 /
		for(int y = yPos-3, x = xPos + 3; y <= xPos ; ++y, --x) {
			if(y < 0 || y+3 >= FIELD_WIDTH || x-3 < 0 || x >= FIELD_WIDTH)
				continue;
			int i;
			for(i=0; i<4; ++i) {
				if(field[y+i][x-i] != who)
					break;
			}
			if(i == 4)
				return true;
		}
			
		return false;
	}
	
	public boolean canDropBall(User user, int pos) {
		int who = who(user);
		
		if(!isTurn(who))
			return false;
		if(pos < 0 || pos >= FIELD_WIDTH)
			return false;
		if(field[0][pos] != NO_BALL)
			return false;
		
		return true;
	}
	
	public boolean isTurn(User user) {
		int who = who(user);
		return isTurn(who);
	}
	
	private boolean isTurn(int who) {
		return (turn == who);
	}
	
	private int who(User user) {
		if(user==admin) {
			return ADMIN;
		} else if(user==guest) {
			return GUEST;
		} else {
			return NOTHING;
		}
	}
	
	public int getEnemyBallDropPos(User user) {
		int who = who(user);
		if(!isTurn(who))
			return -1;
		else 
			return recentPos;
	}
}

public class ServerManager {
	HashMap<Socket,User> users;
	HashMap<Integer,Room> rooms;
	
	ServerManager() {
		users = new HashMap<Socket,User>();
		rooms = new HashMap<Integer,Room>();
		Collections.synchronizedMap(users);
		Collections.synchronizedMap(rooms);
	}
	
	synchronized public boolean isLogin(Socket socket) {
		return users.keySet().contains(socket);
	}
	
	synchronized public User getUser(Socket socket) {
		return users.get(socket);
	}
	
	synchronized public Room getRoom(int roomId) {
		return rooms.get(roomId);
	}
	
	synchronized public void addUser(Socket socket, User user) {
		users.put(socket, user);
	}
	
	synchronized public void addRoom(int roomId, Room room) {
		rooms.put(roomId, room);
	}
	
	synchronized public int enterLobby(Socket socket, String userName) {
		boolean isDuplicate = false;
		Iterator<Socket> it = users.keySet().iterator();
		
		while(it.hasNext()) {
			String oName = getUser(it.next()).getUserName();
			if(userName.equals(oName)) {
				isDuplicate = true;
				break;
			}
		}
		
		if(isDuplicate) {
			return NetworkInterface.NICKNAME_DUP;
		} else {
			User user = new User(socket, userName);
			addUser(socket, user);
			return NetworkInterface.NICKNAME_OK;
		}
	}

	synchronized public int getUserNum() {
		return users.size();
	}

	synchronized public NetworkRoomList getRoomList() {
		int networkRoomNum = rooms.size();
		NetworkRoom[] networkRooms = new NetworkRoom[networkRoomNum];
		int i=0;
		
		Iterator<Integer> it = rooms.keySet().iterator();
		while(it.hasNext()) {
			networkRooms[i++] = getRoom(it.next()).toNetworkRoom();
		}
		
		NetworkRoomList networkRoomList = new NetworkRoomList(networkRoomNum, networkRooms);
		return networkRoomList;
	}
	
	synchronized public boolean makeRoom(Socket socket, String roomName, int gameMode) {
		User user = getUser(socket);
		Room room = new Room(user, roomName, gameMode);
		addRoom(room.getRoomId(), room);
		room.setAdmin(user);
		user.enterRoom(room);
		return true;
	}

	synchronized public int enterRoom(Socket socket, int roomId) {
		User user = getUser(socket);
		Room room = getRoom(roomId);
		if(room == null) {
			return NetworkInterface.ROOM_DEL;
		} else {
			if(room.getUserNum() >= 2) {
				return NetworkInterface.ROOM_FULL;
			} else {
				room.setGuest(user);
				user.enterRoom(room);
				return NetworkInterface.ENTER_ROOM_OK;
			}
		}
	}

	synchronized public boolean isRoomAdmin(Socket socket) {
		User user = getUser(socket);
		Room room = user.getRoom();
		
		if(room == null) {
			return false;
		} else {
			if(room.getAdmin() == user)
				return true;
			else
				return false;
		}
	}

	synchronized public String getMyRoomGuest(Socket socket) {
		User user = getUser(socket);
		Room room = user.getRoom();
		User guest = room.getGuest();
		
		if(guest == null) {
			return null;
		} else {
			return guest.getUserName();
		}
	}

	synchronized public boolean isInRoom(Socket socket) {
		User user = getUser(socket);
		return (user.getRoom() == null ? false : true);
	}

	synchronized public Socket exitRoom(Socket socket) {
		User user = getUser(socket);
		Room room = user.getRoom();
		
		user.setStatus(User.IN_LOBBY);
		User remainUser = room.deleteUser(user);		// return remain user
		if(remainUser == null) {
			// delete room.
			rooms.remove(room.getRoomId());
			return null;
		} else {
			return remainUser.getSocket();
		}
	}

	synchronized public boolean readyGame(Socket socket) {
		User user = getUser(socket);
		Room room = user.getRoom();
		User enemy = getEnemy(user);
		if(enemy != null) {
			user.setStatus(User.IN_ROOM_READY);
			if(enemy.getStatus() == User.IN_ROOM_READY) {
				// game start
				gameStart(room);
			}
			return true;
		} else {
			return false;
		}
	}

	synchronized private User getEnemy(User user) {
		Room room = user.getRoom();
		if(room == null)
			return null;
		User admin = room.getAdmin();
		User guest = room.getGuest();
		if(admin == user) {
			return guest;
		} else if(guest == user) {
			return admin;
		} else {
			return null;
		}
	}
	
	synchronized private void gameStart(Room room) {
		room.initializeGame();
	}

	synchronized public boolean isGameStart(Socket socket) {
		User user = getUser(socket);
		Room room = user.getRoom();
		return room.isGameStart();
	}
	
	synchronized public boolean canDropBall(Socket socket, int pos) {
		User user = getUser(socket);
		Room room = user.getRoom();
		return room.canDropBall(user, pos);
	}

	synchronized public void dropBall(Socket socket, int pos) {
		User user = getUser(socket);
		Room room = user.getRoom();
		room.dropBall(user, pos);
	}

	synchronized public int isEnemyDropBall(Socket socket) {
		User user = getUser(socket);
		Room room = user.getRoom();
		return room.getEnemyBallDropPos(user);
	}
	
	synchronized public void terminateUser(Socket socket) {
		User user = getUser(socket);
		if(user != null) {
			Room room = user.getRoom();
			if(room != null)
				exitRoom(socket);
			users.remove(socket);
			rooms.remove(room.getRoomId());
		}
	}

	synchronized public boolean isFinishByVictory(Socket socket) {
		User user = getUser(socket);
		Room room = user.getRoom();
		return room.isFinishByVictory();
	}
}
