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
		System.out.println("[Log] " + this.getName() + " run!");
		try {
			while(true) {
				int dataLen = getPacket();
				if(dataLen < 4) {
					// error handling.
					continue;
				}
				
				byte[] data = getData(dataLen);

				processPacket(data);	
			}
		} catch(Exception e) {
			serverManager.terminateUser(socket);
			System.out.println("[Log] ["+this.getName()+"]  " + " terminated. ("+e.getMessage()+")");
		}
	}
	
	private void invalidRequest() throws Exception {
		System.out.println("[Log] ["+this.getName()+"]  Invalid Request!!");
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.INVALID_REQ);
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
				System.out.println("[Log] ["+this.getName()+"]  (invalidRequest) **"
						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("throw in invalidRequest()");
		}
	}
	
	private void enterLobby(String userName) throws Exception {
		int result = serverManager.enterLobby(socket, userName);
		System.out.println("[Log] ["+this.getName()+"]  (enterLobby) result : " + result);
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.ENTER_LOBBY_RES);
				resDataOutputStream.writeInt(result);
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
				
//				System.out.println("Debug (enterLobby) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in enterLobby()");
		}
	}
	
	private void getUserNum() throws Exception {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		int userNum = serverManager.getUserNum();
		
		System.out.println("[Log] ["+this.getName()+"]  (getUserNum) userNum : " + userNum);
		
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.GET_USERNUM_RES);
				resDataOutputStream.writeInt(userNum);
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
				
//				System.out.println("[Log] ["+this.getName()+"]  (getUserNum) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in getUserNum()");
		}
	}
	
	private void getRoomList() throws Exception {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		NetworkRoomList roomList = serverManager.getRoomList();
		
		System.out.println("[Log] ["+this.getName()+"]  (getRoomList) roomNum : " + roomList.getRoomNum());
		
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.GET_ROOMLIST_RES);
				resDataOutputStream.writeObject(roomList);		// roomList serialization problem notice!!
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
				
//				System.out.println("[Log] ["+this.getName()+"]  (getRoomList) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in getRoomList()");
		}
	}
	
	private void makeRoom(String roomName, int gameMode) throws Exception {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		serverManager.makeRoom(socket, roomName, gameMode);		// do what if fail?
		
		System.out.println("[Log] ["+this.getName()+"]  (makeRoom) : MAKE_ROOM_RES");
		
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.MAKE_ROOM_RES);
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
//				System.out.println("[Log] ["+this.getName()+"]  (makeRoom) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in makeRoom()");
		}
	}
	
	private void enterRoom(int roomId) throws Exception {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		int result = serverManager.enterRoom(socket, roomId);
		
		System.out.println("[Log] ["+this.getName()+"]  (enterRoom) result : " + result);
		
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.ENTER_ROOM_RES);
				resDataOutputStream.writeInt(result);
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
//				System.out.println("[Log] ["+this.getName()+"]  (enterRoom) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in enterRoom()");
		}
	}
	
	private void waitUser() throws Exception {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
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
				if(!isTimeOver) {
					resDataOutputStream.writeInt(PacketFlag.WAIT_USER_RES);
					resDataOutputStream.writeObject(guestName);
					System.out.println("[Log] ["+this.getName()+"]  (waitUser) : WAIT_USER_RES");
				} else {
					resDataOutputStream.writeInt(PacketFlag.WAIT_USER_TIMEOVER_RES);
					System.out.println("[Log] ["+this.getName()+"]  (waitUser) : WAIT_USER_TIMEOVER_RES");
				}
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
//				System.out.println("[Log] ["+this.getName()+"]  (waitUser) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in waitUser()");
		}
	}
	
	private void exitRoom() throws Exception {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		if(!serverManager.isInRoom(socket)) {
			invalidRequest();
			return;
		}
		
		serverManager.exitRoom(socket);
		
		System.out.println("[Log] ["+this.getName()+"]  (exitRoom) : EXIT_ROOM_RES");
	
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.EXIT_ROOM_RES);
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
//				System.out.println("[Log] ["+this.getName()+"]  (exitRoom) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in exitRoom()");
		}
	}
	
	private void readyGame() throws Exception {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		if(!serverManager.isInRoom(socket)) {
			invalidRequest();
			return;
		}
		
		serverManager.readyGame(socket);
		
		System.out.println("[Log] ["+this.getName()+"]  (readyGame) : GAME_READY_RES");
	
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.GAME_READY_RES);
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
//				System.out.println("[Log] ["+this.getName()+"]  (readyGame) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in readyGame()");
		}
	}
	
	private void waitGameStart() throws Exception {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		if(!serverManager.isInRoom(socket)) {
			invalidRequest();
			return;
		}
		
		boolean isTimeOver = true;
		final int timeout = 3000;
		final int gap = 500;
		int time = 0;
		
		do {
			try {
				Thread.sleep(gap);
			} catch (InterruptedException e) {
				// error handling -> maybe connection quit?
			}
			time += gap;
			if(serverManager.isGameStart(socket)) {
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
				if(!isTimeOver) {
					resDataOutputStream.writeInt(PacketFlag.WAIT_GAMESTART_RES);
					System.out.println("[Log] ["+this.getName()+"]  (waitGameStart) : WAIT_GAMESTART_RES");
				} else {
					resDataOutputStream.writeInt(PacketFlag.WAIT_GAMESTART_TIMEOVER_RES);
					System.out.println("[Log] ["+this.getName()+"]  (waitGameStart) : WAIT_GAMESTART_TIMEOVER_RES");
				}
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
//				System.out.println("[Log] ["+this.getName()+"]  (waitGameStart) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in waitGameStart()");
		}
	}
	
	private void dropBall(int pos) throws Exception {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		if(!serverManager.isInRoom(socket)) {
			invalidRequest();
			return;
		}
		
		if(!serverManager.isGameStart(socket)) {
			invalidRequest();
			return;
		}
		
		if(!serverManager.canDropBall(socket, pos)) {
			invalidRequest();
			return;
		}
		
		serverManager.dropBall(socket, pos);
		
		System.out.println("[Log] ["+this.getName()+"]  (dropBall) My dropBall pos : " + pos);
	
		try {
			synchronized(socket) {
				DataOutputStream resStream = new DataOutputStream(socket.getOutputStream());
				byte[] resData;
				ByteArrayOutputStream resDataStream = new ByteArrayOutputStream();
				ObjectOutputStream resDataOutputStream = new ObjectOutputStream(resDataStream);
				resDataOutputStream.writeInt(PacketFlag.DROP_BALL_RES);
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
//				System.out.println("[Log] ["+this.getName()+"]  (dropBall) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in dropBall()");
		}
	}
	
	private void waitEnemyDropBall() throws Exception {
		if(!serverManager.isLogin(socket)) {
			invalidRequest();
			return;
		}
		
		if(!serverManager.isInRoom(socket)) {
			invalidRequest();
			return;
		}
		
		if(!serverManager.isGameStart(socket)) {
			invalidRequest();
			return;
		}
		
		boolean isTimeOver = true;
		final int timeout = 3000;
		final int gap = 500;
		int time = 0;
		int pos = -1;
		
		do {
			try {
				Thread.sleep(gap);
			} catch (InterruptedException e) {
				// error handling -> maybe connection quit?
			}
			time += gap;
			if((pos=serverManager.isEnemyDropBall(socket)) >= 0) {
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
				if(!isTimeOver) {
					resDataOutputStream.writeInt(PacketFlag.ENEMY_DROP_BALL_RES);
					resDataOutputStream.writeInt(pos);
					System.out.println("[Log] ["+this.getName()+"]  (waitEnemyDropBall) : ENEMY_DROP_BALL_RES (pos : "+pos+")");
				} else {
					resDataOutputStream.writeInt(PacketFlag.ENEMY_DROP_BALL_TIMEOVER_RES);
					System.out.println("[Log] ["+this.getName()+"]  (waitEnemyDropBall) : ENEMY_DROP_BALL_TIMEOVER_RES");
				}
				resDataOutputStream.flush();
				resData = resDataStream.toByteArray();
				resDataOutputStream.close();
				resStream.writeInt(resData.length);
				resStream.write(resData);
				resStream.flush();
//				System.out.println("[Log] ["+this.getName()+"]  (waitGameStart) **"
//						+ " resData.length : " + resData.length + "  resData : " + new String(resData));
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in waitEnemyDropBall()");
		}
	}
	
	private void processPacket(byte[] data) throws Exception {
		ObjectInputStream stream = null;
		int flag = -8888;

		try {
			stream = new ObjectInputStream(new ByteArrayInputStream(data));
			flag = stream.readInt();
			
//			System.out.println("[Log] ["+this.getName()+"]  (processPacket) ** flag : "+flag+" *** " + new String(data));
			
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
				waitGameStart();
				break;
				
			case PacketFlag.DROP_BALL_REQ:
				int pos = stream.readInt();
				dropBall(pos);
				break;
				
			case PacketFlag.ENEMY_DROP_BALL_REQ:
				waitEnemyDropBall();
				break;
				
			default:
				// error handling.
			}
		} catch(Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in processPacket()");
		}
	}

	// return data size
	private int getPacket() throws Exception {
		DataInputStream reqStream;
		int dataLen = 0;
		try {
			synchronized(socket) {
				reqStream = new DataInputStream(socket.getInputStream());
				dataLen = reqStream.readInt();
//				System.out.println("[Log] ["+this.getName()+"]  (getData) ** " + dataLen);
				
			}
		} catch (Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in getPacket()");
		}
		return dataLen;
	}
	
	private byte[] getData(int dataLen) throws Exception {
		byte[] data = new byte[dataLen];
		try {
			synchronized(socket) {
				DataInputStream stream = new DataInputStream(socket.getInputStream());
				int readLen = 0;
				int readSz=-8888;
				
				while(readLen < dataLen && (readSz=stream.read(data,readLen,dataLen-readLen)) != -1) {
					readLen += readSz;
				}
				
//				System.out.println("[Log] ["+this.getName()+"]  (getData) **"
//						+ " dataLen : " + dataLen + " readLen : " + readLen + " readSz : " + readSz);
				if(readLen < dataLen) {
					return null;	// why use network interface? must modify it !
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			throw new Exception("throw in getData()");
		}
		return data;
	}
}
// dongihwa..