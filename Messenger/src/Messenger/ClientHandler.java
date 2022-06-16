package Messenger;

import java.net.Socket;

public class ClientHandler extends Thread {

	private Socket serversocket;
	private Client client;
	private String line;

	public ClientHandler(Socket socket, Client client, String line) {
		this.serversocket = socket;
		this.client = client;
		this.line = line;
	}

	@Override
	public void run() {
		aufschluesseln(line);
	}

	/**
	 * Überprüft um welche Art von Message es sich handelt
	 * 
	 * @param line - Message (Syntax)
	 */
	private void aufschluesseln(String line) {

		// Empfangene Nachicht verarbeiten
		if (line.substring(0, 3).equals("<N>")) {
			String[] daten = Protokoll.getDataFromMessage(line);
			System.out.println("");
			System.out.println(daten[0] + ": " + daten[2]);
		}

		// Bestätigung Login
		if (line.substring(0, 3).equals("<E>")) {
			System.out.println("Einloggen am Server war erfolgreich!\n");
			client.initialisiereChat();
		}

		// Ablehnung Login weil Nutzer schon vorhanden am Server
		if (line.substring(0, 3).equals("<A>")) {
			System.out.println("Einloggen am Server war nicht erfolgreich! \nNutzername ist schon vergeben!\n");
			client.eingabeUsername();
		}

		// Logout
		if (line.substring(0, 3).equals("<O>")) {
			// logout();
			System.out.println("Client vom Server ausgeloggt!");
		}

		// Alle Online Anzeigen
		if (line.substring(0, 3).equals("<B>")) {
			System.out.println(Protokoll.getUsernameFromAllOnlineMessage(line));

		}

		// Angefragter Nutzer ist nicht online
		if ((line.substring(0, 3)).equals("<Z>")) {
			System.out.println("Nutzer ist nicht online!\nNachicht wurde am Server gespeichert!");
		}

		// Keine Offline Messages
		if ((line.substring(0, 3)).equals("<K>")) {
			System.out.println("Sie haben keine Nachichten bekommen solange Sie offline waren.");
		}

		// Anzeigen aller Messages die man Offlien bekommen hat
		if ((line.substring(0, 3)).equals("<M>")) {
			String[] messages = Protokoll.getOfflineMessages(line);

			for (int i = 0; i < messages.length; i++) {
				System.out.println(messages[i]);
			}

			System.out.println("");
			System.out.println("Aktueller Chat:");
		}

	}
}
