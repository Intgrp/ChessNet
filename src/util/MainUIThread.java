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
        //�����汻������֮�󣬻�ȡ��ǰ�û��б����½����û��б���Ϣ
        sendMessage("/mainui allonline");
    }
	
	/**
	 * ������Ϣ
	 */
	public void sendMessage( String sndMessage) {
		System.out.println("MainUIThread������Ϣ��"+sndMessage);
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
		System.out.println("MainUIThread������Ϣ��"+recMessage);
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
//				mui.mainUIThread.sendMessage("/eachroomuserlist "+mui.roomId);
				eroomf = new EachRoomFrame(mui);
				eroomf.chessRightPanel.label_user.setText("�����ǳƣ�"+mui.name);
				mui.mframe.setVisible(false);
			}else if(result.equals("occupy")){
				//��λ���Ѿ������ˣ������սģʽ
				eroomf = new EachRoomFrame(mui);
				eroomf.chessBottomPanel.btn_fail.setEnabled(false);
				eroomf.chessBottomPanel.btn_prepare.setEnabled(false);
				eroomf.chessRightPanel.label_user.setText("�����ǳƣ�"+mui.name);
				mui.mframe.setVisible(false);
			}
		}
		else if (recMessage.startsWith("/mainui ")) {
			String result = recMessage.substring("/mainui ".length());
			
			//���Ϊ�գ���û���˽��뷿���ˣ������з��䶼��Ϊ��ʼͼ��
			if (result.equals("")) {
				for (int i=0;i<40;i++) {
					mui.roomPanel.eachRoomPanels[i].btn_left_user
					.setIcon(mui.roomPanel.eachRoomPanels[i].icon_person);
					mui.roomPanel.eachRoomPanels[i].btn_right_user
					.setIcon(mui.roomPanel.eachRoomPanels[i].icon_person);
				}
				mui.repaint();//ȫ��������ͼƬ��ˢ�½���
			}
			else {
				//�������Ϣ����Է������˵Ĳ�������Ϊͷ�񣬷���ҲҪ����ΪĬ��
				String[] tmp = result.split(" ");
				for (int i=0;i<tmp.length;i++) {
					String[] eachRoom = tmp[i].split(",");
					System.out.println("MainUIThread���յ���Ϣ��eachRoom="+eachRoom[0]+","+eachRoom[1]+","+eachRoom[2]);
					int roomId = Integer.valueOf(eachRoom[0]);
					//���Ŵ�1��ʼ�����Լ�һ��������������
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
				mui.repaint();//ȫ��������ͼƬ��ˢ�½���
			}
			
		}
		else {
			if (eroomf!=null) {
				eroomf.eachRoomThread.acceptMessage(recMessage);
			}
			else System.out.println("MainUIThread���յ���������"+recMessage);
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
