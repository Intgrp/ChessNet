package org.shan.chesspanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *  
 * 聊天信息Panel。Panel上的文本域可以显示用户聊天信息。
 */
public class ChatPanel extends JPanel {
	public JTextArea chatLineArea = new JTextArea("", 18, 30);

	public ChatPanel() {
		setLayout(new BorderLayout());

		add(chatLineArea, BorderLayout.CENTER);
	}

}