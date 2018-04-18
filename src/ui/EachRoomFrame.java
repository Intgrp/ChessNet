package ui;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;

import util.Config;
import util.EachRoomThread;
import util.WZQ_listener;

public class EachRoomFrame extends JFrame{
	
	public ChessLeftPanel chessLeftPanel;
	public JPanel chessBoardPanel;
	public ChessRightPanel chessRightPanel;
	public ChessBottomPanel chessBottomPanel;
	
	public Graphics g;
	public static Config chess_config;
	public static int GameModel = 1;
	
	public EachRoomThread eachRoomThread;
	
	//默认构造函数
	public EachRoomFrame() {
		this.setTitle("User1");
		this.setLayout(null);
		chessLeftPanel = new ChessLeftPanel();
		chessRightPanel = new ChessRightPanel();
		chessBottomPanel = new ChessBottomPanel();
		
		chessBoardPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.BLACK);
				super.paint(g);
				// 画15行
				for (int i = 0; i < chess_config.ROW; i++) {
					g.drawLine(20, 20 + i * chess_config.Board_distance,
							20 + (chess_config.COLUMN - 1) * chess_config.Board_distance,
							20 + i * chess_config.Board_distance);
				}
				// 画15列
				for (int i = 0; i < chess_config.COLUMN; i++) {
					g.drawLine(20 + i * chess_config.Board_distance, 20, 20 + i * chess_config.Board_distance,
							20 + (chess_config.ROW - 1) * chess_config.Board_distance);
				}
				g.setColor(Color.BLACK);
				g.fillOval(133, 133, 15, 15);
				g.fillOval(293, 133, 15, 15);
				g.fillOval(453, 133, 15, 15);
				g.fillOval(133, 293, 15, 15);
				g.fillOval(293, 293, 15, 15);
				g.fillOval(453, 293, 15, 15);
				g.fillOval(133, 453, 15, 15);
				g.fillOval(293, 453, 15, 15);
				g.fillOval(453, 453, 15, 15);
			
			}
		};
		
		chessBoardPanel.setBackground(new Color(209, 167, 78));
		
		
		chessLeftPanel.setBounds(0, 0, Config.Chess_width/3, Config.Chess_high+100);
		chessBoardPanel.setBounds(Config.Chess_width/3, 0, Config.Chess_width, Config.Chess_high);
		chessRightPanel.setBounds(Config.Chess_width*4/3, 0, Config.Chess_width/3, Config.Chess_high+100);
		chessBottomPanel.setBounds(Config.Chess_width/3,  Config.Chess_high, Config.Chess_width, 100);
		
		this.add(chessLeftPanel);
		this.add(chessBoardPanel);
		this.add(chessRightPanel);
		this.add(chessBottomPanel);
		this.setSize(Config.Chess_width*5/3, Config.Chess_high+100);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		g = chessBoardPanel.getGraphics();
		WZQ_listener lis = new WZQ_listener(g);
		chessBoardPanel.addMouseListener(lis);
		
		
		
	}
	public EachRoomFrame(MainUIFrame mui) {
		this.setTitle("房间号："+mui.roomId+" 用户名："+":"+mui.name);
		this.setLayout(null);
		chessLeftPanel = new ChessLeftPanel();
		chessRightPanel = new ChessRightPanel();
		chessBottomPanel = new ChessBottomPanel(mui,this);
		
		chessBoardPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.BLACK);
				super.paint(g);
				// 画15行
				for (int i = 0; i < chess_config.ROW; i++) {
					g.drawLine(20, 20 + i * chess_config.Board_distance,
							20 + (chess_config.COLUMN - 1) * chess_config.Board_distance,
							20 + i * chess_config.Board_distance);
				}
				// 画15列
				for (int i = 0; i < chess_config.COLUMN; i++) {
					g.drawLine(20 + i * chess_config.Board_distance, 20, 20 + i * chess_config.Board_distance,
							20 + (chess_config.ROW - 1) * chess_config.Board_distance);
				}
				g.setColor(Color.BLACK);
				g.fillOval(133, 133, 15, 15);
				g.fillOval(293, 133, 15, 15);
				g.fillOval(453, 133, 15, 15);
				g.fillOval(133, 293, 15, 15);
				g.fillOval(293, 293, 15, 15);
				g.fillOval(453, 293, 15, 15);
				g.fillOval(133, 453, 15, 15);
				g.fillOval(293, 453, 15, 15);
				g.fillOval(453, 453, 15, 15);
			
			}
		};
		
		chessBoardPanel.setBackground(new Color(209, 167, 78));
		
		chessLeftPanel.setBounds(0, 0, Config.Chess_width/3, Config.Chess_high+100);
		chessBoardPanel.setBounds(Config.Chess_width/3, 0, Config.Chess_width, Config.Chess_high);
		chessRightPanel.setBounds(Config.Chess_width*4/3, 0, Config.Chess_width/3, Config.Chess_high+100);
		chessBottomPanel.setBounds(Config.Chess_width/3,  Config.Chess_high, Config.Chess_width, 100);
		
		this.add(chessLeftPanel);
		this.add(chessBoardPanel);
		this.add(chessRightPanel);
		this.add(chessBottomPanel);
		this.setSize(Config.Chess_width*5/3, Config.Chess_high+100);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		g = chessBoardPanel.getGraphics();
		WZQ_listener lis = new WZQ_listener(g);
		chessBoardPanel.addMouseListener(lis);
		
//		mui.mainUIThread.sendMessage("/eachroomuserlist "+mui.roomId);
		eachRoomThread = new EachRoomThread(mui,this);
		//获得当前房间观战人员列表
		eachRoomThread.sendMessage("/eachroomuserlist "+mui.roomId);
		//获得棋盘正在对战的双方人员
		eachRoomThread.sendMessage("/compete "+mui.roomId);
	}

	public static void main(String[] args) {
		new EachRoomFrame();

	}
}
