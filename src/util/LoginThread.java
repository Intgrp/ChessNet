package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.shan.chesspanel.ChessPanel;

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
	 * ������Ϣ
	 */
	public void sendMessage( String sndMessage) {
		System.out.println("Login��ʼ������Ϣ��"+sndMessage);
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
			if (result.equals("ok")) {
				//ԭʼ��¼��������
				loginFrame.jf.setVisible(false);
				mainUIFrame = new MainUIFrame(socket, loginFrame.name);
				System.out.println("�ɹ��յ���Ϣ����¼��.......");
			}else if (result.equals("error")) {
				loginFrame.lable_result.setText("���û��Ѵ���");
			}
			else {
				System.out.println("������Ϣʧ�ܡ�");
			}
		}
		else {
			if (mainUIFrame!=null) {
				System.out.println("======LoginThread����Ϣת�ӵ�mainUIThread======");
				System.out.println("���յ���������"+recMessage);
				mainUIFrame.mainUIThread.acceptMessage(recMessage);
			}else {
				System.out.println("======δ��¼��LoginThread����Ϣ======");
				System.out.println("���յ���������"+recMessage);
			}
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
					System.out.println("������socketΪ��");
				}
			}
		} catch (IOException es) {
			es.printStackTrace();
		}
    }    
}