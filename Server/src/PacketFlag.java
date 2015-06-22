class PacketFlag {
	public static final int INVALID_REQ = -2;
	public static final int INVALID_RES = -3;
	
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
	// time over
	public static final int WAIT_USER_TIMEOVER_RES = 7777;
	
	// x
	public static final int EXIT_ROOM_REQ = 14;
	// x
	public static final int EXIT_ROOM_RES = 15;
	// x
	public static final int GAME_READY_REQ = 1111;
	// x
	public static final int GAME_READY_RES = 2222;
	public static final int GAME_READY_FAIL_RES = 13579;
	// x
	public static final int WAIT_GAMESTART_REQ = 16;
	// x
	public static final int WAIT_GAMESTART_RES = 17;
	// x
	public static final int WAIT_GAMESTART_TIMEOVER_RES = 17777;
	// int
	public static final int DROP_BALL_REQ = 18;
	// x
	public static final int DROP_BALL_RES = 19;
	// x
	public static final int ENEMY_DROP_BALL_REQ = 20;	// wait enemy drop ball
	// int
	public static final int ENEMY_DROP_BALL_RES = 21;
	// time over
	public static final int ENEMY_DROP_BALL_TIMEOVER_RES = 9999;
	
	// x
	public static final int ENEMY_EXIT = 30;
}