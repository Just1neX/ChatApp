package Messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReader extends Thread {

	private Socket serversocket;
	private Client client;

	private boolean hoere = true;

	public ClientReader(Socket socket, Client client) {
		this.serversocket = socket;
		this.client = client;
	}

	@Override
	public void run() {
		try {
			InputStream input = serversocket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			while (hoere) {
				if (reader.ready()) {
					String line = reader.readLine();
					// Aufschlüsseln in anderem Thread
					new ClientHandler(serversocket, client, line).start();
				}
			}

			serversocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
