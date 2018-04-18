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
 * �û��б�Panel����Panelά��һ���������ĵ�ǰ�û��б�
 * ���е��û���������ʾ���б��С�
 */
public class ChessRightPanel extends JPanel {
	public JLabel label_user = new JLabel("�û�1",JLabel.CENTER);
	
	public List userList = new List(20);  //�ɹ������ı����б�
	
	public JTextArea tArea_notice = new JTextArea("�˴���ʾ֪ͨ");
	public JScrollPane jsp = new JScrollPane(tArea_notice);

	public ChessRightPanel() {
		this.setSize(Config.Chess_width/3, Config.Chess_high);
		this.setLayout(new GridLayout(3, 1));
//		for (int i = 0; i < 30; i++) {
//			userList.add(i + "." + "��ǰ�����û�");
//		}
//		tArea_notice.setEditable(false);
		this.add(label_user);
		this.add(userList); //��ӵ������м�����
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
