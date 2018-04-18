package ui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.jar.Attributes.Name;

import javax.swing.JFrame;

import util.MainUIThread;

public class MainUIFrame extends JFrame implements ActionListener{
	
	public JFrame mframe = new JFrame("������ͻ���");
	
	public RoomPanel roomPanel =new RoomPanel(this);
	public UserPanel userPanel = new UserPanel();
	
	public Socket clientSocket ;
	DataInputStream inData;
	DataOutputStream outData;
	public String name="";
	public String roomId = "";
	
	public MainUIThread mainUIThread ;
	
	public MainUIFrame() {
		roomPanel.setBounds(0, 0,180*5+60, 70*8+40);
		userPanel.setBounds(970, 0, 200, 600);
		
		mframe.add(roomPanel);
		mframe.add(userPanel);
		mframe.setLayout(null);
		mframe.setSize(1200, 640);
		mframe.setVisible(true);
		mframe.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	public MainUIFrame(Socket socket, String name) {
		roomPanel.setBounds(0, 0,180*5+60, 70*8+40);
		userPanel.setBounds(970, 0, 200, 600);
		
		mframe.add(roomPanel);
		mframe.add(userPanel);
		mframe.setLayout(null);
		mframe.setSize(1200, 640);
		mframe.setVisible(true);
		mframe.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.clientSocket = socket;
		this.name = name;
		mainUIThread = new MainUIThread(clientSocket ,name, this);
		//ÿ�����洴����ʱ�򣬶����������ȡ��ǰ�û��б���ʾ���û�����
		//������ͨ���㲥�������µ��û��б����Բ�����Ҫ����ǰ�ͻ�������
		//mainui �ͻ����� allonline
//		mainUIThread.sendMessage("/mainui allonline");
	}

	
	public static void main(String[] args) {
		new MainUIFrame();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		for (int i=0; i<roomPanel.eachRoomPanels.length ;i++) {
			if (e.getSource()==roomPanel.eachRoomPanels[i].btn_left_user ||
					e.getSource()==roomPanel.eachRoomPanels[i].btn_right_user||
					e.getSource()==roomPanel.eachRoomPanels[i].btn_table) {
				System.out.println(roomPanel.eachRoomPanels[i].btn_table.getText());
			}
		}
		System.out.println("no find!!!");
	}


}
