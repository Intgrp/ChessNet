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
	 * ������Ϣ
	 */
	public void sendMessage( String sndMessage) {
		System.out.println("EachRoomThread������Ϣ��"+sndMessage);
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
		System.out.println("EachRoomThread������Ϣ��"+recMessage);
		if (recMessage.startsWith("/eachroomuserlist ")) {
			String result = recMessage.substring("/eachroomuserlist ".length());
			String[] lookUserList= result.split(" ");
			HashSet<String> tmp = new HashSet<String>();
			for (int i=0;i<lookUserList.length;i++) {
				tmp.add(lookUserList[i]);
			}
			System.out.println("EachRoomThread������Ϣset��"+tmp.toString());
			eachRoomFrame.chessRightPanel.userList.removeAll();
			Iterator<String> iterator = tmp.iterator();
			int flag1=0;
			int flag2=0;
			while (iterator.hasNext()) {
				String ss = iterator.next();
				eachRoomFrame.chessRightPanel.userList.add(ss);
				//����ڶ�ս��˫��û�г����ڹ�ս�б����˵�����˳��ˣ���ô��λ������Ϊû����
				if (eachRoomFrame.chessLeftPanel.label_head1.getText().equals(ss))
					flag1=1;
				if (eachRoomFrame.chessLeftPanel.label_head2.getText().equals(ss))
					flag2=1;
			}
			if (flag1==0) {
				eachRoomFrame.chessLeftPanel.label_head1.setText("��ǰû����");
			}
			if (flag2==0) {
				eachRoomFrame.chessLeftPanel.label_head2.setText("��ǰû����");
			}
		}
		else if (recMessage.startsWith("/compete ")) {
			String result = recMessage.substring("/compete ".length());
			String[] competeUsers = result.split(" ");
			if (!competeUsers[0].equals("null"))
				eachRoomFrame.chessLeftPanel.label_head1.setText("��ս����"+competeUsers[0]);
			else eachRoomFrame.chessLeftPanel.label_head1.setText("��ǰû����");
			if (!competeUsers[1].equals("null"))
				eachRoomFrame.chessLeftPanel.label_head2.setText("��ս����"+competeUsers[1]);
			else eachRoomFrame.chessLeftPanel.label_head2.setText("��ǰû����");
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
					System.out.println("����socketΪ��");
				}
			}
		} catch (IOException es) {
			es.printStackTrace();
		}
    }    
}
