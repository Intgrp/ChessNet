package beans;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class User {
	public Socket clientSocket;
	public String host = "127.0.0.1";
	public int port = 4331;
	
	public User() {
		try {
			clientSocket = new Socket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public User(String host, int port) {
		this.host=host;
		this.port = port;
		try {
			clientSocket = new Socket(host, port);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Socket¡¨Ω” ß∞‹");
		}
	}
}
