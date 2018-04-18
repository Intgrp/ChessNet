package ui;

import java.awt.GridLayout;
import java.awt.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import util.Config;

/**
 * 
 * 用户列表Panel。此Panel维护一个服务器的当前用户列表，
 * 所有的用户名都将显示在列表中。
 */
public class ChessRightPanel extends JPanel {
	public JLabel label_user = new JLabel("用户1",JLabel.CENTER);
	
	public List userList = new List(20);  //可滚动的文本项列表
	
	public JTextArea tArea_notice = new JTextArea("此处显示通知");
	public JScrollPane jsp = new JScrollPane(tArea_notice);

	public ChessRightPanel() {
		this.setSize(Config.Chess_width/3, Config.Chess_high);
		this.setLayout(new GridLayout(3, 1));
//		for (int i = 0; i < 30; i++) {
//			userList.add(i + "." + "当前暂无用户");
//		}
//		tArea_notice.setEditable(false);
		this.add(label_user);
		this.add(userList); //添加到面板的中间区域
		this.add(jsp);
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED));

	}
	
	public static void main(String[] args) {
		JFrame jf = new JFrame("tttt");
		jf.add(new ChessRightPanel());
		jf.setVisible(true);
		jf.setSize((Integer) (Config.Chess_width / 3), Config.Chess_high);
//		jf.setSize(500, 600);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
