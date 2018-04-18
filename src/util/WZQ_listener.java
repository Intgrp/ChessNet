package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPanel;

import ui.ResultFrame;
import ui.EachRoomFrame;
import ui.ResultFrame;
import util.Config;

public class WZQ_listener extends MouseAdapter{
	/**
	 * 设置黑白棋标识 true标识黑子，false标识白子
	 */
	
	public boolean state = true;
	public Graphics g;
	public int x,y;
	public String[][] array = new String[700][700];
	public int[][] array_win = new int[15][15];  
    public int[][] array_pve = new int[15][15];  
    public int count_max;  
    public int count_where = 0;  
    public JPanel panel;  
    public ResultFrame result;  
    public List list;  
    public int val;
	public WZQ_listener(Graphics g) {
		super();
		this.g = g;
	}
	
	/**
	 * 鼠标释放执行的方法
	 */
	@Override
	public void mouseReleased(MouseEvent e) {  
        x = correctXY(e.getX());  
        y = correctXY(e.getY());  
        System.out.println("x:"+x+"   y:"+y);  
        /* 
         * 判定为人人对战 
         */  
        if (EachRoomFrame.GameModel == 1) {  
            if (x < 582 && x >= 0 && y < 582 && y >= 0) {  
                if (state && array[x][y] == null) {  
                    g.setColor(Color.BLACK);  
                    g.fillOval(x, y, Config.Chess_size,  
                            Config.Chess_size);  
  
                    array[x][y] = "black";  
  
                    array_win[getXY(y)][getXY(x)] = 1;  
                    state = false;  
  
                } else if (array[x][y] == null) {  
                    g.setColor(Color.WHITE);  
                    g.fillOval(x, y, Config.Chess_size,  
                            Config.Chess_size);  
  
                    array[x][y] = "white";  
  
                    array_win[getXY(y)][getXY(x)] = -1;  
  
                    state = true;  
                }  
  
                if (Win(getXY(y), getXY(x)) == 1) {  
                    result = new ResultFrame(1);  
                    result.initUI();  
                } else if (Win(getXY(y), getXY(x)) == -1) {  
                    result = new ResultFrame(-1);  
                    result.initUI();  
                }  
                for (int i = 0; i < 15; i++) {  
                    for (int j = 0; j < 15; j++) {  
                        System.out.print(array_win[i][j] + "  ");  
                    }  
                    System.out.println("");  
                }  
                System.out.println("");  
            }  
  
        }  
  
        /* 
         * 人机对战 
         */  
        else if (EachRoomFrame.GameModel == 2) {  
            if (x < 582 && x > 10 && y < 582 && y > 10 && array[x][y] == null) {  
                g.setColor(Color.BLACK);  
                g.fillOval(x - Config.Chess_size / 2, y  
                        - Config.Chess_size / 2, Config.Chess_size,  
                        Config.Chess_size);  
  
                array[x][y] = "black";  
  
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
                return 1;  
            else if (array_win[row][column] == -1)  
                return -1;  
        }  
        return 0;  
    }    
    

}
