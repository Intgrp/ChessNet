package org.shan.chesspanel;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * ��������Panel��
 */

public class RenamePanel extends JPanel {
	public JTextField rename = new JTextField("������������", 20);

	public JButton nameChangeButton = new JButton("��������");

	public RenamePanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBackground(new Color(204, 204, 204));
		this.add(rename);
		this.add(nameChangeButton);
	}

}