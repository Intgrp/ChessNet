package org.shan.server;

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

	Hashtable clientDataHash;// Socket与发送数据的流的映射

	Hashtable clientNameHash;// Socket与用户名的映射

	Hashtable chessPeerHash;// 对弈的两个客户端用户名的映射

	MessageServerPanel server;

	boolean isClientClosed = false;

	/**
	 * 服务器端线程的构造函数，用于初始化一些对象。
	 */
	ServerThread(Socket clientSocket, Hashtable clientDataHash,
			Hashtable clientNameHash, Hashtable chessPeerHash,
			MessageServerPanel server) {
		this.clientSocket = clientSocket;
		this.clientDataHash = clientDataHash;
		this.clientNameHash = clientNameHash;
		this.chessPeerHash = chessPeerHash;
		this.server = server;
	}

	/**
	 * 对客户端发来的消息处理的函数，处理后转发回客户端。处理消息的过程比较复杂， 要针对很多种情况分别处理。
	 */
	public void messageTransfer(String message) {
		String clientName, peerName;
		// 如果消息以“/”开头，表明是命令消息。
		if (message.startsWith("/")) {
			// 消息以“/changename”开头的几种情况.
			if (message.startsWith("/r")) {
				// 获取修改后的用户名
				clientName = message.substring(2);
				if (clientName.length() <= 0 || clientName.length() > 20
						|| clientName.startsWith("/")
						|| clientNameHash.containsValue(clientName)
						|| clientName.startsWith("changename")
						|| clientName.startsWith("list")
						|| clientName.startsWith("[inchess]")
						|| clientName.startsWith("creatgame")
						|| clientName.startsWith("joingame")
						|| clientName.startsWith("yourname")
						|| clientName.startsWith("userlist")
						|| clientName.startsWith("chess")
						|| clientName.startsWith("OK")
						|| clientName.startsWith("reject")
						|| clientName.startsWith("peer")
						|| clientName.startsWith("peername")
						|| clientName.startsWith("giveup")
						|| clientName.startsWith("youwin")
						|| clientName.startsWith("所有人")) {
					// 如果名字不合规则，则向客户端发送信息“无效命令”。
					message = "无效命令";
					Feedback(message);
				} else {
					if (clientNameHash
							.containsValue(("[inchess]" + (String) clientNameHash
									.get(clientSocket)))) {
						// 如果用户正在对弈中，则直接修改Socket和名字的映射Hash表。
						synchronized (clientNameHash) {
							clientNameHash.put((Socket) getHashKey(
									clientNameHash,
									("[inchess]" + clientNameHash
											.get(clientSocket))),
									("[inchess]" + clientName));
							chessPeerTalk(("[inchess" + clientName),
									("/yourname " + ("[inchess]" + clientName)));
						}
					}
					// 如果用户在对弈双方的客户端，做如下处理。
					else if (chessPeerHash.containsKey(clientNameHash
							.get(clientSocket))) {
						// 将客户端的新名字映射到clientNameHash
						synchronized (clientNameHash) {
							clientNameHash.put((Socket) getHashKey(
									clientNameHash,
									("[inchess]" + clientNameHash
											.get(clientSocket))),
									("[inchess]" + clientName));
						}

						synchronized (chessPeerHash) {
							// chessPeerHash添加新名字映射
							chessPeerHash.put(clientName, chessPeerHash
									.get(clientNameHash.get(clientSocket)));
							// chessPeerHash删除旧映射
							chessPeerHash.remove(clientNameHash
									.get(clientSocket));
						}
						// 向游戏客户端发送新名字
						chessPeerTalk(("[inchess]" + clientName),
								("/yourname " + ("[inchess]" + clientName)));
						// 向peer游戏客户端发送
						chessPeerTalk((String) chessPeerHash.get(clientName),
								("/peer " + "[inchess]" + clientName));

					}
					// 如果用户在对弈双方的服务端，做如下处理。
					else if (chessPeerHash.containsValue(clientNameHash
							.get(clientSocket))) {
						synchronized (clientNameHash) {
							// 游戏客户端改名字
							clientNameHash.put((Socket) getHashKey(
									clientNameHash,
									("[inchess]" + clientNameHash
											.get(clientSocket))),
									("[inchess]" + clientName));
						}
						synchronized (chessPeerHash) {
							// chessPeerHash重新映射
							chessPeerHash.put((String) getHashKey(
									chessPeerHash, clientNameHash
											.get(clientSocket)), clientName);
							// 向游戏客户端发送新名字
							chessPeerTalk(("[inchess]" + clientName),
									("/yourname " + ("[inchess]" + clientName)));
						}
						// 向peer游戏客户端发送
						chessPeerTalk((String) getHashKey(chessPeerHash,
								clientName),
								("/peer " + "[inchess]" + clientName));

					}
					// 将改名消息封装好以备发送
					message = clientNameHash.get(clientSocket) + "改名为:"
							+ clientName;
					synchronized (clientNameHash) {
						clientNameHash.put(clientSocket, clientName);
					}
					// 向所有的客户端发送消息
					publicTalk(message);
					// 回馈到改名的客户端
					Feedback("/yourname "
							+ (String) clientNameHash.get(clientSocket));
					// 刷新用户列表
					publicTalk(getUserList());

				}

			}
			// 如果消息以“/list”开头，则将其回馈到客户端以更新用户列表
			else if (message.equals("/list")) {
				Feedback(getUserList());
			}
			// 如果消息以"/creatgame [inchess]"开头，则修改clientNameHash映射
			// 和chessPeerHash映射。
			else if (message.startsWith("/creatgame [inchess]")) {
				String chessServerName = message.substring(20);
				synchronized (clientNameHash) {
					clientNameHash.put(clientSocket, message.substring(11));
				}
				synchronized (chessPeerHash) {
					chessPeerHash.put(chessServerName, "wait");
				}
				Feedback("/yourname " + clientNameHash.get(clientSocket));
				chessPeerTalk(chessServerName, "/OK");
				publicTalk(getUserList());
			}
			// 如果消息以“/joingame”开头，则将消息的服务端名字和本地用户名提取出来，
			// 然后修改clientNameHash表和chessPeerHash表。
			else if (message.startsWith("/joingame ")) {
				StringTokenizer userToken = new StringTokenizer(message, " ");
				String getUserToken, serverName, selfName;
				String[] chessNameOpt = { "0", "0" };
				int getOptNum = 0;
				// 提取服务端用户名和本地用户名
				while (userToken.hasMoreTokens()) {
					getUserToken = (String) userToken.nextToken(" ");
					if (getOptNum >= 1 && getOptNum <= 2) {
						chessNameOpt[getOptNum - 1] = getUserToken;
					}
					getOptNum++;
				}
				serverName = chessNameOpt[0];
				selfName = chessNameOpt[1];
				// 如果有服务端在等待开始棋局
				if (chessPeerHash.containsKey(serverName)
						&& chessPeerHash.get(serverName).equals("wait")) {
					// 修改Socket和名字映射
					synchronized (clientNameHash) {
						clientNameHash.put(clientSocket,
								("[inchess]" + selfName));
					}
					// 修改chessPeerHash映射
					synchronized (chessPeerHash) {
						chessPeerHash.put(serverName, selfName);
					}
					publicTalk(getUserList());
					chessPeerTalk(selfName,
							("/peer " + "[inchess]" + serverName));
					chessPeerTalk(serverName,
							("/peer " + "[inchess]" + selfName));
				} else {
					chessPeerTalk(selfName, "/reject");
					try {
						clientClose();
					} catch (Exception ez) {
					}
				}
			}
			// 如果消息以“/[inchess]”开头，则获取要发送消息的用户名和发送的消息
			// 然后发送出去。
			else if (message.startsWith("/[inchess]")) {
				int firstLocation = 0, lastLocation;

				lastLocation = message.indexOf(" ", 0);

				peerName = message.substring((firstLocation + 1), lastLocation);
				message = message.substring((lastLocation + 1));
				if (chessPeerTalk(peerName, message)) {
					Feedback("/error");
				}
			}
			// 如果消息以“/giveup”开头，则判断是对弈双方哪方放弃了。
			else if (message.startsWith("/giveup ")) {
				String chessClientName = message.substring(8);
				if (chessPeerHash.containsKey(chessClientName)
						&& !((String) chessPeerHash.get(chessClientName))
								.equals("wait")) {
					// 如果服务方放弃，则发送消息“/youwin”表明对方获胜
					chessPeerTalk((String) chessPeerHash.get(chessClientName),
							"/youwin");
					synchronized (chessPeerHash) {
						chessPeerHash.remove(chessClientName);
					}
				}
				if (chessPeerHash.containsValue(chessClientName)) {
					// 如果客户方放弃，也发送消息“/youwin”表明对方获胜
					chessPeerTalk((String) getHashKey(chessPeerHash,
							chessClientName), "/youwin");
					synchronized (chessPeerHash) {
						chessPeerHash.remove((String) getHashKey(chessPeerHash,
								chessClientName));
					}
				}
			}
			// 如果找不到发送消息的用户，则输出消息说“没有这个用户”
			else {

				int firstLocation = 0, lastLocation;

				lastLocation = message.indexOf(" ", 0);
				if (lastLocation == -1) {
					Feedback("无效命令");
					return;
				} else {
					peerName = message.substring((firstLocation + 1),
							lastLocation);
					message = message.substring((lastLocation + 1));
					message = (String) clientNameHash.get(clientSocket) + ">"
							+ message;
					if (peerTalk(peerName, message)) {
						Feedback("没有这个用户:" + peerName + "\n");
					}
				}

			}

		}
		// 如果不以“/”开头，表明是普通消息，直接发送
		else {
			message = clientNameHash.get(clientSocket) + ">" + message;
			server.messageBoard.append(message + "\n");
			publicTalk(message);
			server.messageBoard.setCaretPosition(server.messageBoard.getText()
					.length());
		}

	}

	/**
	 * 发送公共消息的函数，将消息向每个客户端都发送一份
	 */
	public void publicTalk(String publicTalkMessage) {

		synchronized (clientDataHash) {
			for (Enumeration enu = clientDataHash.elements(); enu
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
		for (Enumeration enu = clientDataHash.keys(); enu.hasMoreElements();) {
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

		for (Enumeration enu = clientDataHash.keys(); enu.hasMoreElements();) {
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

		for (Enumeration enu = clientNameHash.elements(); enu.hasMoreElements();) {
			userList = userList + " " + (String) enu.nextElement();
		}
		return userList;
	}

	/**
	 * 给出HashTable和值对象，获取相对应得键值的函数。
	 */
	public Object getHashKey(Hashtable targetHash, Object hashValue) {
		Object hashKey;
		for (Enumeration enu = targetHash.keys(); enu.hasMoreElements();) {
			hashKey = (Object) enu.nextElement();
			if (hashValue.equals((Object) targetHash.get(hashKey)))
				return (hashKey);
		}
		return (null);
	}
	public void firstCome() {
		publicTalk(getUserList());
		Feedback("/yourname " + (String) clientNameHash.get(clientSocket));
		Feedback("Java五子棋聊天客户端");	
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
		publicTalk(getUserList());
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
			firstCome();
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

	Hashtable clientDataHash = new Hashtable(50);

	Hashtable clientNameHash = new Hashtable(50);

	Hashtable chessPeerHash = new Hashtable(50);

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
		long clientAccessNumber = 1;
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

				DataOutputStream outData = new DataOutputStream(clientSocket
						.getOutputStream());
				clientDataHash.put(clientSocket, outData);
				clientNameHash
						.put(clientSocket, ("新游客" + clientAccessNumber++));
				ServerThread thread = new ServerThread(clientSocket,
						clientDataHash, clientNameHash, chessPeerHash, server);
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