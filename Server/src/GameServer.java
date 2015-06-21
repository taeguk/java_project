import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class GameServer {
	final static int PORT = 7779;
	
	public static void main(String[] args) {
		ServerManager serverManager = new ServerManager();
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("[Log] Server is ready.");
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		while(true) {
			try {
				Socket socket = serverSocket.accept();
				// log
				System.out.println("[Log] someone connected!");
				ClientHandler clientHandler = new ClientHandler(socket, serverManager);
				clientHandler.start();
			} catch(Exception e) {
				System.out.println("[Log] Exception in main() : " + e.getMessage());
				continue;
			}
		}
	}
}
