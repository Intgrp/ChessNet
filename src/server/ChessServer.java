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
 * ��ʾ���������û���Ϣ��Panel��
 */
class MessageServerPanel extends Panel {
	TextArea messageBoard = new TextArea("", 22, 50,
			TextArea.SCROLLBARS_VERTICAL_ONLY);

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

	Hashtable<String, String> chessPeerHash;// ���ĵ������ͻ����û�����ӳ��
	
	Hashtable<String, String> room;//ÿ������Ķ��������keyΪ����ţ�valueΪ �ͻ���1 �ͻ���2���ո����

	MessageServerPanel server;

	boolean isClientClosed = false;

	/**
	 * ���������̵߳Ĺ��캯�������ڳ�ʼ��һЩ����
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
	 * �Կͻ��˷�������Ϣ����ĺ����������ת���ؿͻ��ˡ�������Ϣ�Ĺ��̱Ƚϸ��ӣ� Ҫ��Ժܶ�������ֱ���
	 */
	public void messageTransfer(String message) {
		System.out.println("�յ���Ϣ����ϢΪ��"+message);
		String clientName ,peerName;
		//��¼��ȷ���Ƿ��Ѿ�����ռ��������ǳ�
		if (message.startsWith("/login ")) {
			clientName = message.substring("/login ".length());
			System.out.println(clientName);
			if (clientNameHash.containsValue(clientName)) {
				//����Ѿ����ڸ��û�������ֳ�ͻ������������û���
				//��Ϊ�û�����ͻ������ͨ��clientDataHash��������ʱ�ͼ�¼������������������û��ˣ�����������ź�
				Feedback("/login error");//�ú�����ͨ��clientDataHash�ҵ��Լ���socket����������
				//peerTalk(clientName,"/login error");
			}else {
				//����˳����¼���û����û��б�
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
//				peerTalk(clientName,"/login ok");//ѡ���������Ϣ
			}
		}
		//�г�Ա��¼�ɹ���Ⱥ�������û��б���ͻ��˸����û��б�
		else if (message.startsWith("/mainui ")){
			String opera=message.substring("/mainui ".length());
			if (opera.equals("allonline")) {
				//�����Ҫ��ȡ�����û��б������㲥�����û�
				firstCome();
			}
		}
		//�ͻ���ѡ����һ�����䣬�����������¼ռ��λ�ã����㲥�������û����µķ������
		else if (message.startsWith("/room ")) {
			String []mm = message.substring("/room ".length()).split(" ");
			String roomId = mm[0];
			String leftUser = mm[1];
			String rightUser = mm[2];
			//����÷��仹û���ˣ���ֱ�Ӽ��뷿�䣬��һ��λ����Ϊ"null"
			if (!room.containsKey(roomId)) {
				synchronized (room) {
					room.put(roomId, leftUser+" "+ rightUser);
				}
				Feedback("/room ok");
			}else{//�������
				String [] users = room.get(roomId).split(" ");
				if ((users[0]!="null" && users[1]!="null") ||
						(leftUser!="null" && users[0]!="null") || 
						(rightUser!="null" && users[1]!="null")) {
					//�÷���λ�������ˣ��ҷ����������ˣ��������˵�λ�ú��û�ѡ��λ��һ���������
					Feedback("/room error");
				}
				//�û�ռ�����λ������û�ˣ���һ��λ��û��(��Ϊ��һ���Ѿ������������˵����ˣ���������Ϳ϶���ֻ��һ������
				else if ((leftUser != "null" && users[0]=="null" ) ) {//������û��
					synchronized (room) {
						room.replace(roomId, users[0]+" "+users[1],  leftUser+" "+  users[1]);
					}
					Feedback("/room ok");
				}
				else if (rightUser!="null" && users[1]=="null") {//����ұ�û��
					synchronized (room) {
						room.replace(roomId, users[0]+" "+users[1],  users[0]+" "+  rightUser);
					}
					Feedback("/room ok");
				}
			}
		}
		
		
	}

	/**
	 * ���͹�����Ϣ�ĺ���������Ϣ��ÿ���ͻ��˶�����һ��
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
	 * ѡ���������Ϣ������peerTalkΪ���͵��û���������Ĳ���Ϊ���͵���Ϣ
	 */
	public boolean peerTalk(String peerTalk, String talkMessage) {
		//
		System.out.println("���͸��û�"+peerTalk+"��ϢΪ��"+talkMessage);
		for (Enumeration<Socket> enu = clientDataHash.keys(); enu.hasMoreElements();) {
			Socket userClient = (Socket) enu.nextElement();
			// �ҵ�������Ϣ�Ķ��󣬻�ȡ����������Է�����Ϣ
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
	 * ���ڴ�����Ϣ�����ĺ���
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
//		Feedback("/yourname " + (String) clientNameHash.get(clientSocket));
//		Feedback("Java����������ͻ���");	
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
				chessPeerHash.put((String) getHashKey(chessPeerHash,
						(String) clientNameHash.get(clientSocket)),
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
	Hashtable<String, String> room = new Hashtable<String, String>(50);//ÿ������Ķ��������keyΪ����ţ�valueΪ �ͻ���1 �ͻ���2���ո����

	/**
	 *�����Ĺ��캯��
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
	}

	/**
	 * ��ʼ����Ϣ����������
	 */
	public void makeMessageServer(int port, MessageServerPanel server)
			throws IOException {
		Socket clientSocket;
		this.server = server;

		try {
			// �����������������Ϣ
			serverSocket = new ServerSocket(port);
			server.messageBoard.setText("��������ʼ��:"
					+ serverSocket.getInetAddress().getLocalHost() + ":"
					+ serverSocket.getLocalPort() + "\n");

			while (true) {
				clientSocket = serverSocket.accept();
				server.messageBoard.append("�û�����:" + clientSocket + "\n");
				
				//�û������������֮��clientDataHash���û�socket�����������Ķ�Ӧ�Ƚ��м�¼��
				//Ȼ�����ж����¼�õ��û����Ƿ������е��ظ��ˣ���ʹ�ظ���Ҳ����ͨ�����������������֪ͨȥ��Դ���޸ģ�
				//�������ô�����û���ȥ�����û�����socket�����һ������socket
				DataOutputStream outData = new DataOutputStream(clientSocket.getOutputStream());
				clientDataHash.put(clientSocket, outData);
				//Ϊÿһ����Ϣ����һ���߳�������
				ServerThread thread = new ServerThread(clientSocket,
						clientDataHash, clientNameHash, chessPeerHash, room, server);
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
				server.messageBoard.append("��������Ϣ:"
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