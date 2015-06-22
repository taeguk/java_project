package Client;
import java.awt.*;
import java.awt.event.*;

import Common.NetworkInterface;
import Common.NetworkRoom;
import Common.NetworkRoomList;
import Common.PacketFlag;
import Common.Pair;
 
class WindowDestroyer extends WindowAdapter
{
    public void windowClosing(WindowEvent e) 
    {
        System.exit(0);
    }
}
public class FourMok extends Frame implements KeyListener, ActionListener{
	public static int KEY;
	
	public static NetworkInterface nt;
	
	private static int userNum;
	public static Room[] rooms;
	public static RoomList roomlists;
	private int roomCount ;
	
	public static CardLayout cld;		// cardlayout
	private Panel ENTER;		// ù ȭ��
	private Panel E_top;		// ù ȭ�� �� �κ�
	private Panel E_bot;		// ù ȭ�� �Ʒ� �κ�
	private Label E_subject;	// ����
	private TextField E_nic;	// nickname
	private Button E_b;			// ���� 
	private String login;		// 
	
	private Panel ROOM;			// room list ȭ��
	private CardLayout Roomcard;  // roomlist card
	private Panel List;			// room list
	private static Button[] bt;
	private Panel listone;		// first page
	private Panel listtwo;		// second page
	private Panel info;			// up,down, make, information
	private Panel button;		// up,down,make
	private Button reset;
	private Button makeroom;	
	private Label nameLabel;
	private Panel roominfo;		// ������ ���
	private static Label infoLabel1;
	private static Label infoLabel2;
	
	private Panel WAIT;
	private Label waitLabel;
	
	public static Panel GAME;	// room( game ���� ȭ��)
	private Game game;			// game �Լ�
	public static String user1;
	public static String user2;
	
	public FourMok(String str){
		super(str);
		roomCount = 0;
		nt = new NetworkMethod();
		cld = new CardLayout();
		bt = new Button[6];
		setLayout(cld);
		// 1 ù ȭ��
		ENTER = new Panel();
		ENTER.setBackground(Color.GRAY);
		ENTER.setLayout(new BorderLayout());
		//// 1-1 ���� 
		E_top = new Panel();
		E_top.setBackground(Color.GRAY);
		E_subject = new Label("4Binggo!");
		E_subject.setFont(new Font("Serif",Font.ITALIC,100));
		E_top.add(E_subject);
		//// 1-2 �г���
		E_bot = new Panel();
		E_nic = new TextField(10);
		E_b = new Button("����");
		E_bot.add(E_nic );
		E_bot.add(E_b);
		////
		ENTER.add("Center", E_top);
		ENTER.add("South",E_bot);
		//1 end
		
		//2 Room
		ROOM = new Panel();
		ROOM.setLayout(new BorderLayout());
		////2-1 roomlist
		Roomcard = new CardLayout();
		List = new Panel();
		List.setLayout(Roomcard);
		listone = new Panel();
		listone.setLayout(new GridLayout(3,2));
		for(int i = 0 ; i < 6; i++){	//	6��
			listone.add(bt[i] = new Button("empty"));
			bt[i].setFont(new Font("Serif",Font.BOLD,30));
		}
		List.add(listone);
		
		info = new Panel();
		info.setLayout(new BorderLayout());
		info.setBackground(Color.DARK_GRAY);
		button = new Panel();
		button.setBackground(Color.LIGHT_GRAY);
		Font ft = new Font("Serif",Font.BOLD,50);
		reset = new Button("RESET");
		makeroom = new Button("MAKE");
		reset.setFont(ft);
		makeroom.setFont(ft);
		button.add(reset); button.add(makeroom);

		nameLabel = new Label();
		roominfo = new Panel();
		
		roominfo.setLayout(new BorderLayout());
		roominfo.setBackground(Color.LIGHT_GRAY);
		infoLabel1 = new Label("User number: ");
		infoLabel2 = new Label("Room number: ");
		infoLabel1.setFont(new Font("Serif", Font.BOLD, 30));
		infoLabel2.setFont(new Font("Serif", Font.BOLD, 30));
		roominfo.add("North",infoLabel1);
		roominfo.add("Center",infoLabel2);
		
		info.add("North",button);
		info.add(nameLabel);
		info.add("South",roominfo);
		////
		ROOM.add("Center", List);
		ROOM.add("East", info);
		
		//2 end
		
		WAIT = new Panel();
		WAIT.setBackground(Color.GRAY);
		waitLabel = new Label("Wait..");
		waitLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC , 100));
		WAIT.add(waitLabel);
		
		//3 Game
		GAME = new Panel();
		GAME.setLayout(new GridLayout(1,1,0,0));
		//3 end
		
		// card �߰�
		add(ENTER, "enter");
		add(ROOM , "room");
		add(WAIT, "wait");
		add(GAME, "game");
		
		for(int i = 0 ; i < 6; i++){
			bt[i].addActionListener(this);
		}
		E_b.addActionListener(this);
		reset.addActionListener(this);
		makeroom.addActionListener(this);
		cld.show(this,"ENTER");
	}
	public void keyPressed(KeyEvent e) {
		KEY = e.getKeyCode();
		System.out.println(KEY);
		switch(KEY){
			case KeyEvent.VK_LEFT: //37
				game.repaint();
				break;
			case KeyEvent.VK_RIGHT: //39
				game.repaint();
				break;
			case KeyEvent.VK_SPACE: //32
				game.repaint();
				break;
			case KeyEvent.VK_ESCAPE:
				game.repaint();
				break;
		}
	}
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
	public void actionPerformed(ActionEvent e) {
		String get = e.getActionCommand();
		System.out.println(get);
		if( get.equals("����")){
			int enterchk;	// ���� 
			enterchk = nt.enterLobby(E_nic.getText());
			System.out.println(enterchk);
			// �г��� ����
			if( enterchk == nt.NICKNAME_OK){
				int getroomchk ;
				cld.next(this);
				login = E_nic.getText();
				nameLabel.setFont(new Font("Serif", Font.BOLD+Font.ITALIC,100));
				nameLabel.setText((login));
				user1 = login;	// �г��� ����
				button.requestFocus();
				newlist();
			}
			else if( enterchk == nt.NETWORK_ERROR){
				System.out.println("Net error");
			}
			else if( enterchk == nt.NICKNAME_DUP){
				System.out.println("du");
			}
			else if( enterchk == nt.NICKNAME_INVALID ){
				System.out.println("invalid");
			}
			else if( enterchk == nt.INVALID_REQ || enterchk == nt.INVALID_RES){
				System.out.println("INVALID");
			}
		}
		else if( get.equals("RESET")){
			newlist();
		}
		else if( get.equals("MAKE")){
			int makechk;
			makechk = nt.makeRoom(Integer.toString(roomCount+1), NetworkRoom.ONE_WINS);
			if( makechk == nt.MAKE_ROOM_OK ){
				cld.next(this);
				Pair<Integer, String> w ;
				int waitchk;
				while(true){
					w = nt.waitUser();
					waitchk = w.first();
					if( waitchk == nt.USER_ENTER){
						roomCount++;
						game = new Game(this,'r');
						GAME.add(game);
						cld.next(this);
						GAME.setFocusable(true);
						GAME.requestFocus();
						GAME.addKeyListener(this);
						user2 = w.second();
						game.state = game.ST_MAIN;
						game.repaint();
						break;
					}
					else if(waitchk == nt.NETWORK_ERROR ){
						System.out.println("Net Error");
						cld.previous(this);
						cld.previous(this);
						break;
					}
					else if( waitchk == nt.INVALID_REQ || waitchk == nt.INVALID_RES){
						System.out.println("INVALID");
						cld.previous(this);
						cld.previous(this);
						break;
					}
				}
			}
			else if( makechk == nt.NETWORK_ERROR ){
				System.out.println("Net error");
				cld.previous(this);
			}
			else if( makechk == nt.INVALID_REQ || makechk == nt.INVALID_RES){
				System.out.println("INVALID");
				cld.previous(this);
			}
		}
		else{
			int enterRoomchk;
			enterRoomchk = nt.enterRoom(Integer.parseInt(get.replaceAll("[^0-9]", "")));//
			if(enterRoomchk == nt.ENTER_ROOM_OK){
				game = new Game(this,'b');
				GAME.add(game);
				cld.next(this);
				cld.next(this);
				GAME.setFocusable(true);
				GAME.requestFocus();
				GAME.addKeyListener(this);
				game.state = game.ST_MAIN;
				game.repaint();
				
			}
			else if( enterRoomchk == nt.NETWORK_ERROR){
				System.out.println("Net Error");
				cld.previous(this);
			}
			else if( enterRoomchk == nt.INVALID_REQ || enterRoomchk == nt.INVALID_RES){
				System.out.println("INVALID");
				cld.previous(this);
			}
		}
	}
	public static void newlist(){
		Pair<Integer, NetworkRoomList> rl = nt.getRoomList();
		int getroomchk = rl.first();
		if( getroomchk == nt.GET_ROOM_LIST_OK){	// roomlist
			roomlists = new RoomList(rl.second().getRoomNum(), rl.second().getRooms());
			userNum = nt.getUserNum();
			userNum = nt.getUserNum();
			System.out.println("-------------");
			System.out.println(userNum);
			infoLabel1.setText("User number: " + Integer.toString(userNum));
		}
		infoLabel2.setText("Room number: " + Integer.toString(roomlists.roomNum));
		Font fff = new Font("Serif",Font.BOLD,30);
		for(int i = 0 ; i < roomlists.roomNum ; i++){
			bt[i].setFont(fff);
			bt[i].setLabel("roomId: " + Integer.toString(rooms[i].roomId));
			if(rooms[i].isOpened == true){
				bt[i].setBackground(new Color(30,144,255));
			}
			else{
				bt[i].setBackground(new Color(199,21,133));
			}
		}
		for(int j = roomlists.roomNum ; j < 6; j++ ){
			bt[j].setFont(fff);
			bt[j].setLabel("Empty");
			bt[j].setBackground(Color.LIGHT_GRAY);
		}
	}
	public static void main(String[] args) {
		FourMok f = new FourMok("4BINGGO");
		f.setSize(800, 800);
		WindowDestroyer listener = new WindowDestroyer();  
		f.addWindowListener(listener);
		f.setVisible(true);
	}
}

class Room{
	int roomId;
	String[] guests;
	boolean isOpened;
	Room(int roomId, String[] guests,boolean isOpened){
		this.roomId = roomId;
		this.guests = guests;
		this.isOpened = isOpened;
	}
}

class RoomList{
	int roomNum;	// room ����
	RoomList(int roomNum, NetworkRoom[] rooms){
		this.roomNum = roomNum;
		FourMok.rooms = new Room[rooms.length];
		for(int i = 0 ; i < rooms.length; i++){
			FourMok.rooms[i] = new Room(rooms[i].getRoomId(), rooms[i].getGuests(), rooms[i].getIsOpened());
		}
	}
}