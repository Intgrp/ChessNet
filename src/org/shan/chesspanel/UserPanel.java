
package org.shan.chesspanel;

import java.awt.BorderLayout;
import java.awt.List;

import javax.swing.JPanel;

/**
 * 
 * �û��б�Panel����Panelά��һ���������ĵ�ǰ�û��б�
 * ���е��û���������ʾ���б��С�
 */
public class UserPanel extends JPanel {
	public List userList = new List(5);  //�ɹ������ı����б�

	public UserPanel() {
		this.setLayout(new BorderLayout());

		for (int i = 0; i < 30; i++) {
			userList.add(i + "." + "��ǰ�����û�");
		}
		
		this.add(userList, BorderLayout.CENTER); //��ӵ������м�����
	}
}
