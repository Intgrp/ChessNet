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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;

import contrib.ch.randelshofer.quaqua.colorchooser.Crayons;

/**
 * 显示服务器及用户信息的Panel类
 */
class MessageServerPanel extends Panel {
	TextArea messageBoard = new TextArea("", 22, 50, TextArea.SCROLLBARS_VERTICAL_ONLY);

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
	// 对弈的两个客户端房间号和两个用户名的映射,正在下棋，则放进去。与roomHash一样，只是这里是进入准备状态
	Hashtable<String, String> chessPeerHash;

	// 每个房间的对弈情况，key为房间号，value为 客户端1，客户端2，逗号隔开
	// 为正在对战的列表，如果进来时有空的，则直接进入对战人员，如果不空，则进入观战列表
	Hashtable<String, String> roomHash;

	ConcurrentHashMap<String, Vector<String>> roomUserList;// 记录每个房间进去了的人列表格式为： 房间号 用户列表

	MessageServerPanel server;

	boolean isClientClosed = false;

	/**
	 * 服务器端线程的构造函数，用于初始化一些对象。
	 */
	ServerThread(Socket clientSocket, Hashtable<Socket, DataOutputStream> clientDataHash,
			Hashtable<Socket, String> clientNameHash, Hashtable<String, String> chessPeerHash,
			Hashtable<String, String> roomHash, ConcurrentHashMap<String, Vector<String>> roomUserList,
			MessageServerPanel server) {
		this.clientSocket = clientSocket;
		this.clientDataHash = clientDataHash;
		this.clientNameHash = clientNameHash;
		this.chessPeerHash = chessPeerHash;
		this.roomHash = roomHash;
		this.roomUserList = roomUserList;
		this.server = server;
	}

	/**
	 * 对客户端发来的消息处理的函数，处理后转发回客户端。处理消息的过程比较复杂， 要针对很多种情况分别处理。
	 */
	public void messageTransfer(String message) {
		System.out.println("收到消息，信息为：" + message);
		String clientName, peerName;
		/**
		 * 登录，确认是否已经有人占用了这个昵称
		 */
		if (message.startsWith("/login ")) {
			clientName = message.substring("/login ".length());
			System.out.println(clientName);
			if (clientName.equals("") || clientNameHash.containsValue(clientName)) {
				// 如果已经存在该用户，则出现冲突，不能用这个用户名
				// 因为用户名冲突，所以通过clientDataHash表，从连接时就记录的数据流来查找这个用户端，返回其错误信号
				Feedback("/login error");// 该函数是通过clientDataHash找到自己的socket，返回数据
				// peerTalk(clientName,"/login error");
			} else {
				// 否则，顺利记录该用户进用户列表
				DataOutputStream outData = null;
				try {
					outData = new DataOutputStream(clientSocket.getOutputStream());
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
				// peerTalk(clientName,"/login ok");//选择对象发送消息
			}
		}
		/**
		 * 有成员登录成功，群发最新用户列表给客户端更新用户列表
		 */
		else if (message.startsWith("/mainui ")) {
			String opera = message.substring("/mainui ".length());
			if (opera.equals("allonline")) {
				// 如果是要获取所有用户列表的命令，广播在线用户
				firstCome();
				// 函数处理后输出为 /mainui 桌号,user1,user2 桌号,user3,user4
				String roomUserlist = getRoomList();
				System.out.println("服务器端roomUserlist：" + roomUserlist);
				publicTalk(roomUserlist);
			}
		}
		/**
		 * 客户端选择了一个房间，服务器将其记录占用位置，并广播给所有用户最新的房间情况
		 * 成功进入该位置 返回：/room ok 左用户名称 右用户名    
		 * 否则：返回 /room occupy 左用户名称 右用户名
		 * 该位置有人了,进入观战模式
		 */
		else if (message.startsWith("/room ")) {
			String[] mm = message.substring("/room ".length()).split(" ");
			System.out.println("/room 分割后的数组：" + mm[0] + "," + mm[1] + "," + mm[2]);
			String roomId = mm[0];
			String leftUser = mm[1];
			String rightUser = mm[2];

			// 如果该房间还没有人，则直接加入房间，另一个位置置为"null"
			if (!roomHash.containsKey(roomId)) {
				synchronized (roomHash) {
					roomHash.put(roomId, leftUser + "," + rightUser);// key为房间号,value为格式 user1,user2 的形式
				}
				Feedback("/room ok "+leftUser + " " + rightUser);
			} else {// 如果有人
				String[] users = roomHash.get(roomId).split(",");
				if (!users[0].equals("null") && !users[1].equals("null")) {
					// 该房间位置有人了，且房间有两个人，则进入观战模式
					/**
					 * 进入观战
					 */
					Feedback("/room occupy "+users[0]+" "+users[1]);
				} else {// 如果房间只有一个人，则自动占领 空的位置
					if (users[0].equals("null")) {
						synchronized (roomHash) {
							String tmp = (leftUser.equals("null") ? rightUser : leftUser) + "," + users[1];
							String outMsg = (leftUser.equals("null") ? rightUser : leftUser) + " " + users[1];
							roomHash.replace(roomId, users[0] + "," + users[1], tmp);
							Feedback("/room ok "+outMsg);
						}
					} else {
						synchronized (roomHash) {
							String tmp = users[0] + "," + (leftUser.equals("null") ? rightUser : leftUser);
							String outMsg = users[0] + " " + (leftUser.equals("null") ? rightUser : leftUser);
							roomHash.replace(roomId, users[0] + "," + users[1], tmp);
							Feedback("/room ok "+outMsg);
						}
					}
				}
			}
			// 向其他用户更新各个桌子对战的用户情况
			publicTalk(getRoomList());

			// 不管房间有没有人，先加入房间观战用户列表
			if (!roomUserList.containsKey(roomId)) {
				synchronized (roomUserList) {
					Vector<String> roomVector = new Vector<String>();
					roomUserList.put(roomId, roomVector);
				}
			}

			Vector<String> tmp = roomUserList.get(roomId);
			synchronized (roomUserList) {
				String uuser = "";
				if (leftUser != null && !"null".equals(leftUser))
					uuser = leftUser;
				else if (rightUser != null && !"null".equals(rightUser))
					uuser = rightUser;
				System.out.println("uuser:" + uuser);
				if (roomUserList != null) {
					System.out.println("roomUserList=" + roomUserList);
					System.out.println("roomUserList.get(roomId)=" + roomUserList.get(roomId).toString());
					synchronized (roomUserList) {
						roomUserList.get(roomId).add(uuser);
					}

				}
			}
			String outMesg = getRoomUserList(roomId);
			// 并且对该房间的所有用户更新观战用户列表
			Iterator<String> iterator = tmp.iterator();
			while (iterator.hasNext()) {
				// 向该房间的用户广播房间观战用户列表
				peerTalk(iterator.next(), outMesg);
			}
		}
		/**
		 * 如果进入房间的用户离开房间 格式为 /leaveroom 房间号 user 需要广播给各个客户端最新房间信息
		 */
		else if (message.startsWith("/leaveroom ")) {
			String[] mm = message.substring("/leaveroom ".length()).split(" ");
			System.out.println("/leaveroom 分割后的数组：" + mm[0] + "," + mm[1]);
			String roomId = mm[0];
			String leaveName = mm[1];
			Vector<String> tmp = roomUserList.get(roomId);
			System.out.println("判断vector是否为空:" + tmp);
			System.out.println("判断该房间用户信息：" + tmp.toString());
			synchronized (tmp) {
				tmp.remove(leaveName);
				System.out.println("删除后的用户信息tmp=" + tmp.toString());
			}
			Iterator<String> iterator = tmp.iterator();
			// 向该房间用户发送更新的用户列表
			String outMesg = getRoomUserList(roomId);
			iterator = tmp.iterator();
			while (iterator.hasNext()) {
				// 向该房间内的用户广播房间用户列表
				peerTalk(iterator.next(), outMesg);
			}
			// 向MainUI主窗口广播，如果你是该房间的下棋着，则退出后该房间少了一人，故要广播说这个房间少一人
			// 如果不是，则不用广播
			String[] userlist = roomHash.get(roomId).split(",");
			synchronized (roomHash) {
				if (userlist[0].equals(leaveName)) {
					userlist[0] = "null";
				} else if (userlist[1].equals(leaveName)) {
					userlist[1] = "null";
				}
				roomHash.replace(roomId, userlist[0] + "," + userlist[1]);
			}
			publicTalk(getRoomList());
		}
		/**
		 * 返回观战的用户列表 请求格式：/eachroomuserlist 房间号 返回格式：/eachroomuserlist user1 user2 user3
		 */
		else if (message.startsWith("/eachroomuserlist ")) {
			// 格式为：/eachroomuserlist 房间号
			String result = message.substring("/eachroomuserlist ".length());
			if (result != null && !result.equals("")) {
				Iterator<String> tmp = roomUserList.get(result).iterator();
				String outMesg = getRoomUserList(result);
				System.out.println("服务端，消息/eachroomuserlist的getRoomUserList的结果" + outMesg);
				while (tmp.hasNext()) {
					peerTalk(tmp.next(), outMesg);
				}
			}
		}
		/**
		 * 返回正在对弈的双方信息 请求格式：/compete 房间号 返回格式：/compete user1 user2
		 */
		else if (message.startsWith("/compete ")) {
			String result = message.substring("/compete ".length());
			String[] competeUser = roomHash.get(result).split(",");
			if (result != null && !result.equals("")) {
				Iterator<String> tmp = roomUserList.get(result).iterator();
				String outMesg = "/compete " + competeUser[0] + " " + competeUser[1];
				while (tmp.hasNext()) {
					peerTalk(tmp.next(), outMesg);
				}
			}
		}
		/**
		 * 返回给所有该房间用户最新的棋盘状态 请求格式：/inchess 房间号 棋盘信息 返回格式：/inchess 棋盘数组信息
		 */
		else if (message.startsWith("/inchess ")) {
			String[] result = message.substring("/inchess ".length()).split(" ");
			Iterator<String> tmp = roomUserList.get(result[0]).iterator();
			String outMesg = "/inchess " +result[1];
			while (tmp.hasNext()) {
				peerTalk(tmp.next(), outMesg);
			}
		}
		/**
		 * 返回给对弈方你可以下棋了 请求格式：/play 房间号 本人用户名 返回格式：/play ok 
		 */
		else if (message.startsWith("/play ")) {
			String[] result = message.substring("/play ".length()).split(" ");
			String[] userlist = roomHash.get(result[0]).split(",");//user0,user1
			if (userlist[0].equals(result[1]) && !userlist[1].equals("null")) {
				//本用户是user0，同时user1有人，不空，那么发送给use1
				System.out.println("服务器：收到："+message+"消息，获得对战列表："+userlist[0]+","+userlist[1]);
				peerTalk(userlist[1], "/play "+userlist[1]+" ok");
			}
			else if (userlist[1].equals(result[1]) && !userlist[0].equals("null")) {
				peerTalk(userlist[0], "/play "+userlist[0]+" ok");
			}
		}
		/**
		 * 开始准备 请求格式：/prepare 房间号 用户名 返回：/prepare user1 user2 
		 */
		else if (message.startsWith("/prepare ")) {
			String[] result = message.substring("/prepare ".length()).split(" "); 
			if(!chessPeerHash.containsKey(result[0])) {
				//如果还没有该房间信息，则加进去
				synchronized (chessPeerHash) {
					chessPeerHash.put(result[0], result[1]+",null");
				}
				Feedback("/prepare "+result[1]+" null");
			}else {
				String[] tmp = chessPeerHash.get(result[0]).split(",");
				synchronized (chessPeerHash) {
					if (tmp[0].equals("null") && !result[1].equals(tmp[1])) {
						chessPeerHash.replace(result[0], result[1]+","+tmp[1]);
						Feedback("/prepare "+result[1]+" "+tmp[1]);
					}else if (tmp[1].equals("null") && !result[0].equals(tmp[0])) {
						chessPeerHash.replace(result[0], tmp[0]+","+result[1]);
						Feedback("/prepare "+tmp[0]+" "+result[1]);
					}else if (!tmp[0].equals("null") && !tmp[1].equals("null")) {
						Feedback("/prepare error");
					}
				}
				
			}
			
		}
	}

	/**
	 * 发送公共消息的函数，将消息向每个客户端都发送一份
	 */
	public void publicTalk(String publicTalkMessage) {

		synchronized (clientDataHash) {
			for (Enumeration<DataOutputStream> enu = clientDataHash.elements(); enu.hasMoreElements();) {
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
		System.out.println("发送给用户" + peerTalk + "消息为：" + talkMessage);
		for (Enumeration<Socket> enu = clientDataHash.keys(); enu.hasMoreElements();) {
			Socket userClient = (Socket) enu.nextElement();
			// 找到发送消息的对象，获取它的输出流以发送消息
			if (peerTalk.equals((String) clientNameHash.get(userClient))
					&& !peerTalk.equals((String) clientNameHash.get(clientSocket))) {
				synchronized (clientDataHash) {
					DataOutputStream peerOutData = (DataOutputStream) clientDataHash.get(userClient);
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
					&& !chessPeerTalk.equals((String) clientNameHash.get(clientSocket))) {
				synchronized (clientDataHash) {
					DataOutputStream peerOutData = (DataOutputStream) clientDataHash.get(userClient);
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
			DataOutputStream outData = (DataOutputStream) clientDataHash.get(clientSocket);
			try {
				outData.writeUTF(feedbackString);
			} catch (Exception eb) {
				eb.printStackTrace();
			}
		}

	}

	/**
	 * 获取在线房间情况，输出格式为/mainui 桌号,user1,user2 桌号,user1,user2
	 * 桌号key对应的value本身就是user1,user2的格式
	 */
	public String getRoomList() {
		String roomUserList = "/mainui";

		// 利用循环遍历出key和value
		Iterator<String> itr = roomHash.keySet().iterator();
		while (itr.hasNext()) {
			String roomId = (String) itr.next();
			roomUserList = roomUserList + " " + roomId + "," + roomHash.get(roomId);
		}
		return roomUserList;
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
	 * 获取该房间所有在线用户
	 */
	public String getRoomUserList(String roomId) {
		Vector<String> tmp = roomUserList.get(roomId);
		// 并且对该房间的所有用户更新用户列表
		Iterator<String> iterator = tmp.iterator();
		String outMesg = "/eachroomuserlist";
		while (iterator.hasNext()) {
			outMesg = outMesg + " " + iterator.next();
		}
		return outMesg;
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
		// Feedback("/yourname " + (String) clientNameHash.get(clientSocket));
		// Feedback("Java五子棋聊天客户端");
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
				chessPeerHash.put((String) getHashKey(chessPeerHash, (String) clientNameHash.get(clientSocket)),
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
			exx.printStackTrace();
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
			// firstCome();
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
	Hashtable<String, String> roomHash = new Hashtable<String, String>(50);// 每个房间的对弈情况，key为房间号，value为 客户端1 客户端2，空格隔开
	ConcurrentHashMap<String, Vector<String>> roomUserList = new ConcurrentHashMap<String, Vector<String>>();

	/**
	 * 框架类的构造函数
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

		// 初始化各个房间的观众列表为空
		for (int i = 0; i < 50; i++) {
			Vector<String> tmp = new Vector<String>();
			roomUserList.put(String.valueOf(i), tmp);
		}
	}

	/**
	 * 初始化消息服务器的类
	 */
	public void makeMessageServer(int port, MessageServerPanel server) throws IOException {
		Socket clientSocket;
		this.server = server;

		try {
			// 输出服务器的启动信息
			serverSocket = new ServerSocket(port);
			server.messageBoard.setText("服务器开始于:" + serverSocket.getInetAddress().getLocalHost() + ":"
					+ serverSocket.getLocalPort() + "\n");

			while (true) {
				clientSocket = serverSocket.accept();
				server.messageBoard.append("用户连接:" + clientSocket + "\n");

				// 用户与服务器连接之后，clientDataHash即用户socket和其数据流的对应先进行记录，
				// 然后再判断其登录用的用户名是否与已有的重复了，即使重复了也可以通过这个数据流来返回通知去来源端修改，
				// 而不是用错误的用户名去查找用户名和socket表的另一正常方socket
				DataOutputStream outData = new DataOutputStream(clientSocket.getOutputStream());
				clientDataHash.put(clientSocket, outData);
				// 为每一次信息开启一个线程做处理
				ServerThread thread = new ServerThread(clientSocket, clientDataHash, clientNameHash, chessPeerHash,
						roomHash, roomUserList, server);
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
				server.messageBoard.append("服务器信息:" + serverSocket.getInetAddress().getLocalHost() + ":"
						+ serverSocket.getLocalPort() + "\n");
			} catch (Exception ee) {
				System.out.println("serverSocket.getInetAddress().getLocalHost() error \n");
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