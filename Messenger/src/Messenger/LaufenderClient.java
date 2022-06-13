package Messenger;

import java.io.IOException;
import java.net.UnknownHostException;

public class LaufenderClient {

	public static void main(String[] args) {
		Client client = new Client("localhost", 2022);
		try {
			client.connectToServer();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
