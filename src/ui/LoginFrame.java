package ui;


import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


import util.LoginThread;

public class LoginFrame extends JFrame{
	public JFrame jf = new JFrame("��¼");
	public JLabel label_name = new JLabel("�ǳƣ�");
	public JTextField txt_name = new JTextField();
	public JButton btn_login = new JButton("��¼");
	public JLabel lable_result = new JLabel("");
	
	public Socket socket ;
	public String host = "127.0.0.1";
	public int port = 4331;
	public LoginThread clientthread;
	public String name="";
	public LoginFrame() {
		
		jf.setLayout(null);//���������Ĭ����ʧ���֣�ҳ����������Ҳû��
		jf.setBounds(300, 300, 300, 200);
		label_name.setBounds(50, 40, 50, 30);
		txt_name.setBounds(100, 40, 110, 25);
		btn_login.setBounds(110,80 , 70, 30);
		lable_result.setBounds(90, 120, 100, 30);
		
		jf.add(label_name);
		jf.add(txt_name);
		jf.add(btn_login);
		jf.add(lable_result);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		/**
		 * 
		 */
		btn_login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				name = txt_name.getText();
				try {
					if (connectServer(host, port)) {
						clientthread.sendMessage("/login "+txt_name.getText());
//						jf.setVisible(false);
					}
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		/**
		 * �رս������Ӧ�¼�
		 */
		jf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					socket.getInputStream().close();
					socket.close();
					clientthread.stop();
				} catch (IOException e1) {
					System.out.println("login  socket�ر��쳣");
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
//		jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * �ͷ������������Ӳ�ͨ�ŵĺ�����
	 */
	public boolean connectServer(String serverIP, int serverPort)
			throws Exception {
		try {
			socket = new Socket(serverIP, serverPort);
			clientthread = new LoginThread(socket,this);
			clientthread.start();
			return true;
		} catch (IOException ex) {
			System.out.println("�޷����ӣ�����δ����������������������������");
			lable_result.setText("connectServer:�޷�����,���������������� \n");
			System.exit(0);
		}
		return false;
	}
	
	public static void main(String args[]) {
		 new LoginFrame();
	}
}
