package ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class RoomPanel extends JPanel implements ActionListener{
	public EachRoomPanel[] eachRoomPanels = new EachRoomPanel[8*5];
	
	public RoomPanel() {
		setBackground(new Color(5, 67, 108));
		setLayout(new GridLayout(8, 5, 10,10));
		for (int i = 0; i < 5*8; i++) {
			eachRoomPanels[i] = new EachRoomPanel(String.valueOf(i + 1));
			add(eachRoomPanels[i]);
		}
		//一个room是180*70
		setSize(180*5+60, 70*8);
		setVisible(true);
	}
	
	public RoomPanel(MainUIFrame mui) {
		setBackground(new Color(5, 67, 108));
		setLayout(new GridLayout(8, 5, 10,10));
		for (int i = 0; i < 5*8; i++) {
			eachRoomPanels[i] = new EachRoomPanel(String.valueOf(i + 1), mui);
			add(eachRoomPanels[i]);
		}
		//一个room是180*70
		setSize(180*5+60, 70*8);
		setVisible(true);
	}

	
	public static void main(String[] args) {
		JFrame jf = new JFrame("tttt");
		jf.setLayout(null);
		jf.add(new RoomPanel());
		jf.setVisible(true);
		jf.setSize(1000, 600);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("===============");
		for (int i=0; i<eachRoomPanels.length; i++) {
			if (e.getSource()==eachRoomPanels[i]) {
				System.out.println(eachRoomPanels[i].btn_table.getText());
			}
		}
	}
}
