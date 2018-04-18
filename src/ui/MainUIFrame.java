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
	
	public JFrame mframe = new JFrame("五子棋客户端");
	
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
		//每当界面创建的时候，都向服务器获取当前用户列表显示在用户栏，
		//服务器通过广播更新最新的用户列表，所以并不需要传当前客户端名字
		//mainui 客户端名 allonline
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
