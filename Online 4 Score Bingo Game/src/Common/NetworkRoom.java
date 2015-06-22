package Common;

import java.io.Serializable;

public class NetworkRoom implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final int ONE_WINS = 1;	// ´ÜÆÇ½ÂºÎ
	public static final int TWO_WINS = 2;	// 3ÆÇ 2½ÂÁ¦
	public static final int THREE_WINS = 3;	// 5ÆÇ 3½ÂÁ¦
	public static final int FOUR_WINS = 4;	// 7ÆÇ 4½ÂÁ¦
	
	private int roomId;
	private String roomName;
	private String[] guests;
	private boolean isOpened;
	private int gameMode;
	
	public NetworkRoom(int roomId, String roomName, String[] guests, boolean isOpened, int gameMode) {
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