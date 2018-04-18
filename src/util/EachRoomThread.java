package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.shan.chesspanel.ChessPanel;

import ui.EachRoomFrame;
import ui.LoginFrame;
import ui.MainUIFrame;

public class EachRoomThread extends Thread{
	private Socket socket;
	public MainUIFrame mui;
	public EachRoomFrame eachRoomFrame;
	
	public EachRoomThread(MainUIFrame  mainUIFrame, EachRoomFrame eachRoomFrame) {    
        socket = mainUIFrame.clientSocket;    
        mui = mainUIFrame;
        this.eachRoomFrame = eachRoomFrame;
    }
	
	/**
	 * 发送消息
	 */
	public void sendMessage( String sndMessage) {
		System.out.println("EachRoomThread发送消息："+sndMessage);
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
		System.out.println("EachRoomThread接收消息："+recMessage);
		if (recMessage.startsWith("/eachroomuserlist ")) {
			String result = recMessage.substring("/eachroomuserlist ".length());
			String[] lookUserList= result.split(" ");
			HashSet<String> tmp = new HashSet<String>();
			for (int i=0;i<lookUserList.length;i++) {
				tmp.add(lookUserList[i]);
			}
			System.out.println("EachRoomThread接收消息set："+tmp.toString());
			eachRoomFrame.chessRightPanel.userList.removeAll();
			Iterator<String> iterator = tmp.iterator();
			int flag1=0;
			int flag2=0;
			while (iterator.hasNext()) {
				String ss = iterator.next();
				eachRoomFrame.chessRightPanel.userList.add(ss);
				//如果在对战的双方没有出现在观战列表里，则说明其退出了，那么该位置设置为没有人
				if (eachRoomFrame.chessLeftPanel.label_head1.getText().equals(ss))
					flag1=1;
				if (eachRoomFrame.chessLeftPanel.label_head2.getText().equals(ss))
					flag2=1;
			}
			if (flag1==0) {
				eachRoomFrame.chessLeftPanel.label_head1.setText("当前没有人");
			}
			if (flag2==0) {
				eachRoomFrame.chessLeftPanel.label_head2.setText("当前没有人");
			}
		}
		else if (recMessage.startsWith("/compete ")) {
			String result = recMessage.substring("/compete ".length());
			String[] competeUsers = result.split(" ");
			if (!competeUsers[0].equals("null"))
				eachRoomFrame.chessLeftPanel.label_head1.setText("对战方："+competeUsers[0]);
			else eachRoomFrame.chessLeftPanel.label_head1.setText("当前没有人");
			if (!competeUsers[1].equals("null"))
				eachRoomFrame.chessLeftPanel.label_head2.setText("对战方："+competeUsers[1]);
			else eachRoomFrame.chessLeftPanel.label_head2.setText("当前没有人");
		}
	}

    public void run() {    
    	String message = "";
		try {
			while (true) {
				if (socket!=null) {
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
