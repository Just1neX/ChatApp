package Messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientHandler extends Thread {

	private Socket clientSocket;
	private Server server;
	private PrintWriter writer;
	private String username;

	public ServerClientHandler(Socket clientSocket, Server server) {
		this.clientSocket = clientSocket;
		this.server = server;

		try {
			OutputStream output = clientSocket.getOutputStream();
			writer = new PrintWriter(output, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			InputStream input = clientSocket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			while (true) {
				if (reader.ready()) {
					aufschluesseln(reader.readLine());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		writer.println(message);
	}

	/**
	 * Überprüft um welche Art von Message es sich handelt
	 * 
	 * @param line - Message (Syntax)
	 */
	private void aufschluesseln(String line) {

		// Login
		if (line.substring(0, 3).equals("<L>")) {

			String username = Protokoll.getUsernameFromLoginMessage(line);
			boolean newUser = server.isUsernameNew(username);

			if (newUser == false) {
				System.out.println("Server Anmelden des Clientes abgelent!");
				sendMessage("<A>");
			} else {
				server.saveSocketFromClient(username, this);
				this.username = username;
				System.out.println("Server Login: " + username);
				sendMessage("<E>");
			}

		}

		// Nachicht Senden
		if (line.substring(0, 3).equals("<N>")) {
			String[] erg = Protokoll.getDataFromMessage(line);

			boolean send = server.sendMessageFromClientToClient(erg[0], erg[1], erg[2]);

			System.out.println(
					"Nachicht von " + erg[0] + " zu " + erg[1] + " mit Nachicht: \"" + erg[2] + "\" gesenden: " + send);

			// User ist nicht online
			if (send == false) {
				sendMessage("<Z>");
				server.addMessageToOfflineUsers(erg[0], erg[1], erg[2]);
			}
		}

		// Logout
		if (line.substring(0, 3).equals("<O>")) {
			server.removeSocketFromServer(username);
			System.out.println("Server Logout: " + username);
			sendMessage("<O>");
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// All Online User
		if (line.substring(0, 3).equals("<B>")) {
			String message = server.getAllOnlineUsersMessage(username);
			sendMessage(message);
		}

		// Get Offline Messages for User
		if (line.substring(0, 3).equals("<G>")) {
			server.sendAllOfflineMessagesForUser(username);
		}
	}

}
