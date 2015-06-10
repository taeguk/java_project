class Pair<U, V> {

	/**
	 * The first element of this <code>Pair</code>
	 */
	private U first;

	/**
	 * The second element of this <code>Pair</code>
	 */
	private V second;

	/**
	 * Constructs a new <code>Pair</code> with the given values.
	 * 
	 * @param first
	 *            the first element
	 * @param second
	 *            the second element
	 */
	public Pair(U first, V second) {

		this.first = first;
		this.second = second;
	}
	
	public U first() {
        return first;
    }

    public V second() {
        return second;
    }
}

class NetworkRoom {
	public static final int ONE_WINS = 1;	// 단판승부
	public static final int TWO_WINS = 2;	// 3판 2승제
	public static final int THREE_WINS = 3;	// 5판 3승제
	public static final int FOUR_WINS = 4;	// 7판 4승제
	
	private int roomId;
	private String roomName;
	private String[] guests;
	private boolean isOpened;
	private int gameMode;
}

class NetworkRoomList {
	private int roomNum;
	private NetworkRoom[] rooms;
}

// 밑에 구체적인 상수 수치는 추후 변경할 것임!
interface NetworkInterface {
	// all functions can return NETWORK_ERROR
	public static final int NETWORK_ERROR = 1;
	// all functions can return INVALID_REQ or INVALID_RES
	public static final int INVALID_REQ = -2;
	public static final int INVALID_RES = -3;
	// wait로 표시된 함수들은 이 값을 반환할 수 있음.
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
	int makeRoom(NetworkRoom room);
	
	public static final int ENTER_ROOM_OK = 11;
	public static final int ROOM_FULL = 12;
	public static final int ROOM_DEL = 13;
	int enterRoom(int roomId);
	
	// String -> user name
	public static final int USER_ENTER = 14;
	Pair<Integer, String> waitUser();		// wait
	
	
	// 이 밑의 모든 함수들은 USER_EXIT을 반활 할 수 있음.
	public static final int USER_EXIT = 111;
	
	// call one more if this function return USER_EXIT or INVALID_RES
	// internally using static flag, prevent duplicate sending packet.
	// 이 사실은 모든 함수들에 해당됨.
	public static final int READY_OK = 15;
	int readyGame();	// wait
	
	public static final int EXIT_ROOM_OKAY = 2222;
	int exitRoom();
	
	public static final int GAME_START = 16;
	int waitGameStart();
	
	public static final int DROP_BALL_OK = 17;
	int dropBall(int pos);
	
	public static final int ENEMY_DROP = 18;
	public Pair<Integer, Integer> waitDrop();	// wait
}


class NetworkMethod implements NetworkInterface {
	~~~~
}
