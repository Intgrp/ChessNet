package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.security.auth.Refreshable;
import javax.swing.JPanel;

import ui.ResultFrame;
import ui.EachRoomChessFrame;
import ui.ResultFrame;
import util.Config;

public class WZQ_listener extends MouseAdapter{
	/**
	 * ����color�ڰ����ʶ true��ʶ���ӣ�false��ʶ���ӣ������ҷ�ִ����ɫ
	 */
	
	public boolean color = false;
	public Graphics g;
	public int x,y;
//	public String[][] array = new String[700][700];
	public int[][] array = new int[700][700];//1�ڣ�2�ף�0��
	public int[][] array_win = new int[15][15];  //1�� -1�׸ĳ�1��2��
    public int[][] array_pve = new int[15][15];  
    public int count_max;  
    public int count_where = 0;  
    public JPanel panel;  
    public ResultFrame result;  
    public List list;  
    public int val;
    
    public boolean isMouseEnabled=false;
    public EachRoomChessFrame eroomf;
    
    
	public WZQ_listener(Graphics g , EachRoomChessFrame eachRoomFrame) {
		super();
		this.g = g;
		this.eroomf = eachRoomFrame;
	}

	/**
	 * ����ͷ�ִ�еķ���
	 */
	@Override
	public void mouseReleased(MouseEvent e) {  
        x = correctXY(e.getX());  
        y = correctXY(e.getY());  
        System.out.println("x:"+x+"   y:"+y);  
        System.out.println("isMouseEnabled="+isMouseEnabled);
        /* 
         * �ж�Ϊ���˶�ս 
         */  
        if (EachRoomChessFrame.GameModel == 1) {  
            if ((isMouseEnabled) && x < 582 && x >= 0 && y < 582 && y >= 0) {  
                if (array_win[getXY(y)][getXY(x)] == 0) {  
                	if (color) {//����Ǻ���
                		System.out.println("������¼������ƺ���");
                		g.setColor(Color.BLACK);  
                		g.fillOval(x, y, Config.Chess_size,  
                                Config.Chess_size);  
                        array[x][y] = 1;  
                        array_win[getXY(y)][getXY(x)] = 1;  
                        isMouseEnabled=false;
                	}
                	else {
                		System.out.println("������¼������ư���");
                		g.setColor(Color.WHITE);  
                        g.fillOval(x, y, Config.Chess_size,  
                                Config.Chess_size);  
                        array[x][y] = 2;  
                        array_win[getXY(y)][getXY(x)] = 2;  
                        isMouseEnabled=false;
                	}
                	eroomf.eachRoomThread.sendMessage("/inchess "+eroomf.eachRoomThread.mui.roomId +" "+ arrayToString());
                    //�������º��ˣ���������Ѱ���Ҷ��ĵ��ˣ�֪ͨ��������������
                    eroomf.eachRoomThread.sendMessage("/play "+eroomf.eachRoomThread.mui.roomId+" "+eroomf.eachRoomThread.mui.name );
                }
                //֪ͨ���йۿ��û�����������
                if (Win(getXY(y), getXY(x)) == 1) {  
                    result = new ResultFrame(1,eroomf);  
                    result.initUI();  
                } else if (Win(getXY(y), getXY(x)) == 2) {  
                    result = new ResultFrame(2, eroomf);  
                    result.initUI();  
                }  
            }  
  
        }  
  
        /* 
         * �˻���ս 
         */  
        /*
        else if (EachRoomChessFrame.GameModel == 2) {  
            if (isMouseEnabled && x < 582 && x > 10 && y < 582 && y > 10 && array[x][y] == 0) {  
                g.setColor(Color.BLACK);  
                g.fillOval(x - Config.Chess_size / 2, y  
                        - Config.Chess_size / 2, Config.Chess_size,  
                        Config.Chess_size);  
  
                array[x][y] = 1;  
                
//                eroomf.eachRoomThread.sendMessage("/play "+eroomf.eachRoomThread.mui.roomId+" "+eroomf.eachRoomThread.mui.name );
//                eroomf.eachRoomThread.sendMessage("/inchess "+eroomf.eachRoomThread.mui.roomId +" "+ arrayToString());
  
                array_win[getXY(y)][getXY(x)] = 1;  
                array_pve[getXY(y)][getXY(x)] = 0;  
                for (int i = 0; i < 15; i++) {  
                    for (int j = 0; j < 15; j++) {  
                        System.out.print(array_pve[i][j] + "  ");  
                    }  
                    System.out.println("");  
                }  
                System.out.println("");  
  
            }  
  
        }  
        */
  
    }  
  
    /* 
     * ����λ�����������ķ��� 
     */  
    public int correctXY(int x) {  
        x = x / 40;  
  
        return x * 40;  
    }  
  
    public int getXY(int x) {  
        x = x / 40;  
        return x;  
    }  
  
    /* 
     * ��Ӯ���� 
     */  
  
    /* 
     * �ж������������ 
     */  
    public boolean winRow(int row, int column) {  
        int count = 1;  
        for (int i = column + 1; i < 15; i++) {// ���Ҳ���  
            if (array_win[row][column] == array_win[row][i]) {  
                count++;  
            } else  
                break;  
        }  
        for (int i = column - 1; i >= 0; i--) {// �������  
            if (array_win[row][column] == array_win[row][i]) {  
                count++;  
            } else  
                break;  
        }  
  
        if (count >= 5) {  
            return true;  
        } else  
            return false;  
    }  
  
    /* 
     * �ж������������ 
     */  
    public boolean winColumn(int row, int column) {  
        int count = 1;  
        for (int i = row + 1; i < 15; i++) {// ���Ҳ���  
            if (array_win[row][column] == array_win[i][column]) {  
                count++;  
            } else  
                break;  
        }  
        for (int i = row - 1; i >= 0; i--) {// �������  
            if (array_win[row][column] == array_win[i][column]) {  
                count++;  
            } else  
                break;  
        }  
        if (count >= 5) {  
            return true;  
        } else  
            return false;  
    }  
  
    /* 
     * �ж�б������������� 
     */  
    public boolean winRightDown(int row, int column) {  
        int count = 1;  
        for (int i = column + 1, j = row + 1; i < 15 && j < 15; i++, j++) {// ���Ҳ���  
            if (array_win[row][column] == array_win[j][i]) {  
                count++;  
            } else  
                break;  
        }  
        for (int i = column - 1, j = row - 1; i >= 0 && j >= 0; i--, j--) {// �������  
            if (array_win[row][column] == array_win[j][i]) {  
                count++;  
            } else  
                break;  
        }  
        if (count >= 5) {  
            return true;  
        } else  
            return false;  
    }  
  
    /* 
     * �ж�б������������� 
     */  
    public boolean winLeftDown(int row, int column) {  
        int count = 1;  
        for (int i = column - 1, j = row + 1; i >=0 && j < 15; i--, j++) {// ���Ҳ���  
            if (array_win[row][column] == array_win[j][i]) {  
                count++;  
            } else  
                break;  
        }  
        for (int i = column + 1, j = row - 1; i <15 && j >= 0; i++, j--) {// �������  
            if (array_win[row][column] == array_win[j][i]) {  
                count++;  
            } else  
                break;  
        }  
        if (count >= 5) {  
            return true;  
        } else  
            return false;  
    }  
  
    public int Win(int row, int column) {  
        if (winRow(row, column) || winColumn(row, column)  
                || winRightDown(row, column) || winLeftDown(row, column)) {  
            if (array_win[row][column] == 1)  
                return 1;  //��Ӯ
            else if (array_win[row][column] == 2)  
                return 2;  //��Ӯ
        }  
        return 0;  
    }    
    
    /**
     * ������array_win 15*15����ת�����ַ��������д��ö��Ŵ���0����գ�1����ڣ�2�����
     * @return
     */
    public String arrayToString() {
    	//��01����arrayת����ַ��������г����á��������Ŵ���
    	String result="";
    	for (int i=0;i<this.array_win.length;i++) {
    		for (int j=0;j<this.array_win.length;j++) {
    			result = result+String.valueOf(array_win[i][j]);
    		}
    		result = result+",";
    	}
    	result = result.substring(0, result.length()-1);//ȥ�������Ǹ�����
    	System.out.println("array_win����ת�����ַ����Ľ����"+result);
    	return result;
    }
    
    /**
     * ������������������15*15��ɵ��ַ���ת��Ϊarray_win���飬ͬʱ����������������array����СΪ���̴�С
     * @param input
     */
    public void StringToArray(String input){
    	String[] tmp = input.split(",");
    	for (int i=0;i<tmp.length;i++) {
    		for (int j=0;j<tmp[i].length();j++) {
    			array_win[i][j]=tmp[i].charAt(j)-'0';
    			array[i*40][j*40]=array_win[i][j];
    		}
    	}
    	/*
    	System.out.println("====���յ����ݵĴ�С��ʽ====");
    	for (int i=0;i<tmp.length;i++) {
    		for (int j=0;j<tmp[i].length();j++) {
    			System.out.print(array_win[i][j]+" ");
    		}
    		System.out.println(); 
    	}
    	*/
    }
    
    /**
     * ������������״̬��Ϣ
     */
    public void refresh(Graphics g) {
    	for (int i=0;i<array_win.length;i++) {
    		for (int j=0;j<array_win.length;j++) {
    			if (array_win[i][j]!=0) {
    				if (array_win[i][j]==1) {
    					g.setColor(Color.BLACK);  
    					System.out.println("����λ�����꣺��"+j*40+"��"+i*40);
    	                g.fillOval(j*40, i*40, Config.Chess_size,  
    	                        Config.Chess_size);  
    				}
    				else if (array_win[i][j]==2) {
    					g.setColor(Color.WHITE);  
    					System.out.println("����λ�����꣺��"+j*40+"��"+i*40);
    	                g.fillOval(j*40, i*40, Config.Chess_size,  
    	                        Config.Chess_size);  
    				}
    			}
    		}
    	}
    }

}
