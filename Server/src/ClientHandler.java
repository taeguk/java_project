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
			DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
			byte[] resData;
			ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
			ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
			resDataOutputStream.writeInt(PacketFlag.INVALID_REQ);
			resData = resDataStream.toByteArray();
			resStream.writeInt(resData.length);
			resStream.write(resData);
			resStream.flush();
		} catch(IOException e) {
			// error handling.
		}
	}
	
	private void enterLobby(String userName) {
		int result = serverManager.addUser(socket, userName);
		
		try {
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
			DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
			byte[] resData;
			ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
			ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
			resDataOutputStream.writeInt(PacketFlag.MAKE_ROOM_RES);
			resData = resDataStream.toByteArray();
			resStream.writeInt(resData.length);
			resStream.write(resData);
			resStream.flush();
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
				break;
				
			case PacketFlag.WAIT_USER_REQ:
				break;
				
			case PacketFlag.EXIT_ROOM_REQ:
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
			reqStream = new DataInputStream(socket.getInputStream());
			dataLen = reqStream.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataLen;
	}
	
	private int getData() {
		int flag = 0;
		try {
			DataInputStream stream = new DataInputStream(socket.getInputStream());
			flag = stream.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	
	private int getData(byte[] data) {
		int flag = 0;
		try {
			DataInputStream stream = new DataInputStream(socket.getInputStream());
			flag = stream.readInt();
			int dataLen = data.length;
			int readLen = 0;
			int readSz;
			while(readLen < dataLen && (readSz=stream.read(data,readLen,dataLen-readLen)) != -1) {
				readLen += readSz;
			}
			if(readLen < dataLen) {
				return INVALID_REQ;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
}
