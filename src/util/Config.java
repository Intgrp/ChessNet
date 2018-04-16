package util;

public class Config {
	/**
	 * 设置棋盘网络行数列数为10
	 */
	public static final int ROW = 15;
	public static final int COLUMN = 15;
	
	/**
	 * 设置棋盘初始位置
	 */
	public static final int X = 30;
	public static final int Y = 60;
	
	/**
	 * 设置棋子大小
	 */
	public static final int Chess_size = 40;
	
	/**
	 * 设置棋盘格子大小
	 */
	public static final int Board_distance = 40;
	
	public static int Chess_high=ROW  * Board_distance+50;
	public static int Chess_width=COLUMN  * Board_distance+50;

}
