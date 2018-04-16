package org.shan.chesspanel;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * 更改姓名Panel。
 */

public class RenamePanel extends JPanel {
	public JTextField rename = new JTextField("请输入新名称", 20);

	public JButton nameChangeButton = new JButton("更改姓名");

	public RenamePanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBackground(new Color(204, 204, 204));
		this.add(rename);
		this.add(nameChangeButton);
	}

}