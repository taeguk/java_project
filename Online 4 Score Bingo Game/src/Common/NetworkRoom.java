package Common;

import java.io.Serializable;

public class NetworkRoom implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final int ONE_WINS = 1;	// ���ǽº�
	public static final int TWO_WINS = 2;	// 3�� 2����
	public static final int THREE_WINS = 3;	// 5�� 3����
	public static final int FOUR_WINS = 4;	// 7�� 4����
	
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