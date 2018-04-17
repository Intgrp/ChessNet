package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class EachRoomPanel extends JPanel {
	public JButton btn_left_user;
	public JButton btn_right_user;
	public JButton btn_table;
	public ImageIcon icon_desk = new ImageIcon("E:\\workspace\\eclipse-workspace\\ChessNet\\src\\resources\\desk.png");  
	public ImageIcon icon_person = new ImageIcon("E:\\workspace\\eclipse-workspace\\ChessNet\\src\\resources\\person.png"); 
	public ImageIcon icon_select = new ImageIcon("E:\\workspace\\eclipse-workspace\\ChessNet\\src\\resources\\face.png");
	
	public EachRoomPanel() {
		setLayout(new FlowLayout());
		btn_left_user = new JButton("?",icon_person);
		btn_left_user.setBackground(new Color(62, 86, 116));
		btn_right_user = new JButton("?",icon_person);
		btn_right_user.setBackground(new Color(62, 86, 116));
		btn_table = new JButton(icon_desk);
		btn_table.setBackground(new Color(206, 164, 133));
		
		btn_left_user.setBounds(5, 10, 40, 40);
		btn_table.setBounds(50,5,50,50);
		btn_right_user.setBounds(105, 10, 40, 40);
		
		//去边框
		btn_left_user.setBorderPainted(false); 
		btn_table.setBorderPainted(false); 
		btn_right_user.setBorderPainted(false); 
		
		add(btn_left_user);
		add(btn_table);
		add(btn_right_user);
		setSize(165,100);
		setVisible(true);
	}
	//用于测试用的默认构造函数的一个
	public EachRoomPanel(String tableIndex) {
		setLayout(null);
		setBackground(new Color(5, 67, 108));
		btn_left_user = new JButton("?",icon_person);
		btn_left_user.setBackground(new Color(62, 86, 116));
		btn_right_user = new JButton("?",icon_person);
		btn_right_user.setBackground(new Color(62, 86, 116));
		btn_table = new JButton(tableIndex,icon_desk);
		btn_table.setBackground(new Color(206, 164, 133));
		
		btn_left_user.setBounds(5, 10, 50, 50);
		btn_table.setBounds(60,5,60,60);
		btn_right_user.setBounds(125, 10, 50, 50);
		
		//去边框
		btn_left_user.setBorderPainted(false); 
		btn_table.setBorderPainted(false); 
		btn_right_user.setBorderPainted(false); 
		
		add(btn_left_user);
		add(btn_right_user);
		add(btn_table);
		setSize(180,70);
		
		btn_left_user.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(btn_table.getText() + "btn_left_user");
				
			}
		});
		
		btn_right_user.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(btn_table.getText() + "btn_right_user");
			}
		});
		
		btn_table.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(btn_table.getText() + "btn_table");
			}
		});
		
		setVisible(true);
	}
	
	//用于窗口给父窗口传递数据
	public EachRoomPanel(String tableIndex, MainUIFrame mui) {
		setLayout(null);
		setBackground(new Color(5, 67, 108));
		btn_left_user = new JButton(icon_person);
		btn_left_user.setBackground(new Color(62, 86, 116));
		btn_right_user = new JButton(icon_person);
		btn_right_user.setBackground(new Color(62, 86, 116));
//		btn_table = new JButton(tableIndex,icon_desk);
		btn_table = new JButton(tableIndex);
		btn_table.setBackground(new Color(145, 145, 145));
		
		btn_left_user.setBounds(5, 10, 50, 50);
		btn_table.setBounds(60,5,60,60);
		btn_right_user.setBounds(125, 10, 50, 50);
		
		//去边框
		btn_left_user.setBorderPainted(false); 
		btn_table.setBorderPainted(false); 
		btn_right_user.setBorderPainted(false); 
		
		
		add(btn_left_user);
		add(btn_right_user);
		add(btn_table);
		setSize(180,70);
		
		btn_left_user.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(btn_table.getText() + "btn_left_user");
				mui.mainUIThread.sendMessage("/room "+btn_table.getText()+" "+mui.name+" null");
				mui.mainUIThread.sendMessage("/mainui allonline");
			}
		});
		
		btn_right_user.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(btn_table.getText() + "btn_right_user");
				mui.mainUIThread.sendMessage("/room "+btn_table.getText()+" "+"null "+mui.name);
				mui.mainUIThread.sendMessage("/mainui allonline");
			}
		});
		
		btn_table.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(btn_table.getText() + "btn_table");
				mui.mainUIThread.sendMessage("/room "+btn_table.getText()+" "+"null "+mui.name);
				mui.mainUIThread.sendMessage("/mainui allonline");
			}
		});
		
		setVisible(true);
	}


	public static void main(String[] args) {
		JFrame jf = new JFrame("test");
		jf.setLayout(null);
		jf.setVisible(true);
		jf.setSize(180, 60);
		jf.add(new EachRoomPanel());
	}
}
