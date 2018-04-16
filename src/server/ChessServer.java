package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream.PutField;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;

/**
 * 显示服务器及用户信息的Panel类
 */
class MessageServerPanel extends Panel {
	TextArea messageBoard = new TextArea("", 22, 50,
			TextArea.SCROLLBARS_VERTICAL_ONLY);

	JLabel statusLabel = new JLabel("当前连接数:", Label.LEFT);

	JPanel boardPanel = new JPanel();// 主显示区Panel

	JPanel statusPanel = new JPanel();// 连接状态Panel

	MessageServerPanel() {
		setSize(350, 300);
		setBackground(new Color(204, 204, 204));
		setLayout(new BorderLayout());
		boardPanel.setLayout(new FlowLayout());
		boardPanel.setSize(210, 210);
		statusPanel.setLayout(new BorderLayout());
		statusPanel.setSize(210, 50);
		boardPanel.add(messageBoard);
		statusPanel.add(statusLabel, BorderLayout.WEST);
		add(boardPanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.NORTH);
	}
}

/**
 * 服务器线程,主要用于服务器与客户端的通信
 */
class ServerThread extends Thread {
	Socket clientSocket;

	Hashtable<Socket, DataOutputStream> clientDataHash;// Socket与发送数据的流的映射

	Hashtable<Socket, String> clientNameHash;// Socket与用户名的映射

	Hashtable<String, String> chessPeerHash;// 对弈的两个客户端用户名的映射
	
	Hashtable<String, String> room;//每个房间的对弈情况，key为房间号，value为 客户端1 客户端2，空格隔开

	MessageServerPanel server;

	boolean isClientClosed = false;

	/**
	 * 服务器端线程的构造函数，用于初始化一些对象。
	 */
	ServerThread(Socket clientSocket, Hashtable<Socket, DataOutputStream> clientDataHash,
			Hashtable<Socket, String> clientNameHash, Hashtable<String, String> chessPeerHash,
			Hashtable<String, String> room,
			MessageServerPanel server) {
		this.clientSocket = clientSocket;
		this.clientDataHash = clientDataHash;
		this.clientNameHash = clientNameHash;
		this.chessPeerHash = chessPeerHash;
		this.room = room;
		this.server = server;
	}

	/**
	 * 对客户端发来的消息处理的函数，处理后转发回客户端。处理消息的过程比较复杂， 要针对很多种情况分别处理。
	 */
	public void messageTransfer(String message) {
		System.out.println("收到消息，信息为："+message);
		String clientName ,peerName;
		//登录，确认是否已经有人占用了这个昵称
		if (message.startsWith("/login ")) {
			clientName = message.substring("/login ".length());
			System.out.println(clientName);
			if (clientNameHash.containsValue(clientName)) {
				//如果已经存在该用户，则出现冲突，不能用这个用户名
				//因为用户名冲突，所以通过clientDataHash表，从连接时就记录的数据流来查找这个用户端，返回其错误信号
				Feedback("/login error");//该函数是通过clientDataHash找到自己的socket，返回数据
				//peerTalk(clientName,"/login error");
			}else {
				//否则，顺利记录该用户进用户列表
				DataOutputStream outData = null;
				try {
					outData = new DataOutputStream(clientSocket
							.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
				synchronized (clientDataHash) {
					clientDataHash.put(clientSocket, outData);
				}
				synchronized (clientNameHash) {
					clientNameHash.put(clientSocket, message.substring("/login ".length()));
				}
				Feedback("/login ok");
//				peerTalk(clientName,"/login ok");//选择对象发送消息
			}
		}
		//有成员登录成功，群发最新用户列表给客户端更新用户列表
		else if (message.startsWith("/mainui ")){
			String opera=message.substring("/mainui ".length());
			if (opera.equals("allonline")) {
				//如果是要获取所有用户列表的命令，广播在线用户
				firstCome();
			}
		}
		//客户端选择了一个房间，服务器将其记录占用位置，并广播给所有用户最新的房间情况
		else if (message.startsWith("/room ")) {
			String []mm = message.substring("/room ".length()).split(" ");
			String roomId = mm[0];
			String leftUser = mm[1];
			String rightUser = mm[2];
			//如果该房间还没有人，则直接加入房间，另一个位置置为"null"
			if (!room.containsKey(roomId)) {
				synchronized (room) {
					room.put(roomId, leftUser+" "+ rightUser);
				}
				Feedback("/room ok");
			}else{//如果有人
				String [] users = room.get(roomId).split(" ");
				if ((users[0]!="null" && users[1]!="null") ||
						(leftUser!="null" && users[0]!="null") || 
						(rightUser!="null" && users[1]!="null")) {
					//该房间位置有人了，且房间有两个人，或者有人的位置和用户选的位置一样，则出错
					Feedback("/room error");
				}
				//用户占领的左位置正好没人，另一个位置没人(因为上一个已经把两个都有人的用了，所以这个就肯定是只有一个有人
				else if ((leftUser != "null" && users[0]=="null" ) ) {//如果左边没人
					synchronized (room) {
						room.replace(roomId, users[0]+" "+users[1],  leftUser+" "+  users[1]);
					}
					Feedback("/room ok");
				}
				else if (rightUser!="null" && users[1]=="null") {//如果右边没人
					synchronized (room) {
						room.replace(roomId, users[0]+" "+users[1],  users[0]+" "+  rightUser);
					}
					Feedback("/room ok");
				}
			}
		}
		
		
	}

	/**
	 * 发送公共消息的函数，将消息向每个客户端都发送一份
	 */
	public void publicTalk(String publicTalkMessage) {

		synchronized (clientDataHash) {
			for (Enumeration<DataOutputStream> enu = clientDataHash.elements(); enu
					.hasMoreElements();) {
				DataOutputStream outData = (DataOutputStream) enu.nextElement();
				try {
					outData.writeUTF(publicTalkMessage);
				} catch (IOException es) {
					es.printStackTrace();
				}
			}
		}

	}

	/**
	 * 选择对象发送消息，参数peerTalk为发送的用户名，后面的参数为发送的消息
	 */
	public boolean peerTalk(String peerTalk, String talkMessage) {
		//
		System.out.println("发送给用户"+peerTalk+"消息为："+talkMessage);
		for (Enumeration<Socket> enu = clientDataHash.keys(); enu.hasMoreElements();) {
			Socket userClient = (Socket) enu.nextElement();
			// 找到发送消息的对象，获取它的输出流以发送消息
			if (peerTalk.equals((String) clientNameHash.get(userClient))
					&& !peerTalk.equals((String) clientNameHash
							.get(clientSocket))) {
				synchronized (clientDataHash) {
					DataOutputStream peerOutData = (DataOutputStream) clientDataHash
							.get(userClient);
					try {
						peerOutData.writeUTF(talkMessage);
					} catch (IOException es) {
						es.printStackTrace();
					}
				}
				Feedback(talkMessage);
				return (false);
			}
			// 如果是发给自己的，直接回馈
			else if (peerTalk.equals((String) clientNameHash.get(clientSocket))) {
				Feedback(talkMessage);
				return (false);
			}
		}

		return (true);

	}

	/**
	 * 此函数也用于选择发送消息，但不能发送给自己。
	 */
	public boolean chessPeerTalk(String chessPeerTalk, String chessTalkMessage) {

		for (Enumeration<Socket> enu = clientDataHash.keys(); enu.hasMoreElements();) {
			Socket userClient = (Socket) enu.nextElement();

			if (chessPeerTalk.equals((String) clientNameHash.get(userClient))
					&& !chessPeerTalk.equals((String) clientNameHash
							.get(clientSocket))) {
				synchronized (clientDataHash) {
					DataOutputStream peerOutData = (DataOutputStream) clientDataHash
							.get(userClient);
					try {
						peerOutData.writeUTF(chessTalkMessage);
					} catch (IOException es) {
						es.printStackTrace();
					}
				}
				return (false);
			}
		}
		return (true);
	}

	/**
	 * 用于处理消息回馈的函数
	 */
	public void Feedback(String feedbackString) {
		synchronized (clientDataHash) {
			DataOutputStream outData = (DataOutputStream) clientDataHash
					.get(clientSocket);
			try {
				outData.writeUTF(feedbackString);
			} catch (Exception eb) {
				eb.printStackTrace();
			}
		}

	}

	/**
	 * 获取用户列表的函数，此函数读取clientNameHash获取用户列表， 然后将其保存在一个字符串userList中。
	 */
	public String getUserList() {
		String userList = "/userlist";

		for (Enumeration<String> enu = clientNameHash.elements(); enu.hasMoreElements();) {
			userList = userList + " " + (String) enu.nextElement();
		}
		return userList;
	}

	/**
	 * 给出HashTable和值对象，获取相对应得键值的函数。
	 */
	public Object getHashKey(Hashtable<String, String> targetHash, Object hashValue) {
		Object hashKey;
		for (Enumeration<String> enu = targetHash.keys(); enu.hasMoreElements();) {
			hashKey = (Object) enu.nextElement();
			if (hashValue.equals((Object) targetHash.get(hashKey)))
				return (hashKey);
		}
		return (null);
	}
	
	/**
	 * 用于在软件刚登陆的时候把所有的在线用户列表给显示出来
	 */
	public void firstCome() {
		publicTalk(getUserList());
//		Feedback("/yourname " + (String) clientNameHash.get(clientSocket));
//		Feedback("Java五子棋聊天客户端");	
	}

	/**
	 * 用于和客户端断开的函数。
	 */
	public void clientClose() {
		server.messageBoard.append("用户断开:" + clientSocket + "\n");
		// 如果是游戏客户端主机
		synchronized (chessPeerHash) {
			if (chessPeerHash.containsKey(clientNameHash.get(clientSocket))) {
				chessPeerHash.remove((String) clientNameHash.get(clientSocket));
			}
			if (chessPeerHash.containsValue(clientNameHash.get(clientSocket))) {
				chessPeerHash.put((String) getHashKey(chessPeerHash,
						(String) clientNameHash.get(clientSocket)),
						"tobeclosed");
			}
		}
		// 将保留的HashTable里的数据清除
		synchronized (clientDataHash) {
			clientDataHash.remove(clientSocket);
		}
		synchronized (clientNameHash) {
			clientNameHash.remove(clientSocket);
		}
		if (!"/userlist".equals(getUserList())) {
			publicTalk(getUserList());
		}
		// 计算当前连接数，并显示在状态框中
		server.statusLabel.setText("当前连接数:" + clientDataHash.size());
		try {
			clientSocket.close();
		} catch (IOException exx) {
		}

		isClientClosed = true;

	}

	public void run() {
		DataInputStream inData;
		synchronized (clientDataHash) {
			server.statusLabel.setText("当前连接数:" + clientDataHash.size());
		}
		try {
			inData = new DataInputStream(clientSocket.getInputStream());
//			firstCome();
			while (true) {
				String message = inData.readUTF();
				messageTransfer(message);
			}
		} catch (IOException esx) {
		} finally {
			if (!isClientClosed) {
				clientClose();
			}
		}
	}

}

/**
 * @author wufenghanren 服务器端框架类
 */
public class ChessServer extends Frame implements ActionListener {

	JButton messageClearButton = new JButton("清除显示");

	JButton serverStatusButton = new JButton("服务器状态");

	JButton serverOffButton = new JButton("关闭服务器");

	Panel buttonPanel = new Panel();

	MessageServerPanel server = new MessageServerPanel();

	ServerSocket serverSocket;

	Hashtable<Socket, DataOutputStream> clientDataHash = new Hashtable<Socket, DataOutputStream>(50);

	Hashtable<Socket, String> clientNameHash = new Hashtable<Socket, String>(50);

	Hashtable<String, String> chessPeerHash = new Hashtable<String, String>(50);
	Hashtable<String, String> room = new Hashtable<String, String>(50);//每个房间的对弈情况，key为房间号，value为 客户端1 客户端2，空格隔开

	/**
	 *框架类的构造函数
	 */
	ChessServer() {
		super("Java五子棋服务器");
		setBackground(new Color(204, 204, 204));

		buttonPanel.setLayout(new FlowLayout());
		messageClearButton.setSize(60, 25);
		buttonPanel.add(messageClearButton);
		messageClearButton.addActionListener(this);
		serverStatusButton.setSize(75, 25);
		buttonPanel.add(serverStatusButton);
		serverStatusButton.addActionListener(this);
		serverOffButton.setSize(75, 25);
		buttonPanel.add(serverOffButton);
		serverOffButton.addActionListener(this);

		add(server, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		// 退出窗口的监听器
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setVisible(true);
		setSize(400, 450);
		setResizable(false);
		validate();
		try {
			makeMessageServer(4331, server);
		} catch (Exception e) {
			System.out.println("e");
		}
	}

	/**
	 * 初始化消息服务器的类
	 */
	public void makeMessageServer(int port, MessageServerPanel server)
			throws IOException {
		Socket clientSocket;
		this.server = server;

		try {
			// 输出服务器的启动信息
			serverSocket = new ServerSocket(port);
			server.messageBoard.setText("服务器开始于:"
					+ serverSocket.getInetAddress().getLocalHost() + ":"
					+ serverSocket.getLocalPort() + "\n");

			while (true) {
				clientSocket = serverSocket.accept();
				server.messageBoard.append("用户连接:" + clientSocket + "\n");
				
				//用户与服务器连接之后，clientDataHash即用户socket和其数据流的对应先进行记录，
				//然后再判断其登录用的用户名是否与已有的重复了，即使重复了也可以通过这个数据流来返回通知去来源端修改，
				//而不是用错误的用户名去查找用户名和socket表的另一正常方socket
				DataOutputStream outData = new DataOutputStream(clientSocket.getOutputStream());
				clientDataHash.put(clientSocket, outData);
				//为每一次信息开启一个线程做处理
				ServerThread thread = new ServerThread(clientSocket,
						clientDataHash, clientNameHash, chessPeerHash, room, server);
				thread.start();
			}
		} catch (IOException ex) {
			System.out.println("已经有服务器在运行. \n");
		}
	}

	/**
	 * 按钮的事件监听器，响应按钮点击事件
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == messageClearButton) {
			server.messageBoard.setText("");
		}
		// 当“服务器状态”按钮点击时，显示服务器状态
		if (e.getSource() == serverStatusButton) {
			try {
				server.messageBoard.append("服务器信息:"
						+ serverSocket.getInetAddress().getLocalHost() + ":"
						+ serverSocket.getLocalPort() + "\n");
			} catch (Exception ee) {
				System.out
						.println("serverSocket.getInetAddress().getLocalHost() error \n");
			}
		}
		if (e.getSource() == serverOffButton) {
			System.exit(0);
		}
	}

	public static void main(String args[]) {
		 SubstanceLookAndFeel sa = new SubstanceOfficeBlue2007LookAndFeel();			
			 try {
			 UIManager.setLookAndFeel(sa);
			 } catch (Exception e) {
			 e.printStackTrace();
			 }
		ChessServer ChessServer = new ChessServer();
	}
}