
package ui;

import java.awt.BorderLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 
 * �û��б�Panel����Panelά��һ���������ĵ�ǰ�û��б�
 * ���е��û���������ʾ���б��С�
 */
public class UserPanel extends JPanel {
	public List userList = new List(5);  //�ɹ������ı����б�
	
	public JTextArea tArea_notice = new JTextArea("�˴���ʾ֪ͨ");
	public JButton btn_exit = new JButton("�˳�");
	public JScrollPane jsp = new JScrollPane(tArea_notice);

	public UserPanel() {
		this.setLayout(new BorderLayout());
//		for (int i = 0; i < 30; i++) {
//			userList.add(i + "." + "��ǰ�����û�");
//		}
//		tArea_notice.setEditable(false);
		this.add(userList,  BorderLayout.NORTH); //��ӵ������м�����
		this.add(jsp,  BorderLayout.CENTER);
		this.add(btn_exit, BorderLayout.SOUTH);
		btn_exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==btn_exit) {
					System.out.println("ϵͳ�˳�������");
					System.exit(0);
				}
			}
		});
	}
	
	public static void main(String[] args) {
		JFrame jf = new JFrame("tttt");
//		jf.setLayout(null);
		jf.add(new UserPanel());
		jf.setVisible(true);
		jf.setSize(500, 600);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
