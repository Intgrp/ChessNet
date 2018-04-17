package ui;

import javax.swing.JFrame;

import util.Config;

public class EachRoomFrame extends JFrame{
	
	public ChessLeftPanel chessLeftPanel;
	public ChessBoardPanel chessBoardPanel;
	public ChessRightPanel chessRightPanel;
	public ChessBottomPanel chessBottomPanel;
//	public JFrame jf;
	
	//默认构造函数
	public EachRoomFrame() {
		this.setTitle("User1");
		this.setLayout(null);
		chessLeftPanel = new ChessLeftPanel();
		chessBoardPanel = new ChessBoardPanel();
		chessRightPanel = new ChessRightPanel();
		chessBottomPanel = new ChessBottomPanel();
		
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
	}
	public EachRoomFrame(MainUIFrame mui) {
		this.setTitle(mui.name);
		this.setLayout(null);
		chessLeftPanel = new ChessLeftPanel();
		chessBoardPanel = new ChessBoardPanel();
		chessRightPanel = new ChessRightPanel();
		chessBottomPanel = new ChessBottomPanel(mui,this);
		
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
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EachRoomFrame();

	}

}
