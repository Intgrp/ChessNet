package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


import ui.LoginFrame;
import ui.MainUIFrame;

public class LoginThread extends Thread{
	private Socket socket;
	public LoginFrame loginFrame;
	MainUIFrame mainUIFrame;
	
	public LoginThread(Socket client, LoginFrame  clientFrame) {    
        socket = client;    
        loginFrame = clientFrame;
    }
	
	/**
	 * 发送消息
	 */
	public void sendMessage( String sndMessage) {
		System.out.println("LoginThread开始发送消息："+sndMessage);
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
			if (result.equals("ok")) {
				//原始登录界面隐藏
				loginFrame.jf.setVisible(false);
				mainUIFrame = new MainUIFrame(socket, loginFrame.name);
				System.out.println("LoginThread成功收到login消息，登录中.......");
			}else if (result.equals("error")) {
				loginFrame.lable_result.setText("该用户已存在");
			}
			else {
				System.out.println("接收消息失败。");
			}
		}
		else {
			if (mainUIFrame!=null) {
				mainUIFrame.mainUIThread.acceptMessage(recMessage);
			}else {
				System.out.println("======未登录的LoginThread的消息======");
				System.out.println("LoginThread接收到其他请求："+recMessage);
			}
		}
	}

    public void run() {    
    	String message = "";
		try {
			while (true) {
				if (socket.getInputStream()!=null) {
					message = new DataInputStream(socket.getInputStream()).readUTF();
					acceptMessage(message);
				}else {
					System.out.println("出错：socket为空");
				}
			}
		} catch (IOException es) {
			es.printStackTrace();
		}
    }    
}
