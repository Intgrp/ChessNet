package org.shan.chesspanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *  
 * ������ϢPanel��Panel�ϵ��ı��������ʾ�û�������Ϣ��
 */
public class ChatPanel extends JPanel {
	public JTextArea chatLineArea = new JTextArea("", 18, 30);

	public ChatPanel() {
		setLayout(new BorderLayout());

		add(chatLineArea, BorderLayout.CENTER);
	}

}