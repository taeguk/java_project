package Common;

import java.io.Serializable;

public class NetworkRoomList implements Serializable {
	private static final long serialVersionUID = 1L;
	private int roomNum;
	private NetworkRoom[] rooms;
	
	public NetworkRoomList(int roomNum, NetworkRoom[] rooms) {
		this.roomNum = roomNum;
		this.rooms = rooms;
	}
	
	public int getRoomNum() { return roomNum; }
	public NetworkRoom[] getRooms() { return rooms; }
}