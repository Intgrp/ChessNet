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
 * 输入信息Panel。Panel左边的下拉列表中列出了所有用户的名字。
 * 右边的文本框可以输入聊天信息，点击回车信息被发送。
 * 此外还可以 在文本框中输入命令如changename、list等。
 */

public class InputPanel extends JPanel {
	JPanel pan1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JPanel pan2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	public JTextField inputWords = new JTextField("点击回车键发送信息", 20);

	public Choice userChoice = new Choice();
	public JTextField rename = new JTextField("点击回车键更改名称", 20);

	public JLabel nameLabel = new JLabel("输入更改姓名:");
	public JLabel chatLabel = new JLabel("输入发送信息:");
	public InputPanel() {
		pan1.setBackground(new Color(204, 204, 204));
		pan2.setBackground(new Color(204, 204, 204));
		setLayout(new BorderLayout());
		for (int i = 0; i < 30; i++) {
			userChoice.addItem(i + "." + "当前暂无用户");
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