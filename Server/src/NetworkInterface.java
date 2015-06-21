import java.io.Serializable;

class NetworkRoom implements Serializable{
	public static final int ONE_WINS = 1;	// ���ǽº�
	public static final int TWO_WINS = 2;	// 3�� 2����
	public static final int THREE_WINS = 3;	// 5�� 3����
	public static final int FOUR_WINS = 4;	// 7�� 4����
	
	private int roomId;
	private String roomName;
	private String[] guests;
	private boolean isOpened;
	private int gameMode;
	
	NetworkRoom(int roomId, String roomName, String[] guests, boolean isOpened, int gameMode) {
		this.roomId = roomId;
		this.roomName = roomName;
		this.guests = guests;
		this.isOpened = isOpened;
		this.gameMode = gameMode;
	}
	
	public int getRoomId() { return roomId; }
	public String getRoomName() { return roomName; }
	public String[] getGuests() { return guests; }
	public boolean getIsOpened() { return isOpened; }
	public int getGameMode() { return gameMode; }
}

class NetworkRoomList implements Serializable {
	private int roomNum;
	private NetworkRoom[] rooms;
	
	NetworkRoomList(int roomNum, NetworkRoom[] rooms) {
		this.roomNum = roomNum;
		this.rooms = rooms;
	}
	
	public int getRoomNum() { return roomNum; }
	public NetworkRoom[] getRooms() { return rooms; }
}

// �ؿ� ��ü���� ��� ��ġ�� ���� ������ ����!
interface NetworkInterface {
	// all functions can return NETWORK_ERROR
	public static final int NETWORK_ERROR = 1;
	// all functions can return INVALID_REQ or INVALID_RES
	public static final int INVALID_REQ = -2;
	public static final int INVALID_RES = -3;
	// wait�� ǥ�õ� �Լ����� �� ���� ��ȯ�� �� ����.
	public static final int TIME_OVER = -1;
	
	public static final int NICKNAME_OK = 2;
	public static final int NICKNAME_DUP = 3;
	public static final int NICKNAME_INVALID = 4;
	int enterLobby(String nickname);
	
	// return server user number
	int getUserNum();
	
	// second is null if fail
	public static final int GET_ROOM_LIST_OK = 7;
	Pair<Integer, NetworkRoomList> getRoomList();
	
	public static final int MAKE_ROOM_OK = 10;
	int makeRoom(String roomName, int gameMode);
	
	public static final int ENTER_ROOM_OK = 11;
	public static final int ROOM_FULL = 12;
	public static final int ROOM_DEL = 13;
	int enterRoom(int roomId);
	
	// String -> user name
	public static final int USER_ENTER = 14;
	Pair<Integer, String> waitUser();		// wait
	
	
	// �� ���� ��� �Լ����� USER_EXIT�� ��Ȱ �� �� ����.
	public static final int ENEMY_EXIT = 111;
	
	// call one more if this function return USER_EXIT or INVALID_RES
	// internally using static flag, prevent duplicate sending packet.
	// �� ����� ��� �Լ��鿡 �ش��.
	public static final int READY_OK = 15;
	int readyGame();	
	
	public static final int EXIT_ROOM_OKAY = 2222;
	int exitRoom();
	
	public static final int GAME_START = 16;
	int waitGameStart();		// wait
	
	public static final int DROP_BALL_OK = 17;
	int dropBall(int pos);
	
	public static final int ENEMY_DROP = 18;
	public Pair<Integer, Integer> waitDrop();	// wait
}