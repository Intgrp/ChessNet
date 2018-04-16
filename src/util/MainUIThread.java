package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;

import org.shan.chesspanel.ChessPanel;

import ui.ChessBoardPanel;
import ui.EachRoomFrame;
import ui.LoginFrame;
import ui.MainUIFrame;

public class MainUIThread extends Thread{
	private Socket socket;
	public MainUIFrame mui;
	
	public MainUIThread(Socket client, String clientName, MainUIFrame curUI) {    
        socket = client;    
        mui = curUI;
        //�����汻������֮�󣬻�ȡ��ǰ�û��б����½����û��б���Ϣ
        sendMessage("/mainui allonline");
    }
	
	/**
	 * ������Ϣ
	 */
	public void sendMessage( String sndMessage) {
		System.out.println("MainUI��ʼ������Ϣ��"+sndMessage);
		try {
			DataOutputStream outData = new DataOutputStream(socket.getOutputStream());
			outData.writeUTF(sndMessage);
		} catch (Exception ea) {
			ea.printStackTrace();
		}
	}

	/**
	 * ������Ϣ
	 */
	public void acceptMessage(String recMessage) {
		//����յ�����Ϣ�� 
		if (recMessage.startsWith("/login ")) {
			String result = recMessage.substring("/login ".length());
			
		}else if (recMessage.startsWith("/userlist ")) {
			String [] list = recMessage.substring("/userlist ".length()).split(" ");
			mui.userPanel.userList.clear();
			for (int i = 0; i < list.length; i++) {
	        	 mui.userPanel.userList.add(list[i]);
			}
		}
		else if (recMessage.startsWith("/room ")) {
			String result = recMessage.substring("/room ".length());
			if (result.equals("ok")) {
				EachRoomFrame eroomf = new EachRoomFrame();
				mui.mframe.setVisible(false);
			}else {
				System.out.println("room ����ֵerror");
			}
		}
		else {
			System.out.println("======MainUI�������Ϣ======");
			System.out.println("���յ���������"+recMessage);
		}
	}

    public void run() {    
    	String message = "";
		try {
			while (true) {
				message = new DataInputStream(socket.getInputStream()).readUTF();
				acceptMessage(message);
			}
		} catch (IOException es) {
			es.printStackTrace();
		}
    }    
    
    public static void main(String[] args) {
	}
}
