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
	 * 设置color黑白棋标识 true标识黑子，false标识白子，代表我方执棋颜色
	 */
	
	public boolean color = false;
	public Graphics g;
	public int x,y;
//	public String[][] array = new String[700][700];
	public int[][] array = new int[700][700];//1黑，2白，0空
	public int[][] array_win = new int[15][15];  //1黑 -1白改成1黑2白
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
	 * 鼠标释放执行的方法
	 */
	@Override
	public void mouseReleased(MouseEvent e) {  
        x = correctXY(e.getX());  
        y = correctXY(e.getY());  
        System.out.println("x:"+x+"   y:"+y);  
        System.out.println("isMouseEnabled="+isMouseEnabled);
        /* 
         * 判定为人人对战 
         */  
        if (EachRoomChessFrame.GameModel == 1) {  
            if ((isMouseEnabled) && x < 582 && x >= 0 && y < 582 && y >= 0) {  
                if (array_win[getXY(y)][getXY(x)] == 0) {  
                	if (color) {//如果是黑棋
                		System.out.println("鼠标点击事件，绘制黑棋");
                		g.setColor(Color.BLACK);  
                		g.fillOval(x, y, Config.Chess_size,  
                                Config.Chess_size);  
                        array[x][y] = 1;  
                        array_win[getXY(y)][getXY(x)] = 1;  
                        isMouseEnabled=false;
                	}
                	else {
                		System.out.println("鼠标点击事件，绘制白棋");
                		g.setColor(Color.WHITE);  
                        g.fillOval(x, y, Config.Chess_size,  
                                Config.Chess_size);  
                        array[x][y] = 2;  
                        array_win[getXY(y)][getXY(x)] = 2;  
                        isMouseEnabled=false;
                	}
                	eroomf.eachRoomThread.sendMessage("/inchess "+eroomf.eachRoomThread.mui.roomId +" "+ arrayToString());
                    //代表我下好了，服务器找寻与我对弈的人，通知，他可以下棋了
                    eroomf.eachRoomThread.sendMessage("/play "+eroomf.eachRoomThread.mui.roomId+" "+eroomf.eachRoomThread.mui.name );
                }
                //通知所有观看用户，更新棋盘
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
         * 人机对战 
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
     * 下棋位置坐标修正的方法 
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
     * 判赢方法 
     */  
  
    /* 
     * 判定横向五个相连 
     */  
    public boolean winRow(int row, int column) {  
        int count = 1;  
        for (int i = column + 1; i < 15; i++) {// 向右查找  
            if (array_win[row][column] == array_win[row][i]) {  
                count++;  
            } else  
                break;  
        }  
        for (int i = column - 1; i >= 0; i--) {// 向左查找  
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
     * 判定竖向五个相连 
     */  
    public boolean winColumn(int row, int column) {  
        int count = 1;  
        for (int i = row + 1; i < 15; i++) {// 向右查找  
            if (array_win[row][column] == array_win[i][column]) {  
                count++;  
            } else  
                break;  
        }  
        for (int i = row - 1; i >= 0; i--) {// 向左查找  
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
     * 判定斜向右下五个相连 
     */  
    public boolean winRightDown(int row, int column) {  
        int count = 1;  
        for (int i = column + 1, j = row + 1; i < 15 && j < 15; i++, j++) {// 向右查找  
            if (array_win[row][column] == array_win[j][i]) {  
                count++;  
            } else  
                break;  
        }  
        for (int i = column - 1, j = row - 1; i >= 0 && j >= 0; i--, j--) {// 向左查找  
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
     * 判定斜向左下五个相连 
     */  
    public boolean winLeftDown(int row, int column) {  
        int count = 1;  
        for (int i = column - 1, j = row + 1; i >=0 && j < 15; i--, j++) {// 向右查找  
            if (array_win[row][column] == array_win[j][i]) {  
                count++;  
            } else  
                break;  
        }  
        for (int i = column + 1, j = row - 1; i <15 && j >= 0; i++, j--) {// 向左查找  
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
                return 1;  //黑赢
            else if (array_win[row][column] == 2)  
                return 2;  //白赢
        }  
        return 0;  
    }    
    
    /**
     * 将棋盘array_win 15*15数组转换成字符串，换行处用逗号代替0代表空，1代表黑，2代表白
     * @return
     */
    public String arrayToString() {
    	//将01数组array转变成字符串，换行出处用“，”逗号代替
    	String result="";
    	for (int i=0;i<this.array_win.length;i++) {
    		for (int j=0;j<this.array_win.length;j++) {
    			result = result+String.valueOf(array_win[i][j]);
    		}
    		result = result+",";
    	}
    	result = result.substring(0, result.length()-1);//去掉最后的那个逗号
    	System.out.println("array_win数组转换成字符串的结果："+result);
    	return result;
    }
    
    /**
     * 将传过来的最新棋盘15*15组成的字符串转换为array_win数组，同时更新棋盘像素数组array，大小为棋盘大小
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
    	System.out.println("====接收的数据的大小样式====");
    	for (int i=0;i<tmp.length;i++) {
    		for (int j=0;j<tmp[i].length();j++) {
    			System.out.print(array_win[i][j]+" ");
    		}
    		System.out.println(); 
    	}
    	*/
    }
    
    /**
     * 更新棋盘最新状态信息
     */
    public void refresh(Graphics g) {
    	for (int i=0;i<array_win.length;i++) {
    		for (int j=0;j<array_win.length;j++) {
    			if (array_win[i][j]!=0) {
    				if (array_win[i][j]==1) {
    					g.setColor(Color.BLACK);  
    					System.out.println("绘制位置坐标：行"+j*40+"列"+i*40);
    	                g.fillOval(j*40, i*40, Config.Chess_size,  
    	                        Config.Chess_size);  
    				}
    				else if (array_win[i][j]==2) {
    					g.setColor(Color.WHITE);  
    					System.out.println("绘制位置坐标：行"+j*40+"列"+i*40);
    	                g.fillOval(j*40, i*40, Config.Chess_size,  
    	                        Config.Chess_size);  
    				}
    			}
    		}
    	}
    }

}
