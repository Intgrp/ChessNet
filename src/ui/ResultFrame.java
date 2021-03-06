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
    public EachRoomChessFrame eChessFrame;
    public ResultFrame(int i, EachRoomChessFrame eChessFrame){  
        this.i = i;  
        this.eChessFrame = eChessFrame;
    }  

    /* 
     * 定义一个生成界面的方法 
     */  
    public void initUI(){  
    	jf = new JFrame("结果");
    	jf.setSize(new Dimension(400,200));
    	jf.setLocationRelativeTo(null);  
    	jf.setResizable(false);  
    	jf.setLayout(new BorderLayout());  
    	jf.setDefaultCloseOperation(HIDE_ON_CLOSE);  
          
        panel = new JPanel();  
        panel.setLayout(new FlowLayout());  
        jf.add(panel,BorderLayout.CENTER);  
        if(i == 1){  
            JLabel lab = new JLabel("黑子五连，黑子胜！");  
            panel.add(lab,BorderLayout.CENTER);  
              
        }  
        else if(i == 2){  
                JLabel lab = new JLabel("白子五连，白子胜！");  
                panel.add(lab);  
        }  
          
        JPanel pal = new JPanel();  
        JButton btn_restart = new JButton("重新开始");  
        JButton btn_exit = new JButton("退出游戏");  
        btn_restart.setActionCommand("restart");  
        btn_exit.setActionCommand("exit");  
          
        btn_restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new EachRoomChessFrame(eChessFrame.eachRoomThread.mui);
				jf.setVisible(false);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.dispose();
			}
		});  
        btn_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eChessFrame.eachRoomThread.mui.mainUIThread.sendMessage("/leaveroom "+eChessFrame.eachRoomThread.mui.roomId+" "+eChessFrame.eachRoomThread.mui.name);
				eChessFrame.eachRoomThread.mui.roomId="";
				for (int i=0;i<eChessFrame.lis.array_win.length;i++) {
					for (int j=0;j<eChessFrame.lis.array_win.length;j++) {
						eChessFrame.lis.array_win[i][j]=0;
					}
				}
				eChessFrame.refresh();
				eChessFrame.eachRoomThread.mui.repaint();//全部设置完图片后刷新界面
				eChessFrame.eachRoomThread.mui.mframe.setVisible(true);
				eChessFrame.setVisible(false);
				eChessFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.setVisible(false);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});  

        pal.setLayout(new FlowLayout());  
        jf.add(pal,BorderLayout.SOUTH);  
        pal.add(btn_restart);  
        pal.add(btn_exit);  
          
          
        jf.setVisible(true);  
  
    }    
    
    
}  
