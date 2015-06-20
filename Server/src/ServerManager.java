import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

class User {
	private String userName;
	private int status;
	
	User(String userName) {
		this.userName = userName;
	}
	public String getUserName() { return userName; }
}

class Room {
	public static final int ONE_WINS = 1;	// ´ÜÆÇ½ÂºÎ
	public static final int TWO_WINS = 2;	// 3ÆÇ 2½ÂÁ¦
	public static final int THREE_WINS = 3;	// 5ÆÇ 3½ÂÁ¦
	public static final int FOUR_WINS = 4;	// 7ÆÇ 4½ÂÁ¦
	
	private static int id = 0;
	
	private int roomId;
	private String roomName;
	private Pair<Socket,User> admin = null, guest = null;
	private boolean isOpened = true;
	private int gameMode;
	
	Room(String roomName, int gameMode) {
		this.roomId = ++id;
		this.roomName = roomName;
		this.gameMode = gameMode;
	}
	
	Room(Pair<Socket,User> admin, String roomName, int gameMode) {
		this.roomId = ++id;
		this.roomName = roomName;
		this.admin = admin;
		this.gameMode = gameMode;
	}
	
	public boolean setAdmin(Pair<Socket,User> admin) {
		if(this.admin == null) {
			this.admin = admin;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean setGuest(Pair<Socket,User> guest) {
		if(this.guest == null) {
			this.guest = guest;
			return true;
		} else {
			return false;
		}
	}
	
	public int getUserNum() {
		return (admin==null?0:1) + (guest==null?0:1);
	}
	
	public NetworkRoom toNetworkRoom() {
		int userNum = getUserNum();
		String[] userNames = new String[userNum];
		if(userNum==1) {
			userNames[0] = admin.second().getUserName();
		} else if(userNum==2) {
			userNames[0] = admin.second().getUserName();
			userNames[1] = guest.second().getUserName();
		} else {
			userNames = null;
		}
		NetworkRoom networkRoom = new NetworkRoom(roomId, roomName, userNames, isOpened, gameMode);
		return networkRoom;
	}
	
	public int getRoomId() { return roomId; }
	public String getRoomName() { return roomName; }
	public Pair<Socket,User> getAdmin() { return admin; }
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
	
	synchronized public int addUser(Socket socket, String userName) {
		boolean isDuplicate = false;
		Iterator<Socket> it = users.keySet().iterator();
		
		while(it.hasNext()) {
			String oName = users.get(it.next()).getUserName();
			if(userName.equals(oName)) {
				isDuplicate = true;
				break;
			}
		}
		
		if(isDuplicate) {
			return NetworkInterface.NICKNAME_DUP;
		} else {
			User user = new User(userName);
			users.put(socket, user);
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
			networkRooms[i++] = rooms.get(it.next()).toNetworkRoom();
		}
		
		NetworkRoomList networkRoomList = new NetworkRoomList(networkRoomNum, networkRooms);
		return networkRoomList;
	}
	
	synchronized public boolean makeRoom(Socket socket, String roomName, int gameMode) {
		Room room = new Room(new Pair<Socket,User>(socket, users.get(socket)), roomName, gameMode);
		rooms.put(room.getRoomId(),room);
		return true;
	}

	synchronized public int enterRoom(Socket socket, int roomId) {
		Room room;
		if((room=rooms.get(roomId)) == null) {
			return NetworkInterface.ROOM_DEL;
		} else {
			if(room.getUserNum() >= 2) {
				return NetworkInterface.ROOM_FULL;
			} else {
				room.setGuest(new Pair<Socket,User>(socket,users.get(socket)));
				return NetworkInterface.ENTER_ROOM_OK;
			}
		}
	}
}
