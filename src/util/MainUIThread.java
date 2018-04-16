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
        //当界面被创建的之后，获取当前用户列表，更新界面用户列表信息
        sendMessage("/mainui allonline");
    }
	
	/**
	 * 发送消息
	 */
	public void sendMessage( String sndMessage) {
		System.out.println("MainUI开始发送消息："+sndMessage);
		try {
			DataOutputStream outData = new DataOutputStream(socket.getOutputStream());
			outData.writeUTF(sndMessage);
		} catch (Exception ea) {
			ea.printStackTrace();
		}
	}

	/**
	 * 接收消息
	 */
	public void acceptMessage(String recMessage) {
		//如果收到的消息是 
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
				System.out.println("room 返回值error");
			}
		}
		else {
			System.out.println("======MainUI处理的消息======");
			System.out.println("接收到其他请求："+recMessage);
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
