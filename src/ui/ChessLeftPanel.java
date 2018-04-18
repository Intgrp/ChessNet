package ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import util.Config;

public class ChessLeftPanel extends JPanel {

	public JLabel label_head1;
	public JLabel label_head2;
	public JLabel label_time1;
	public JLabel label_time2;

	public ChessLeftPanel() {
		setLayout(new GridLayout(4, 1));
		setSize((Integer) (Config.Chess_width / 3), Config.Chess_high);

		label_head1 = new JLabel("当前没有人",JLabel.CENTER);
		label_head2 = new JLabel("当前没有人",JLabel.CENTER);
		label_time1 = new JLabel("120 s",JLabel.CENTER);
		label_time2 = new JLabel("120 s",JLabel.CENTER);

		label_head1.setSize((Integer) (Config.Chess_width / 3), (Integer) (Config.Chess_high / 3));
		label_head2.setSize((Integer) (Config.Chess_width / 3), (Integer) (Config.Chess_high / 3));
		label_time1.setSize((Integer) (Config.Chess_width / 3), (Integer) (Config.Chess_high / 3));
		label_time2.setSize((Integer) (Config.Chess_width / 3), (Integer) (Config.Chess_high / 3));

		add(label_head1);
		add(label_time1);
		add(label_time2);
		add(label_head2);
		setVisible(true);
		setBorder(new EtchedBorder(EtchedBorder.RAISED));
	}

	public static void main(String[] args) {

		JFrame jf = new JFrame();
		ChessLeftPanel chessLeftPanel = new ChessLeftPanel();
		jf.add(chessLeftPanel);
		jf.setVisible(true);
		jf.setSize((Integer) (Config.Chess_width / 3), Config.Chess_high);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
