package Messenger;

import java.io.IOException;

public class LaufenderServer {

	public static void main(String[] args) {
		Server server = new Server(2022);
		try {
			server.acceptNewClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
