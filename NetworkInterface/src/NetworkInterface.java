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
	public static final int ONE_WINS = 1;	// ´ÜÆÇ½ÂºÎ
	public static final int TWO_WINS = 2;	// 3ÆÇ 2½ÂÁ¦
	public static final int THREE_WINS = 3;	// 5ÆÇ 3½ÂÁ¦
	public static final int FOUR_WINS = 4;	// 7ÆÇ 4½ÂÁ¦
	
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

interface NetworkInterface {
	// all functions can return NETWORK_ERROR
	public static final int NETWORK_ERROR = 1;
	// all functions can return INVALID_REQ
	public static final int INVALID_REQ = -2;
	public static final int INVALID_RES = -3;
	// wait·Î Ç¥½ÃµÈ ÇÔ¼öµé
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
	
	public static final int USER_ENTER = 14;
	int waitUser();		// wait
	
	public static final int USER_EXIT = 111;
	
	public static final int READY_OK = 15;
	int readyGame();	// wait
	
	// process that and call one more if return USER_EXIT or INVALID_RES
	// internally using static flag, prevent duplicate sending packet.
	public static final int EXIT_ROOM_OKAY = 2222;
	int exitRoom();
	
	public static final int GAME_START = 16;
	int waitGameStart();
	
	public static final int DROP_BALL_OK = 17;
	int dropBall(int pos);
	
	public static final int ENEMY_DROP = 18;
	public Pair<Integer, Integer> waitDrop();
}


class NetworkMethod implements NetworkInterface {
	~~~~
}