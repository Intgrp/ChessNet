
package org.shan.chesspanel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Label;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * ����Panel����Panel�ϵİ�ť�������������Ӧ�Ĺ��ܡ�
 */
public class ControlPanel extends JPanel {
	public JLabel IPlabel = new JLabel("������IP:", Label.LEFT);

	public JTextField inputIP = new JTextField("localhost", 10);

	public JButton connectButton = new JButton("��������");

	public JButton creatGameButton = new JButton("�½���Ϸ");

	public JButton joinGameButton = new JButton("������Ϸ");

	public JButton cancelGameButton = new JButton("�˳���Ϸ");

	public JButton exitGameButton = new JButton("�رճ���");
    //���캯��������Panel�ĳ�ʼ���֡�
	public ControlPanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBackground(new Color(204,204,204));

		add(IPlabel);
		add(inputIP);
		add(connectButton);
		add(creatGameButton);
		add(joinGameButton);
		add(cancelGameButton);
		add(exitGameButton);
	}

}