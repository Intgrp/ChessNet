package org.shan.chesspanel;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 *   
 * ��ʾ���̵�Panel����Panelʵ������������
 */
public class ChessPanel extends Panel implements MouseListener {
	public int chessPoint_x = -1, chessPoint_y = -1, chessColor = 1;

	int chessBlack_x[] = new int[200];//���ӵ�x����

	int chessBlack_y[] = new int[200];//���ӵ�y����

	int chessWhite_x[] = new int[200];//���ӵ�x����

	int chessWhite_y[] = new int[200];//���ӵ�y����

	int chessBlackCount = 0, chessWhiteCount = 0;

	int chessBlackWin = 0, chessWhiteWin = 0;

	public boolean isMouseEnabled = false, isWin = false, isInGame = false;

	public JLabel statusLabel = new JLabel("�ͻ���״̬");

	public JTextField statusText = new JTextField("�������ӷ�����");//��ʾ�ͻ���״̬���ı���

	public Socket chessSocket;

	DataInputStream inData;

	DataOutputStream outData;

	public String chessSelfName = null;//����������

	public String chessPeerName = null;//�Է�������

	public String host = null;

	public int port = 4331;

	public ChessThread chessthread = new ChessThread(this);

	/**
	 * ����Panel�Ĺ��캯��
	 */
	public ChessPanel() {
		setSize(440, 440);
		setLayout(null);
		setBackground(new Color(204, 204, 204));
		addMouseListener(this);
		add(statusLabel);
		statusLabel.setBounds(30, 5, 70, 24);
		add(statusText);
		statusText.setBounds(100, 5, 300, 24);
		statusText.setEditable(false);
	}

	/**
	 * �ͷ�����ͨ�ŵĺ���
	 */
	public boolean connectServer(String ServerIP, int ServerPort)
			throws Exception {
		try {
			//���ò�������һ��Socket��ʵ������ɺͷ�����֮�����Ϣ����
			chessSocket = new Socket(ServerIP, ServerPort);
			inData = new DataInputStream(chessSocket.getInputStream());
			outData = new DataOutputStream(chessSocket.getOutputStream());
			chessthread.start();
			return true;
		} catch (IOException ex) {
			statusText.setText("chessPad:connectServer:�޷����� \n");
		}
		return false;
	}

	/**
	 * һ����ʤʱ�Ķ���ֵĴ���
	 */
	public void chessVictory(int chessColorWin) {
		//������е�����
		this.removeAll();
		//���������к���Ͱ����λ�������������գ�Ϊ��һ������׼����
		for (int i = 0; i <= chessBlackCount; i++) {
			chessBlack_x[i] = 0;
			chessBlack_y[i] = 0;
		}
		for (int i = 0; i <= chessWhiteCount; i++) {
			chessWhite_x[i] = 0;
			chessWhite_y[i] = 0;
		}
		chessBlackCount = 0;
		chessWhiteCount = 0;
		add(statusText);
		statusText.setBounds(40, 5, 360, 24);
		//��������ʤ������˫����ʤ��������˫����ս������״̬�ı�����ʾ������
		if (chessColorWin == 1) {
			chessBlackWin++;
			statusText.setText("����ʤ,��:��Ϊ" + chessBlackWin + ":" + chessWhiteWin
					+ ",���¿���,�ȴ���������...");
		}
		//�����ʤ��ͬ�ϡ�
		else if (chessColorWin == -1) {
			chessWhiteWin++;
			statusText.setText("����ʤ,��:��Ϊ" + chessBlackWin + ":" + chessWhiteWin
					+ ",���¿���,�ȴ���������...");
		}
	}

	/**
	 * ���������ӵ����걣����������
	 */
	public void getLocation(int a, int b, int color) {

		if (color == 1) {
			chessBlack_x[chessBlackCount] = a * 20;
			chessBlack_y[chessBlackCount] = b * 20;
			chessBlackCount++;
		} else if (color == -1) {
			chessWhite_x[chessWhiteCount] = a * 20;
			chessWhite_y[chessWhiteCount] = b * 20;
			chessWhiteCount++;
		}
	}

	/**
	 * �������������������ж�ĳ����ʤ
	 */
	public boolean checkWin(int a, int b, int checkColor) {
		int step = 1, chessLink = 1, chessLinkTest = 1, chessCompare = 0;
		if (checkColor == 1) {
			chessLink = 1;
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessBlackCount; chessCompare++) {
					if (((a + step) * 20 == chessBlack_x[chessCompare])
							&& ((b * 20) == chessBlack_y[chessCompare])) {
						chessLink = chessLink + 1;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessBlackCount; chessCompare++) {
					if (((a - step) * 20 == chessBlack_x[chessCompare])
							&& (b * 20 == chessBlack_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			chessLink = 1;
			chessLinkTest = 1;
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessBlackCount; chessCompare++) {
					if ((a * 20 == chessBlack_x[chessCompare])
							&& ((b + step) * 20 == chessBlack_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessBlackCount; chessCompare++) {
					if ((a * 20 == chessBlack_x[chessCompare])
							&& ((b - step) * 20 == chessBlack_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			chessLink = 1;
			chessLinkTest = 1;
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessBlackCount; chessCompare++) {
					if (((a - step) * 20 == chessBlack_x[chessCompare])
							&& ((b + step) * 20 == chessBlack_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessBlackCount; chessCompare++) {
					if (((a + step) * 20 == chessBlack_x[chessCompare])
							&& ((b - step) * 20 == chessBlack_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			chessLink = 1;
			chessLinkTest = 1;
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessBlackCount; chessCompare++) {
					if (((a + step) * 20 == chessBlack_x[chessCompare])
							&& ((b + step) * 20 == chessBlack_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessBlackCount; chessCompare++) {
					if (((a - step) * 20 == chessBlack_x[chessCompare])
							&& ((b - step) * 20 == chessBlack_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
		} else if (checkColor == -1) {
			chessLink = 1;
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessWhiteCount; chessCompare++) {
					if (((a + step) * 20 == chessWhite_x[chessCompare])
							&& (b * 20 == chessWhite_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessWhiteCount; chessCompare++) {
					if (((a - step) * 20 == chessWhite_x[chessCompare])
							&& (b * 20 == chessWhite_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			chessLink = 1;
			chessLinkTest = 1;
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessWhiteCount; chessCompare++) {
					if ((a * 20 == chessWhite_x[chessCompare])
							&& ((b + step) * 20 == chessWhite_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessWhiteCount; chessCompare++) {
					if ((a * 20 == chessWhite_x[chessCompare])
							&& ((b - step) * 20 == chessWhite_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			chessLink = 1;
			chessLinkTest = 1;
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessWhiteCount; chessCompare++) {
					if (((a - step) * 20 == chessWhite_x[chessCompare])
							&& ((b + step) * 20 == chessWhite_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessWhiteCount; chessCompare++) {
					if (((a + step) * 20 == chessWhite_x[chessCompare])
							&& ((b - step) * 20 == chessWhite_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			chessLink = 1;
			chessLinkTest = 1;
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessWhiteCount; chessCompare++) {
					if (((a + step) * 20 == chessWhite_x[chessCompare])
							&& ((b + step) * 20 == chessWhite_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
			for (step = 1; step <= 4; step++) {
				for (chessCompare = 0; chessCompare <= chessWhiteCount; chessCompare++) {
					if (((a - step) * 20 == chessWhite_x[chessCompare])
							&& ((b - step) * 20 == chessWhite_y[chessCompare])) {
						chessLink++;
						if (chessLink == 5) {
							return (true);
						}
					}
				}
				if (chessLink == (chessLinkTest + 1))
					chessLinkTest++;
				else
					break;
			}
		}
		return (false);
	}

	/**
	 * �������̣������̻��Ƴ�19*19�ĸ��Ӳ���������Ӧ�е�����������ȥ��
	 */
	public void paint(Graphics g) {
		for (int i = 40; i <= 380; i = i + 20) {
			g.drawLine(40, i, 400, i);
		}
		g.drawLine(40, 400, 400, 400);
		for (int j = 40; j <= 380; j = j + 20) {
			g.drawLine(j, 40, j, 400);
		}
		g.drawLine(400, 40, 400, 400);
		g.fillOval(97, 97, 6, 6);
		g.fillOval(337, 97, 6, 6);
		g.fillOval(97, 337, 6, 6);
		g.fillOval(337, 337, 6, 6);
		g.fillOval(217, 217, 6, 6);
	}

	/**
	 * ���ӵ�ʱ���������
	 */
	public void chessPaint(int chessPoint_a, int chessPoint_b, int color) {
		chessPoint_black chesspoint_black = new chessPoint_black(this);
		chessPoint_white chesspoint_white = new chessPoint_white(this);

		if (color == 1 && isMouseEnabled) {
			//����������ʱ�����´��ӵ�λ��
			getLocation(chessPoint_a, chessPoint_b, color);
			//�ж��Ƿ��ʤ
			isWin = checkWin(chessPoint_a, chessPoint_b, color);
			if (isWin == false) {
				//���û�л�ʤ����Է�����������Ϣ������������
				chessthread.sendMessage("/" + chessPeerName + " /chess "
						+ chessPoint_a + " " + chessPoint_b + " " + color);
				this.add(chesspoint_black);
				chesspoint_black.setBounds(chessPoint_a * 20 - 7,
						chessPoint_b * 20 - 7, 16, 16);
				//��״̬�ı�����ʾ������Ϣ
				statusText.setText("��(��" + chessBlackCount + "��)"
						+ chessPoint_a + " " + chessPoint_b + ",���������");
				isMouseEnabled = false;
			} else {
				//�����ʤ��ֱ�ӵ���chessVictory��ɺ�������
				chessthread.sendMessage("/" + chessPeerName + " /chess "
						+ chessPoint_a + " " + chessPoint_b + " " + color);
				this.add(chesspoint_black);
				chesspoint_black.setBounds(chessPoint_a * 20 - 7,
						chessPoint_b * 20 - 7, 16, 16);
				chessVictory(1);
				isMouseEnabled = false;
			}
		}
		//�������ӣ�ͬ�������ƴ���
		else if (color == -1 && isMouseEnabled) {
			getLocation(chessPoint_a, chessPoint_b, color);
			isWin = checkWin(chessPoint_a, chessPoint_b, color);
			if (isWin == false) {
				chessthread.sendMessage("/" + chessPeerName + " /chess "
						+ chessPoint_a + " " + chessPoint_b + " " + color);
				this.add(chesspoint_white);
				chesspoint_white.setBounds(chessPoint_a * 20 - 7,
						chessPoint_b * 20 - 7, 16, 16);
				statusText.setText("��(��" + chessWhiteCount + "��)"
						+ chessPoint_a + " " + chessPoint_b + ",���������");
				isMouseEnabled = false;
			} else {
				chessthread.sendMessage("/" + chessPeerName + " /chess "
						+ chessPoint_a + " " + chessPoint_b + " " + color);
				this.add(chesspoint_white);
				chesspoint_white.setBounds(chessPoint_a * 20 - 7,
						chessPoint_b * 20 - 7, 16, 16);
				chessVictory(-1);
				isMouseEnabled = false;
			}
		}
	}

	/**
	 * ����ʱ�ڶԷ��ͻ��˻������ӡ� �Է����ܵ���������������Ϣ�����ô˺����������ӣ���ʾ����״̬�ȵ�
	 */
	public void netChessPaint(int chessPoint_a, int chessPoint_b, int color) {
		chessPoint_black chesspoint_black = new chessPoint_black(this);
		chessPoint_white chesspoint_white = new chessPoint_white(this);
		getLocation(chessPoint_a, chessPoint_b, color);
		if (color == 1) {
			isWin = checkWin(chessPoint_a, chessPoint_b, color);
			if (isWin == false) {

				this.add(chesspoint_black);
				chesspoint_black.setBounds(chessPoint_a * 20 - 7,
						chessPoint_b * 20 - 7, 16, 16);
				statusText.setText("��(��" + chessBlackCount + "��)"
						+ chessPoint_a + " " + chessPoint_b + ",���������");
				isMouseEnabled = true;
			} else {
				this.add(chesspoint_black);
				chesspoint_black.setBounds(chessPoint_a * 20 - 7,
						chessPoint_b * 20 - 7, 16, 16);
				chessVictory(1);
				isMouseEnabled = true;
			}
		} else if (color == -1) {
			isWin = checkWin(chessPoint_a, chessPoint_b, color);
			if (isWin == false) {
				this.add(chesspoint_white);
				chesspoint_white.setBounds(chessPoint_a * 20 - 7,
						chessPoint_b * 20 - 7, 16, 16);
				statusText.setText("��(��" + chessWhiteCount + "��)"
						+ chessPoint_a + " " + chessPoint_b + ",���������");
				isMouseEnabled = true;
			} else {
				chessthread.sendMessage("/" + chessPeerName + " /victory "
						+ color);
				this.add(chesspoint_white);
				chesspoint_white.setBounds(chessPoint_a * 20 - 7,
						chessPoint_b * 20 - 7, 16, 16);
				chessVictory(-1);
				isMouseEnabled = true;
			}
		}
	}

	/**
	 * ����갴��ʱ��Ӧ�Ķ��������µ�ǰ�������λ�ã�����ǰ���ӵ�λ�á�
	 */
	public void mousePressed(MouseEvent e) {
		if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
			chessPoint_x = (int) e.getX();
			chessPoint_y = (int) e.getY();
			int a = (chessPoint_x + 10) / 20, b = (chessPoint_y + 10) / 20;
			if (chessPoint_x / 20 < 2 || chessPoint_y / 20 < 2
					|| chessPoint_x / 20 > 19 || chessPoint_y / 20 > 19) {
			} else {
				chessPaint(a, b, chessColor);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

}
/**
 * ��ʾ���ӵ���
 */

class chessPoint_black extends Canvas {
	ChessPanel chesspad = null;

	chessPoint_black(ChessPanel p) {
		setSize(20, 20);
		chesspad = p;
	}

	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillOval(0, 0, 14, 14);
	}

}
/**
 * ��ʾ���ӵ���
 */

class chessPoint_white extends Canvas {
	ChessPanel chesspad = null;

	chessPoint_white(ChessPanel p) {
		setSize(20, 20);

		chesspad = p;
	}

	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillOval(0, 0, 14, 14);
	}

}

