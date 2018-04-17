package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import util.Config;
import util.WZQ_listener;
import ui.ResultFrame;

public class ChessBoardPanel extends JFrame{

	public Graphics g;
	JPanel jp;
	public static Config chess_config;
	public static int GameModel = 1;

	public ChessBoardPanel() {
		super();
		this.initUI();
	}

	/**
	 * 初始化棋盘界面
	 */
	public void initUI() {
		/**
		 * 设置棋盘属性
		 */
//		this.setTitle("郭宋湖五子棋");
		this.setSize(new Dimension(650, 650));
		this.setResizable(false);
		this.setDefaultCloseOperation(3);
		/**
		 * 添加一块棋盘
		 */
		this.setLayout(null);
		JPanel jp = new JPanel() {

			@Override
			public void paint(Graphics g) {
				g.setColor(Color.BLACK);
				super.paint(g);
				System.out.println("g="+g);
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
		jp.setBackground(new Color(209, 167, 78));
		jp.setBounds(10, 10, 602, 602);
		this.add(jp);

		this.setVisible(true);
		g = jp.getGraphics();
		System.out.println("gg="+g);
		/**
		 * 添加监听器
		 * 
		 */
		WZQ_listener lis = new WZQ_listener(g);
		jp.addMouseListener(lis);

	}

	public static void main(String[] args) {
		new ChessBoardPanel();
//		JFrame jf = new JFrame("五子棋");
//		jf.add(new ChessBoardPanel());
//		jf.setVisible(true);
//		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		jf.setSize(Config.Chess_width, Config.Chess_high);

	}

}