package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ResultFrame extends JFrame{  
	public JFrame jf ;
    public int i;  
    public JPanel panel;  
    public ResultFrame(int i){  
        this.i = i;  
    }  
      
    /* 
     * ����һ�����ɽ���ķ��� 
     */  
    public void initUI(){  
    	jf = new JFrame("���");
    	jf.setSize(new Dimension(400,200));
    	jf.setLocationRelativeTo(null);  
    	jf.setResizable(false);  
    	jf.setLayout(new BorderLayout());  
    	jf.setDefaultCloseOperation(HIDE_ON_CLOSE);  
          
        panel = new JPanel();  
        panel.setLayout(new FlowLayout());  
        jf.add(panel,BorderLayout.CENTER);  
        if(i == 1){  
            JLabel lab = new JLabel("��������������ʤ��");  
            panel.add(lab,BorderLayout.CENTER);  
              
        }  
        else if(i == -1){  
                JLabel lab = new JLabel("��������������ʤ��");  
                panel.add(lab);  
        }  
          
        JPanel pal = new JPanel();  
        JButton btn_restart = new JButton("���¿�ʼ");  
        JButton btn_exit = new JButton("�˳���Ϸ");  
        btn_restart.setActionCommand("restart");  
        btn_exit.setActionCommand("exit");  
          
        btn_restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jf.dispose();
				new ChessBoardPanel();
			}
		});  
        btn_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jf.dispose();
			}
		});  

        pal.setLayout(new FlowLayout());  
        jf.add(pal,BorderLayout.SOUTH);  
        pal.add(btn_restart);  
        pal.add(btn_exit);  
          
          
        jf.setVisible(true);  
  
    }    
    
    
}  
