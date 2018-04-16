package org.shan.chesspanel;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *  
 * ������ϢPanel��Panel��ߵ������б����г��������û������֡�
 * �ұߵ��ı����������������Ϣ������س���Ϣ�����͡�
 * ���⻹���� ���ı���������������changename��list�ȡ�
 */

public class InputPanel extends JPanel {
	JPanel pan1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JPanel pan2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	public JTextField inputWords = new JTextField("����س���������Ϣ", 20);

	public Choice userChoice = new Choice();
	public JTextField rename = new JTextField("����س�����������", 20);

	public JLabel nameLabel = new JLabel("�����������:");
	public JLabel chatLabel = new JLabel("���뷢����Ϣ:");
	public InputPanel() {
		pan1.setBackground(new Color(204, 204, 204));
		pan2.setBackground(new Color(204, 204, 204));
		setLayout(new BorderLayout());
		for (int i = 0; i < 30; i++) {
			userChoice.addItem(i + "." + "��ǰ�����û�");
		}
		userChoice.setSize(60, 24);
		pan1.add(chatLabel);
		pan1.add(inputWords);
		pan1.add(userChoice);		
		pan2.add(nameLabel);
		pan2.add(rename);
		this.add(pan1, BorderLayout.NORTH);
		this.add(pan2, BorderLayout.SOUTH);
	}

}