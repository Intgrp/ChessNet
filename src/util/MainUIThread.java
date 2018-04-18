package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;

import org.shan.chesspanel.ChessPanel;

import ui.EachRoomFrame;
import ui.LoginFrame;
import ui.MainUIFrame;

public class MainUIThread extends Thread{
	private Socket socket;
	public MainUIFrame mui;
	EachRoomFrame eroomf;
	
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
		System.out.println("MainUIThread发送消息："+sndMessage);
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
		System.out.println("MainUIThread接收消息："+recMessage);
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
//				mui.mainUIThread.sendMessage("/eachroomuserlist "+mui.roomId);
				eroomf = new EachRoomFrame(mui);
				eroomf.chessRightPanel.label_user.setText("本人昵称："+mui.name);
				mui.mframe.setVisible(false);
			}else if(result.equals("occupy")){
				//该位置已经有人了，进入观战模式
				eroomf = new EachRoomFrame(mui);
				eroomf.chessBottomPanel.btn_fail.setEnabled(false);
				eroomf.chessBottomPanel.btn_prepare.setEnabled(false);
				eroomf.chessRightPanel.label_user.setText("本人昵称："+mui.name);
				mui.mframe.setVisible(false);
			}
		}
		else if (recMessage.startsWith("/mainui ")) {
			String result = recMessage.substring("/mainui ".length());
			
			//如果为空，即没有人进入房间了，则所有房间都置为初始图标
			if (result.equals("")) {
				for (int i=0;i<40;i++) {
					mui.roomPanel.eachRoomPanels[i].btn_left_user
					.setIcon(mui.roomPanel.eachRoomPanels[i].icon_person);
					mui.roomPanel.eachRoomPanels[i].btn_right_user
					.setIcon(mui.roomPanel.eachRoomPanels[i].icon_person);
				}
				mui.repaint();//全部设置完图片后刷新界面
			}
			else {
				//如果有消息，则对房间有人的部分设置为头像，否则也要设置为默认
				String[] tmp = result.split(" ");
				for (int i=0;i<tmp.length;i++) {
					String[] eachRoom = tmp[i].split(",");
					System.out.println("MainUIThread接收到消息：eachRoom="+eachRoom[0]+","+eachRoom[1]+","+eachRoom[2]);
					int roomId = Integer.valueOf(eachRoom[0]);
					//桌号从1开始，所以减一才能做数组索引
					if (!eachRoom[1].equals("null")) {
						mui.roomPanel.eachRoomPanels[roomId-1].btn_left_user
						.setIcon(mui.roomPanel.eachRoomPanels[roomId-1].icon_select);
					}
					else {
						mui.roomPanel.eachRoomPanels[roomId-1].btn_left_user
						.setIcon(mui.roomPanel.eachRoomPanels[roomId-1].icon_person);
					}
					if (!eachRoom[2].equals("null")) {
						mui.roomPanel.eachRoomPanels[roomId-1].btn_right_user
						.setIcon(mui.roomPanel.eachRoomPanels[roomId-1].icon_select);
					}
					else {
						mui.roomPanel.eachRoomPanels[roomId-1].btn_right_user
						.setIcon(mui.roomPanel.eachRoomPanels[roomId-1].icon_person);
					}
				}
				mui.repaint();//全部设置完图片后刷新界面
			}
			
		}
		else {
			if (eroomf!=null) {
				eroomf.eachRoomThread.acceptMessage(recMessage);
			}
			else System.out.println("MainUIThread接收到其他请求："+recMessage);
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
