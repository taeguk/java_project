import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientHandler extends Thread {
	ServerManager serverManager;
	Socket socket;
	
	ClientHandler(Socket socket, ServerManager serverManager) {
		this.socket = socket;
		this.serverManager = serverManager;
	}
	
	public void run() {
		
		while(true) {
			int dataLen = getPacket();
			if(dataLen < 4) {
				// error handling.
				continue;
			}
			
			int flag;
			byte[] data = null;
			
			if(dataLen > 4) {
				data = new byte[dataLen-4];
				flag = getData(data);
			} else {
				flag = getData();
			}
			
			processPacket(flag, data);
			
		}
	}
	
	// code duplication problem notice!!
	
	private void invalidRequest() {
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.INVALID_REQ);
				resData = resDataStream.toByteArray();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
			}
		} catch(IOException e) {
			// error handling.
		}
	}
	
	private void enterLobby(String userName) {
		int result = serverManager.enterLobby(socket, userName);
		
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.ENTER_LOBBY_RES);
				resDataOutputStream.writeInt(result);
				resData = resDataStream.toByteArray();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
			}
		} catch(IOException e) {
			// error handling.
		}
	}
	
	private void getUserNum() {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		int userNum = serverManager.getUserNum();
		
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.GET_USERNUM_RES);
				resDataOutputStream.writeInt(userNum);
				resData = resDataStream.toByteArray();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
			}
		} catch(IOException e) {
			// error handling.
		}
	}
	
	private void getRoomList() {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		NetworkRoomList roomList = serverManager.getRoomList();
		
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.GET_ROOMLIST_RES);
				resDataOutputStream.writeObject(roomList);		// roomList serialization problem notice!!
				resData = resDataStream.toByteArray();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
			}
		} catch(IOException e) {
			// error handling.
		}
	}
	
	private void makeRoom(String roomName, int gameMode) {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		serverManager.makeRoom(socket, roomName, gameMode);		// do what if fail?
		
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.MAKE_ROOM_RES);
				resData = resDataStream.toByteArray();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
			}
		} catch(IOException e) {
			// error handling.
		}
	}
	
	private void enterRoom(int roomId) {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		int result = serverManager.enterRoom(socket, roomId);
		
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.ENTER_ROOM_RES);
				resDataOutputStream.writeInt(result);
				resData = resDataStream.toByteArray();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
			}
		} catch(IOException e) {
			// error handling.
		}
	}
	
	private void waitUser() {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		// check room admin
		if(!serverManager.isRoomAdmin(socket)) {
			invalidRequest();
			return;
		}
		
		boolean isTimeOver = true;
		final int timeout = 3000;
		final int gap = 500;
		int time = 0;
		String guestName;
		
		do {
			try {
				Thread.sleep(gap);
			} catch (InterruptedException e) {
				// error handling -> maybe connection quit?
			}
			time += gap;
			guestName = serverManager.getMyRoomGuest(socket);
			if(guestName != null) {
				isTimeOver = false;
				break;
			}
		} while(time < timeout);
	
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				if(isTimeOver) {
					resDataOutputStream.writeInt(PacketFlag.WAIT_USER_TIMEOVER_RES);
				} else {
					resDataOutputStream.writeInt(PacketFlag.WAIT_USER_TIMEOVER_RES);
					resDataOutputStream.writeObject(guestName);
				}
				resData = resDataStream.toByteArray();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
			}
		} catch(IOException e) {
			// error handling.
		}
	}
	
	private void exitRoom() {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		// check in room.
		if(!serverManager.isInRoom(socket)) {
			invalidRequest();
			return;
		}
		
		serverManager.exitRoom(socket);
	
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.EXIT_ROOM_RES);
				resData = resDataStream.toByteArray();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
			}
		} catch(IOException e) {
			// error handling.
		}
	}
	
	private void readyGame() {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		// check in room.
		if(!serverManager.isInRoom(socket)) {
			invalidRequest();
			return;
		}
		
		serverManager.readyGame(socket);
	
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.EXIT_ROOM_RES);
				resData = resDataStream.toByteArray();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
			}
		} catch(IOException e) {
			// error handling.
		}
	}
	
	private void processPacket(int flag, byte[] data) {
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new ByteArrayInputStream(data));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {	
			switch(flag) {
			case PacketFlag.ENTER_LOBBY_REQ:
				String userName = (String) stream.readObject();
				enterLobby(userName);
				break;
				
			case PacketFlag.GET_USERNUM_REQ:
				getUserNum();
				break;
				
			case PacketFlag.GET_ROOMLIST_REQ:
				getRoomList();
				break;
				
			case PacketFlag.MAKE_ROOM_REQ:
				String roomName = (String) stream.readObject();
				int gameMode = stream.readInt();
				makeRoom(roomName, gameMode);
				break;
				
			case PacketFlag.ENTER_ROOM_REQ:
				int roomId = stream.readInt();
				enterRoom(roomId);
				break;
			case PacketFlag.WAIT_USER_REQ:
				waitUser();
				break;
				
			case PacketFlag.EXIT_ROOM_REQ:
				exitRoom();
				break;
				
			case PacketFlag.GAME_READY_REQ:
				readyGame();
				break;
				
			case PacketFlag.WAIT_GAMESTART_REQ:
				break;
				
			case PacketFlag.DROP_BALL_REQ:
				break;
				
			case PacketFlag.ENEMY_DROP_BALL_REQ:
				break;
				
			default:
				// error handling.
			}
		} catch(Exception e) {
			// error handling!
		}
	}

	// return data size
	private int getPacket() {
		DataInputStream reqStream;
		int dataLen = 0;
		try {
			synchronized(socket) {
				reqStream = new DataInputStream(socket.getInputStream());
				dataLen = reqStream.readInt();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataLen;
	}
	
	private int getData() {
		int flag = 0;
		try {
			synchronized(socket) {
				DataInputStream stream = new DataInputStream(socket.getInputStream());
				flag = stream.readInt();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	
	private int getData(byte[] data) {
		int flag = 0;
		try {
			synchronized(socket) {
				DataInputStream stream = new DataInputStream(socket.getInputStream());
				flag = stream.readInt();
				int dataLen = data.length;
				int readLen = 0;
				int readSz;
				while(readLen < dataLen && (readSz=stream.read(data,readLen,dataLen-readLen)) != -1) {
					readLen += readSz;
				}
				if(readLen < dataLen) {
					return NetworkInterface.INVALID_REQ;	// why use network interface? must modify it !
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
}
// dongihwa..