package ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import util.Config;

public class EachRoomFrame extends JFrame{
	
	public ChessLeftPanel chessLeftPanel;
	public ChessBoardPanel chessBoardPanel;
	public ChessRightPanel chessRightPanel;
	public ChessBottomPanel chessBottomPanel;
	public JFrame jf;
	
	//默认构造函数
	public EachRoomFrame() {
		jf = new JFrame("User1");
		jf.setLayout(null);
		chessLeftPanel = new ChessLeftPanel();
		chessBoardPanel = new ChessBoardPanel();//增加了getGraphics()
		chessRightPanel = new ChessRightPanel();
		chessBottomPanel = new ChessBottomPanel();
		
		chessLeftPanel.setBounds(0, 0, Config.Chess_width/3, Config.Chess_high+100);
		chessBoardPanel.setBounds(Config.Chess_width/3, 0, Config.Chess_width, Config.Chess_high);
		chessRightPanel.setBounds(Config.Chess_width*4/3, 0, Config.Chess_width/3, Config.Chess_high+100);
		chessBottomPanel.setBounds(Config.Chess_width/3,  Config.Chess_high, Config.Chess_width, 100);
		
		jf.add(chessLeftPanel);
		jf.add(chessBoardPanel);
		jf.add(chessRightPanel);
		jf.add(chessBottomPanel);
		jf.setSize(Config.Chess_width*5/3, Config.Chess_high+100);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public EachRoomFrame(MainUIFrame mui) {
		jf = new JFrame(mui.name);
		jf.setLayout(null);
		chessLeftPanel = new ChessLeftPanel();
		chessBoardPanel = new ChessBoardPanel();
		chessRightPanel = new ChessRightPanel();
		chessBottomPanel = new ChessBottomPanel(mui,jf);
		
		chessLeftPanel.setBounds(0, 0, Config.Chess_width/3, Config.Chess_high+100);
		chessBoardPanel.setBounds(Config.Chess_width/3, 0, Config.Chess_width, Config.Chess_high);
		chessRightPanel.setBounds(Config.Chess_width*4/3, 0, Config.Chess_width/3, Config.Chess_high+100);
		chessBottomPanel.setBounds(Config.Chess_width/3,  Config.Chess_high, Config.Chess_width, 100);
		
		jf.add(chessLeftPanel);
		jf.add(chessBoardPanel);
		jf.add(chessRightPanel);
		jf.add(chessBottomPanel);
		jf.setSize(Config.Chess_width*5/3, Config.Chess_high+100);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EachRoomFrame();

	}

}
