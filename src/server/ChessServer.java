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
 * ��ʾ���������û���Ϣ��Panel��
 */
class MessageServerPanel extends Panel {
	TextArea messageBoard = new TextArea("", 22, 50, TextArea.SCROLLBARS_VERTICAL_ONLY);

	JLabel statusLabel = new JLabel("��ǰ������:", Label.LEFT);

	JPanel boardPanel = new JPanel();// ����ʾ��Panel

	JPanel statusPanel = new JPanel();// ����״̬Panel

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
 * �������߳�,��Ҫ���ڷ�������ͻ��˵�ͨ��
 */
class ServerThread extends Thread {
	Socket clientSocket;

	Hashtable<Socket, DataOutputStream> clientDataHash;// Socket�뷢�����ݵ�����ӳ��

	Hashtable<Socket, String> clientNameHash;// Socket���û�����ӳ��
	// ���ĵ������ͻ��˷���ź������û�����ӳ��,�������壬��Ž�ȥ����roomHashһ����ֻ�������ǽ���׼��״̬
	Hashtable<String, String> chessPeerHash;

	// ÿ������Ķ��������keyΪ����ţ�valueΪ �ͻ���1���ͻ���2�����Ÿ���
	// Ϊ���ڶ�ս���б��������ʱ�пյģ���ֱ�ӽ����ս��Ա��������գ�������ս�б�
	Hashtable<String, String> roomHash;

	ConcurrentHashMap<String, Vector<String>> roomUserList;// ��¼ÿ�������ȥ�˵����б��ʽΪ�� ����� �û��б�

	MessageServerPanel server;

	boolean isClientClosed = false;

	/**
	 * ���������̵߳Ĺ��캯�������ڳ�ʼ��һЩ����
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
	 * �Կͻ��˷�������Ϣ����ĺ����������ת���ؿͻ��ˡ�������Ϣ�Ĺ��̱Ƚϸ��ӣ� Ҫ��Ժܶ�������ֱ���
	 */
	public void messageTransfer(String message) {
		System.out.println("�յ���Ϣ����ϢΪ��" + message);
		String clientName, peerName;
		/**
		 * ��¼��ȷ���Ƿ��Ѿ�����ռ��������ǳ�
		 */
		if (message.startsWith("/login ")) {
			clientName = message.substring("/login ".length());
			System.out.println(clientName);
			if (clientName.equals("") || clientNameHash.containsValue(clientName)) {
				// ����Ѿ����ڸ��û�������ֳ�ͻ������������û���
				// ��Ϊ�û�����ͻ������ͨ��clientDataHash��������ʱ�ͼ�¼������������������û��ˣ�����������ź�
				Feedback("/login error");// �ú�����ͨ��clientDataHash�ҵ��Լ���socket����������
				// peerTalk(clientName,"/login error");
			} else {
				// ����˳����¼���û����û��б�
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
				// peerTalk(clientName,"/login ok");//ѡ���������Ϣ
			}
		}
		/**
		 * �г�Ա��¼�ɹ���Ⱥ�������û��б���ͻ��˸����û��б�
		 */
		else if (message.startsWith("/mainui ")) {
			String opera = message.substring("/mainui ".length());
			if (opera.equals("allonline")) {
				// �����Ҫ��ȡ�����û��б������㲥�����û�
				firstCome();
				// ������������Ϊ /mainui ����,user1,user2 ����,user3,user4
				String roomUserlist = getRoomList();
				System.out.println("��������roomUserlist��" + roomUserlist);
				publicTalk(roomUserlist);
			}
		}
		/**
		 * �ͻ���ѡ����һ�����䣬�����������¼ռ��λ�ã����㲥�������û����µķ������
		 * �ɹ������λ�� ���أ�/room ok ���û����� ���û���    
		 * ���򣺷��� /room occupy ���û����� ���û���
		 * ��λ��������,�����սģʽ
		 */
		else if (message.startsWith("/room ")) {
			String[] mm = message.substring("/room ".length()).split(" ");
			System.out.println("/room �ָ������飺" + mm[0] + "," + mm[1] + "," + mm[2]);
			String roomId = mm[0];
			String leftUser = mm[1];
			String rightUser = mm[2];

			// ����÷��仹û���ˣ���ֱ�Ӽ��뷿�䣬��һ��λ����Ϊ"null"
			if (!roomHash.containsKey(roomId)) {
				synchronized (roomHash) {
					roomHash.put(roomId, leftUser + "," + rightUser);// keyΪ�����,valueΪ��ʽ user1,user2 ����ʽ
				}
				Feedback("/room ok "+leftUser + " " + rightUser);
			} else {// �������
				String[] users = roomHash.get(roomId).split(",");
				if (!users[0].equals("null") && !users[1].equals("null")) {
					// �÷���λ�������ˣ��ҷ����������ˣ�������սģʽ
					/**
					 * �����ս
					 */
					Feedback("/room occupy "+users[0]+" "+users[1]);
				} else {// �������ֻ��һ���ˣ����Զ�ռ�� �յ�λ��
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
			// �������û����¸������Ӷ�ս���û����
			publicTalk(getRoomList());

			// ���ܷ�����û���ˣ��ȼ��뷿���ս�û��б�
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
			// ���ҶԸ÷���������û����¹�ս�û��б�
			Iterator<String> iterator = tmp.iterator();
			while (iterator.hasNext()) {
				// ��÷�����û��㲥�����ս�û��б�
				peerTalk(iterator.next(), outMesg);
			}
		}
		/**
		 * ������뷿����û��뿪���� ��ʽΪ /leaveroom ����� user ��Ҫ�㲥�������ͻ������·�����Ϣ
		 */
		else if (message.startsWith("/leaveroom ")) {
			String[] mm = message.substring("/leaveroom ".length()).split(" ");
			System.out.println("/leaveroom �ָ������飺" + mm[0] + "," + mm[1]);
			String roomId = mm[0];
			String leaveName = mm[1];
			Vector<String> tmp = roomUserList.get(roomId);
			System.out.println("�ж�vector�Ƿ�Ϊ��:" + tmp);
			System.out.println("�жϸ÷����û���Ϣ��" + tmp.toString());
			synchronized (tmp) {
				tmp.remove(leaveName);
				System.out.println("ɾ������û���Ϣtmp=" + tmp.toString());
			}
			Iterator<String> iterator = tmp.iterator();
			// ��÷����û����͸��µ��û��б�
			String outMesg = getRoomUserList(roomId);
			iterator = tmp.iterator();
			while (iterator.hasNext()) {
				// ��÷����ڵ��û��㲥�����û��б�
				peerTalk(iterator.next(), outMesg);
			}
			// ��MainUI�����ڹ㲥��������Ǹ÷���������ţ����˳���÷�������һ�ˣ���Ҫ�㲥˵���������һ��
			// ������ǣ����ù㲥
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
		 * ���ع�ս���û��б� �����ʽ��/eachroomuserlist ����� ���ظ�ʽ��/eachroomuserlist user1 user2 user3
		 */
		else if (message.startsWith("/eachroomuserlist ")) {
			// ��ʽΪ��/eachroomuserlist �����
			String result = message.substring("/eachroomuserlist ".length());
			if (result != null && !result.equals("")) {
				Iterator<String> tmp = roomUserList.get(result).iterator();
				String outMesg = getRoomUserList(result);
				System.out.println("����ˣ���Ϣ/eachroomuserlist��getRoomUserList�Ľ��" + outMesg);
				while (tmp.hasNext()) {
					peerTalk(tmp.next(), outMesg);
				}
			}
		}
		/**
		 * �������ڶ��ĵ�˫����Ϣ �����ʽ��/compete ����� ���ظ�ʽ��/compete user1 user2
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
		 * ���ظ����и÷����û����µ�����״̬ �����ʽ��/inchess ����� ������Ϣ ���ظ�ʽ��/inchess ����������Ϣ
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
		 * ���ظ����ķ������������ �����ʽ��/play ����� �����û��� ���ظ�ʽ��/play ok 
		 */
		else if (message.startsWith("/play ")) {
			String[] result = message.substring("/play ".length()).split(" ");
			String[] userlist = roomHash.get(result[0]).split(",");//user0,user1
			if (userlist[0].equals(result[1]) && !userlist[1].equals("null")) {
				//���û���user0��ͬʱuser1���ˣ����գ���ô���͸�use1
				System.out.println("���������յ���"+message+"��Ϣ����ö�ս�б�"+userlist[0]+","+userlist[1]);
				peerTalk(userlist[1], "/play "+userlist[1]+" ok");
			}
			else if (userlist[1].equals(result[1]) && !userlist[0].equals("null")) {
				peerTalk(userlist[0], "/play "+userlist[0]+" ok");
			}
		}
		/**
		 * ��ʼ׼�� �����ʽ��/prepare ����� �û��� ���أ�/prepare user1 user2 
		 */
		else if (message.startsWith("/prepare ")) {
			String[] result = message.substring("/prepare ".length()).split(" "); 
			if(!chessPeerHash.containsKey(result[0])) {
				//�����û�и÷�����Ϣ����ӽ�ȥ
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
	 * ���͹�����Ϣ�ĺ���������Ϣ��ÿ���ͻ��˶�����һ��
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
	 * ѡ���������Ϣ������peerTalkΪ���͵��û���������Ĳ���Ϊ���͵���Ϣ
	 */
	public boolean peerTalk(String peerTalk, String talkMessage) {
		//
		System.out.println("���͸��û�" + peerTalk + "��ϢΪ��" + talkMessage);
		for (Enumeration<Socket> enu = clientDataHash.keys(); enu.hasMoreElements();) {
			Socket userClient = (Socket) enu.nextElement();
			// �ҵ�������Ϣ�Ķ��󣬻�ȡ����������Է�����Ϣ
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
			// ����Ƿ����Լ��ģ�ֱ�ӻ���
			else if (peerTalk.equals((String) clientNameHash.get(clientSocket))) {
				Feedback(talkMessage);
				return (false);
			}
		}

		return (true);

	}

	/**
	 * �˺���Ҳ����ѡ������Ϣ�������ܷ��͸��Լ���
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
	 * ���ڴ�����Ϣ�����ĺ���
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
	 * ��ȡ���߷�������������ʽΪ/mainui ����,user1,user2 ����,user1,user2
	 * ����key��Ӧ��value�������user1,user2�ĸ�ʽ
	 */
	public String getRoomList() {
		String roomUserList = "/mainui";

		// ����ѭ��������key��value
		Iterator<String> itr = roomHash.keySet().iterator();
		while (itr.hasNext()) {
			String roomId = (String) itr.next();
			roomUserList = roomUserList + " " + roomId + "," + roomHash.get(roomId);
		}
		return roomUserList;
	}

	/**
	 * ��ȡ�û��б�ĺ������˺�����ȡclientNameHash��ȡ�û��б� Ȼ���䱣����һ���ַ���userList�С�
	 */
	public String getUserList() {
		String userList = "/userlist";

		for (Enumeration<String> enu = clientNameHash.elements(); enu.hasMoreElements();) {
			userList = userList + " " + (String) enu.nextElement();
		}
		return userList;
	}

	/**
	 * ��ȡ�÷������������û�
	 */
	public String getRoomUserList(String roomId) {
		Vector<String> tmp = roomUserList.get(roomId);
		// ���ҶԸ÷���������û������û��б�
		Iterator<String> iterator = tmp.iterator();
		String outMesg = "/eachroomuserlist";
		while (iterator.hasNext()) {
			outMesg = outMesg + " " + iterator.next();
		}
		return outMesg;
	}

	/**
	 * ����HashTable��ֵ���󣬻�ȡ���Ӧ�ü�ֵ�ĺ�����
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
	 * ����������յ�½��ʱ������е������û��б����ʾ����
	 */
	public void firstCome() {
		publicTalk(getUserList());
		// Feedback("/yourname " + (String) clientNameHash.get(clientSocket));
		// Feedback("Java����������ͻ���");
	}

	/**
	 * ���ںͿͻ��˶Ͽ��ĺ�����
	 */
	public void clientClose() {
		server.messageBoard.append("�û��Ͽ�:" + clientSocket + "\n");
		// �������Ϸ�ͻ�������
		synchronized (chessPeerHash) {
			if (chessPeerHash.containsKey(clientNameHash.get(clientSocket))) {
				chessPeerHash.remove((String) clientNameHash.get(clientSocket));
			}
			if (chessPeerHash.containsValue(clientNameHash.get(clientSocket))) {
				chessPeerHash.put((String) getHashKey(chessPeerHash, (String) clientNameHash.get(clientSocket)),
						"tobeclosed");
			}
		}
		// ��������HashTable����������
		synchronized (clientDataHash) {
			clientDataHash.remove(clientSocket);
		}
		synchronized (clientNameHash) {
			clientNameHash.remove(clientSocket);
		}
		if (!"/userlist".equals(getUserList())) {
			publicTalk(getUserList());
		}
		// ���㵱ǰ������������ʾ��״̬����
		server.statusLabel.setText("��ǰ������:" + clientDataHash.size());
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
			server.statusLabel.setText("��ǰ������:" + clientDataHash.size());
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
 * @author wufenghanren �������˿����
 */
public class ChessServer extends Frame implements ActionListener {

	JButton messageClearButton = new JButton("�����ʾ");

	JButton serverStatusButton = new JButton("������״̬");

	JButton serverOffButton = new JButton("�رշ�����");

	Panel buttonPanel = new Panel();

	MessageServerPanel server = new MessageServerPanel();

	ServerSocket serverSocket;

	Hashtable<Socket, DataOutputStream> clientDataHash = new Hashtable<Socket, DataOutputStream>(50);

	Hashtable<Socket, String> clientNameHash = new Hashtable<Socket, String>(50);

	Hashtable<String, String> chessPeerHash = new Hashtable<String, String>(50);
	Hashtable<String, String> roomHash = new Hashtable<String, String>(50);// ÿ������Ķ��������keyΪ����ţ�valueΪ �ͻ���1 �ͻ���2���ո����
	ConcurrentHashMap<String, Vector<String>> roomUserList = new ConcurrentHashMap<String, Vector<String>>();

	/**
	 * �����Ĺ��캯��
	 */
	ChessServer() {
		super("Java�����������");
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
		// �˳����ڵļ�����
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

		// ��ʼ����������Ĺ����б�Ϊ��
		for (int i = 0; i < 50; i++) {
			Vector<String> tmp = new Vector<String>();
			roomUserList.put(String.valueOf(i), tmp);
		}
	}

	/**
	 * ��ʼ����Ϣ����������
	 */
	public void makeMessageServer(int port, MessageServerPanel server) throws IOException {
		Socket clientSocket;
		this.server = server;

		try {
			// �����������������Ϣ
			serverSocket = new ServerSocket(port);
			server.messageBoard.setText("��������ʼ��:" + serverSocket.getInetAddress().getLocalHost() + ":"
					+ serverSocket.getLocalPort() + "\n");

			while (true) {
				clientSocket = serverSocket.accept();
				server.messageBoard.append("�û�����:" + clientSocket + "\n");

				// �û������������֮��clientDataHash���û�socket�����������Ķ�Ӧ�Ƚ��м�¼��
				// Ȼ�����ж����¼�õ��û����Ƿ������е��ظ��ˣ���ʹ�ظ���Ҳ����ͨ�����������������֪ͨȥ��Դ���޸ģ�
				// �������ô�����û���ȥ�����û�����socket�����һ������socket
				DataOutputStream outData = new DataOutputStream(clientSocket.getOutputStream());
				clientDataHash.put(clientSocket, outData);
				// Ϊÿһ����Ϣ����һ���߳�������
				ServerThread thread = new ServerThread(clientSocket, clientDataHash, clientNameHash, chessPeerHash,
						roomHash, roomUserList, server);
				thread.start();
			}
		} catch (IOException ex) {
			System.out.println("�Ѿ��з�����������. \n");
		}
	}

	/**
	 * ��ť���¼�����������Ӧ��ť����¼�
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == messageClearButton) {
			server.messageBoard.setText("");
		}
		// ����������״̬����ť���ʱ����ʾ������״̬
		if (e.getSource() == serverStatusButton) {
			try {
				server.messageBoard.append("��������Ϣ:" + serverSocket.getInetAddress().getLocalHost() + ":"
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