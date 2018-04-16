
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
 * 用户列表Panel。此Panel维护一个服务器的当前用户列表，
 * 所有的用户名都将显示在列表中。
 */
public class UserPanel extends JPanel {
	public List userList = new List(5);  //可滚动的文本项列表
	
	public JTextArea tArea_notice = new JTextArea("此处显示通知");
	public JButton btn_exit = new JButton("退出");
	public JScrollPane jsp = new JScrollPane(tArea_notice);

	public UserPanel() {
		this.setLayout(new BorderLayout());
//		for (int i = 0; i < 30; i++) {
//			userList.add(i + "." + "当前暂无用户");
//		}
//		tArea_notice.setEditable(false);
		this.add(userList,  BorderLayout.NORTH); //添加到面板的中间区域
		this.add(jsp,  BorderLayout.CENTER);
		this.add(btn_exit, BorderLayout.SOUTH);
		btn_exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==btn_exit) {
					System.out.println("系统退出！！！");
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
