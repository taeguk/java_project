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
			return true;
		} else {
			return false;
		}
	}
	
	public User deleteUser(User user) {
		if(user == admin) {
			admin = guest;
			guest = null;
		} else if(user == guest) {
			guest = null;
		}
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
	
	public int getRoomId() { return roomId; }
	public String getRoomName() { return roomName; }
	public User getAdmin() { return admin; }
	public User getGuest() { return guest; }
	public boolean getIsOpened() { return isOpened; }
	public int getGameMode() { return gameMode; }
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

	public boolean isRoomAdmin(Socket socket) {
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

	public String getMyRoomGuest(Socket socket) {
		User user = getUser(socket);
		Room room = user.getRoom();
		User guest = room.getGuest();
		
		if(guest == null) {
			return null;
		} else {
			return guest.getUserName();
		}
	}

	public boolean isInRoom(Socket socket) {
		User user = getUser(socket);
		return (user.getRoom() == null ? false : true);
	}

	public void exitRoom(Socket socket) {
		User user = getUser(socket);
		Room room = user.getRoom();
		
		user.setStatus(User.IN_LOBBY);
		User remainUser = room.deleteUser(user);		// return remain user
		if(remainUser == null) {
			// delete room.
			rooms.remove(room.getRoomId());
		} else {
			// send ENEMY_EXIT packet to remainUser.
			sendEnemyExit(remainUser);
		}
	}

	private void sendEnemyExit(User remainUser) {
		Socket socket = remainUser.getSocket();
		synchronized(socket) {
			try {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.ENEMY_EXIT);
				resData = resDataStream.toByteArray();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
			} catch(IOException e) {
				// error handling.
			}
		}
	}

	public void readyGame(Socket socket) {
		User user = getUser(socket);
		Room room = user.getRoom();
		user.setStatus(User.IN_ROOM_READY);
		User enemy = room.getEnemy(user);
		if(enemy != null) {
			
		}
	}
	
	private User getEnemy(User user) {
		Room room = user.getRoom();
		if(room == null)
			return null;
		User admin = room.getAdmin();
		User guest = room.getGuest();
		if(admin == user) {
			return admin;
		} else if(guest == user) {
			return guest;
		} else {
			return null;
		}
	}
}
