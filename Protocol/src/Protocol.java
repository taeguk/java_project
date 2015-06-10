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

class PacketFlag {
	public static final int ENTER_LOBBY_REQ = 1;
	public static final int ENTER_LOBBY_RES = 2;
	public static final int GET_USERNUM_REQ = 3;
	public static final int GET_USERNUM_RES = 4;
	public static final int GET_ROOMLIST_REQ = 5;
	public static final int GET_ROOMLIST_RES = 6;
	public static final int MAKE_ROOM_REQ = 7;
	public static final int MAKE_ROOM_RES = 8;
	public static final int ENTER_ROOM_REQ = 9;
	public static final int ENTER_ROOM_RES = 10;
	public static final int WAIT_USER_REQ = 11;
	public static final int WAIT_USER_RES = 12;
	
	public static final int EXIT_ROOM_REQ = 14;
	public static final int EXIT_ROOM_RES = 15;
	public static final int WAIT_GAMESTART_REQ = 16;
	public static final int WAIT_GAMESTART_RES = 17;
	public static final int DROP_BALL_REQ = 18;
	public static final int DROP_BALL_RES = 19;
	public static final int ENEMY_DROP_BALL_REQ = 20;
	public static final int ENEMY_DROP_BALL_RES = 21;
	
	public static final int ENEMY_EXIT = 30;	
}

class Protocol {
	
}
