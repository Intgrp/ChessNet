package ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import util.Config;

public class ChessBottomPanel extends JPanel {
	
	public JButton btn_fail;
	public JButton btn_prepare;
	public JButton btn_exit;
	
	public ChessBottomPanel() {
		setLayout(new FlowLayout(FlowLayout.CENTER,50,20));
		setSize(Config.Chess_width, 100);
		btn_fail = new JButton("认输");
		btn_prepare = new JButton("准备");
		btn_exit = new JButton("退出");
		add(btn_fail);
		add(btn_prepare);
		add(btn_exit);
		
		btn_fail.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btn_prepare.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btn_exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public ChessBottomPanel(MainUIFrame mui, JFrame jf) {
		setLayout(new FlowLayout(FlowLayout.CENTER,50,20));
		setSize(Config.Chess_width, 100);
		btn_fail = new JButton("认输");
		btn_prepare = new JButton("准备");
		btn_exit = new JButton("退出");
		add(btn_fail);
		add(btn_prepare);
		add(btn_exit);
		
		btn_fail.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btn_prepare.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btn_exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jf.setVisible(false);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				mui.mframe.setVisible(true);
//				mui.mainUIThread.sendMessage("/roomleave "+jf.);
			}
		});
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame("tttt");
		jf.add(new ChessBottomPanel());
		jf.setVisible(true);
		jf.setSize(Config.Chess_width, 100);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
