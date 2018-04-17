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
	public static int GameModel = 1;// ģʽ��1Ϊ���˶�ս��2Ϊ�˻���ս
	

	/**
	 * ���úڰ����ʶ true��ʶ���ӣ�false��ʶ����
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
		// ��15��
		for (int i = 0; i < chess_config.ROW; i++) {
			g.drawLine(20, 20 + i * chess_config.Board_distance,
					20 + (chess_config.COLUMN - 1) * chess_config.Board_distance,
					20 + i * chess_config.Board_distance);
		}
		// ��15��
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
	 * ��ʼ�����̽���
	 */
	void initUI() {
		
		/**
		 * ������������
		 */
		setSize(new Dimension(Config.Chess_width, Config.Chess_high));
//		setLayout(null);
		/**
		 * ���һ������
		 */
		this.setLayout(null);
		this.setBackground(new Color(209, 167, 78));
		this.setBounds((Config.Chess_width-602)/2, (Config.Chess_high-602)/2, 602, 602);
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		this.setVisible(true);
		g = getGraphics();
		
		System.out.println("g:"+g);
		/**
		 * ��Ӽ�����
		 * 
		 */
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				x = correctXY(e.getX());
				y = correctXY(e.getY());
				System.out.println("x:" + x + "   y:" + y);
				/*
				 * �ж�Ϊ���˶�ս
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
	 * ����λ�����������ķ���
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
	 * ��Ӯ����
	 */

	/*
	 * �ж������������
	 */
	public boolean winRow(int row, int column) {
		int count = 1;
		for (int i = column + 1; i < 15; i++) {// ���Ҳ���
			if (array_win[row][column] == array_win[row][i]) {
				count++;
			} else
				break;
		}
		for (int i = column - 1; i >= 0; i--) {// �������
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
	 * �ж������������
	 */
	public boolean winColumn(int row, int column) {
		int count = 1;
		for (int i = row + 1; i < Config.ROW; i++) {// ���Ҳ���
			if (array_win[row][column] == array_win[i][column]) {
				count++;
			} else
				break;
		}
		for (int i = row - 1; i >= 0; i--) {// �������
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
	 * �ж�б�������������
	 */
	public boolean winRightDown(int row, int column) {
		int count = 1;
		for (int i = column + 1, j = row + 1; i < Config.ROW && j < Config.ROW; i++, j++) {// ���Ҳ���
			if (array_win[row][column] == array_win[j][i]) {
				count++;
			} else
				break;
		}
		for (int i = column - 1, j = row - 1; i >= 0 && j >= 0; i--, j--) {// �������
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
	 * �ж�б�������������
	 */
	public boolean winLeftDown(int row, int column) {
		int count = 1;
		for (int i = column - 1, j = row + 1; i >= 0 && j < Config.ROW; i--, j++) {// ���Ҳ���
			if (array_win[row][column] == array_win[j][i]) {
				count++;
			} else
				break;
		}
		for (int i = column + 1, j = row - 1; i < Config.ROW && j >= 0; i++, j--) {// �������
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
		// �ػ�����
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
		
		JFrame jf = new JFrame("������");
		jf.add(new ChessBoardPanel());
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(Config.Chess_width, Config.Chess_high);
	

	}

}
