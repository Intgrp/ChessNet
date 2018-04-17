package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import util.Config;

public class ChessBoardPanel extends JPanel {

	public static Config chess_config;
	public static int GameModel = 1;// 模式，1为人人对战，2为人机对战
	

	/**
	 * 设置黑白棋标识 true标识黑子，false标识白子
	 */
	public Graphics g;
	public boolean state = true;
	public int x, y;
	public String[][] array = new String[700][700];
	public int[][] array_win = new int[15][15];
	public int[][] array_pve = new int[15][15];
	public int count_max;
	public int count_where = 0;
	public ResultFrame result;
	public List list;
	public int val;

	public ChessBoardPanel() {
		this.initUI();
	}

	
	
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
		g.fillOval(135, 135, 10, 10);
		g.fillOval(295, 135, 10, 10);
		g.fillOval(455, 135, 10, 10);
		g.fillOval(135, 295, 10, 10);
		g.fillOval(295, 295, 10, 10);
		g.fillOval(455, 295, 10, 10);
		g.fillOval(135, 455, 10, 10);
		g.fillOval(295, 455, 10, 10);
		g.fillOval(455, 455, 10, 10);
	}


	/**
	 * 初始化棋盘界面
	 */
	void initUI() {
		
		/**
		 * 设置棋盘属性
		 */
		setSize(new Dimension(Config.Chess_width, Config.Chess_high));
//		setLayout(null);
		/**
		 * 添加一块棋盘
		 */
		this.setLayout(null);
		this.setBackground(new Color(209, 167, 78));
		this.setBounds((Config.Chess_width-602)/2, (Config.Chess_high-602)/2, 602, 602);
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		this.setVisible(true);
		g = getGraphics();
		
		System.out.println("g:"+g);
		/**
		 * 添加监听器
		 * 
		 */
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				x = correctXY(e.getX());
				y = correctXY(e.getY());
				System.out.println("x:" + x + "   y:" + y);
				/*
				 * 判定为人人对战
				 */
				if (GameModel == 1) {
					if (x < 582 && x >= 0 && y < 582 && y >= 0) {
						if (state && array[x][y] == null) {
							g.setColor(Color.BLACK);
							g.fillOval(x, y, Config.Chess_size, Config.Chess_size);

							array[x][y] = "black";

							array_win[getXY(y)][getXY(x)] = 1;
							state = false;

						} else if (array[x][y] == null) {
							g.setColor(Color.WHITE);
							g.fillOval(x, y, Config.Chess_size, Config.Chess_size);

							array[x][y] = "white";

							array_win[getXY(y)][getXY(x)] = -1;

							state = true;
						}

						if (Win(getXY(y), getXY(x)) == 1) {
							result = new ResultFrame(1);
							result.initUI();
						} else if (Win(getXY(y), getXY(x)) == -1) {
							result = new ResultFrame(-1);
							result.initUI();
						}
						for (int i = 0; i < 15; i++) {
							for (int j = 0; j < 15; j++) {
								System.out.print(array_win[i][j] + "  ");
							}
							System.out.println("");
						}
						System.out.println("");
					}
				}
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	/*
	 * 下棋位置坐标修正的方法
	 */
	public int correctXY(int x) {
		x = x / 40;

		return x * 40;
	}

	public int getXY(int x) {
		x = x / 40;
		return x;
	}

	/*
	 * 判赢方法
	 */

	/*
	 * 判定横向五个相连
	 */
	public boolean winRow(int row, int column) {
		int count = 1;
		for (int i = column + 1; i < 15; i++) {// 向右查找
			if (array_win[row][column] == array_win[row][i]) {
				count++;
			} else
				break;
		}
		for (int i = column - 1; i >= 0; i--) {// 向左查找
			if (array_win[row][column] == array_win[row][i]) {
				count++;
			} else
				break;
		}

		if (count >= 5) {
			return true;
		} else
			return false;
	}

	/*
	 * 判定竖向五个相连
	 */
	public boolean winColumn(int row, int column) {
		int count = 1;
		for (int i = row + 1; i < Config.ROW; i++) {// 向右查找
			if (array_win[row][column] == array_win[i][column]) {
				count++;
			} else
				break;
		}
		for (int i = row - 1; i >= 0; i--) {// 向左查找
			if (array_win[row][column] == array_win[i][column]) {
				count++;
			} else
				break;
		}
		if (count >= 5) {
			return true;
		} else
			return false;
	}

	/*
	 * 判定斜向右下五个相连
	 */
	public boolean winRightDown(int row, int column) {
		int count = 1;
		for (int i = column + 1, j = row + 1; i < Config.ROW && j < Config.ROW; i++, j++) {// 向右查找
			if (array_win[row][column] == array_win[j][i]) {
				count++;
			} else
				break;
		}
		for (int i = column - 1, j = row - 1; i >= 0 && j >= 0; i--, j--) {// 向左查找
			if (array_win[row][column] == array_win[j][i]) {
				count++;
			} else
				break;
		}
		if (count >= 5) {
			return true;
		} else
			return false;
	}

	/*
	 * 判定斜向左下五个相连
	 */
	public boolean winLeftDown(int row, int column) {
		int count = 1;
		for (int i = column - 1, j = row + 1; i >= 0 && j < Config.ROW; i--, j++) {// 向右查找
			if (array_win[row][column] == array_win[j][i]) {
				count++;
			} else
				break;
		}
		for (int i = column + 1, j = row - 1; i < Config.ROW && j >= 0; i++, j--) {// 向左查找
			if (array_win[row][column] == array_win[j][i]) {
				count++;
			} else
				break;
		}
		if (count >= 5) {
			return true;
		} else
			return false;
	}

	public int Win(int row, int column) {
		if (winRow(row, column) || winColumn(row, column) || winRightDown(row, column) || winLeftDown(row, column)) {
			if (array_win[row][column] == 1)
				return 1;
			else if (array_win[row][column] == -1)
				return -1;
		}
		return 0;
	}
	public void refresh() {
		// 重绘棋子
		for (int i = 0; i < Config.Chess_width; i++) {
			for (int j = 0; j < Config.Chess_high; j++) {
				if (array[i][j] == "black") {
					g.setColor(Color.BLACK);
					g.fillOval(i, j, chess_config.Chess_size, chess_config.Chess_size);
				} else if (array[i][j] == "white") {
					g.setColor(Color.WHITE);
					g.fillOval(i, j, chess_config.Chess_size, chess_config.Chess_size);
				}
			}
		}
	}

	public static void main(String[] args) {
		
		JFrame jf = new JFrame("五子棋");
		jf.add(new ChessBoardPanel());
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(Config.Chess_width, Config.Chess_high);
	

	}

}
