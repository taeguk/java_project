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
//test
class PacketFlag {
	// String class
	public static final int ENTER_LOBBY_REQ = 1;
	// int
	public static final int ENTER_LOBBY_RES = 2;
	// x
	public static final int GET_USERNUM_REQ = 3;
	// int
	public static final int GET_USERNUM_RES = 4;
	// x
	public static final int GET_ROOMLIST_REQ = 5;
	// NetworkRoomList class
	public static final int GET_ROOMLIST_RES = 6;
	// String class, int
	public static final int MAKE_ROOM_REQ = 7;
	// x
	public static final int MAKE_ROOM_RES = 8;
	// int
	public static final int ENTER_ROOM_REQ = 9;
	// int
	public static final int ENTER_ROOM_RES = 10;
	// x
	public static final int WAIT_USER_REQ = 11;
	// String class : nickname
	public static final int WAIT_USER_RES = 12;
	
	// x
	public static final int EXIT_ROOM_REQ = 14;
	// x
	public static final int EXIT_ROOM_RES = 15;
	// x
	public static final int WAIT_GAMESTART_REQ = 16;
	// x
	public static final int WAIT_GAMESTART_RES = 17;
	// int
	public static final int DROP_BALL_REQ = 18;
	// x
	public static final int DROP_BALL_RES = 19;
	// x
	public static final int ENEMY_DROP_BALL_REQ = 20;
	// int
	public static final int ENEMY_DROP_BALL_RES = 21;
	
	// x
	public static final int ENEMY_EXIT = 30;	
}

class Protocol {
	
}
