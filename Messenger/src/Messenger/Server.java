package Messenger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Server {

	/**
	 * Aktuell verbundene Clients
	 */
	private HashMap<String, ServerClientHandler> connections = new HashMap<String, ServerClientHandler>();

	/**
	 * Speichert ein Array mit Usernamen des Senders, Usernamen des Empfängers sowie
	 * die Message zwischen
	 */
	private ArrayList<String[]> offlineMessagesALT = new ArrayList<String[]>();

	private HashMap<String, Queue<String[]>> offlineMessages = new HashMap<String, Queue<String[]>>();

	private int port;

	public Server(int port) {
		this.port = port;
	}

	/**
	 * Verbindungsaufbau mit Clients
	 * 
	 * @throws IOException
	 */
	public void acceptNewClient() throws IOException {

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Server: Neuer Nutzer erfolgreich am Server verbunden!");
				System.out.println("");

				new ServerClientHandler(clientSocket, this).start();
			}
		}

	}

	/**
	 * Gibt die Messaage mit allen Usern die Online sind zurück
	 * 
	 * @param username - Benutzername der Anfragt
	 * @return Message mit allen Online
	 */
	public String getAllOnlineUsersMessage(String username) {

		String result = "<B>";

		for (String s : connections.keySet()) {
			if (s != username) {
				result = result + s + ";";
			}
		}

		if (result.length() > 3) {
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}

	/**
	 * Schickt eine Message vom Server zu einem verbundeten CLient
	 * 
	 * @param sender  - Username des Clients welcher die Nachicht schicken möchte
	 * @param reciver - Username des Clients welcher die Nachicht empfangen soll
	 * @param message - Nachicht welche versendet werden soll
	 * @return true - Message erfolgreich versendet | false - Message nicht
	 *         erfolgreich versendet
	 */
	public boolean sendMessageFromClientToClient(String sender, String reciver, String message) {
		String sendString = "<N><sender:" + sender + "><receiver:" + reciver + "><message:" + message + ">";

		ServerClientHandler client = getSocketFromClient(reciver);

		if (client == null) {
			// Keinen passenden Halder zum gewollten Client gefunden
			return false;
		} else {
			System.out.println("Nachicht senden!");
			client.sendMessage(sendString);
			return true;
		}
	}

	/**
	 * Prüft ob ein Username schon vergeben ist
	 * 
	 * @param username- Benutzername
	 * @return true - neu | false - schon vergeben
	 */
	public boolean isUsernameNew(String username) {

		ServerClientHandler connection = connections.get(username);

		if (connection == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Entfernt den Handler des übergebenen Username aus der Liste der Verbundennen
	 * Clients
	 * 
	 * @param username - Benutzername des Clients
	 */
	public void removeSocketFromServer(String username) {
		connections.remove(username);
	}

	/**
	 * Speichert einen Handler des Client anhand des Usernamen
	 * 
	 * @param username - Benutzername des Clients
	 * @param client   - Handler des Clients
	 */
	public void saveSocketFromClient(String username, ServerClientHandler client) {
		connections.put(username, client);
	}

	/**
	 * Gibt den Handler zum passenden Usernamen zurück
	 * 
	 * @param username - Benutzername des Clients
	 * @return Handler des Clients
	 */
	public ServerClientHandler getSocketFromClient(String username) {
		return connections.get(username);
	}

	/**
	 * Schickt alle Offline Messages an den entsprechenden Client in Richtiger
	 * Reihenfolge
	 * 
	 * @param username - Nutzername des Client
	 */
	public void sendAllOfflineMessagesForUser(String username) {
		ServerClientHandler client = connections.get(username);

		if (offlineMessages.get(username) != null) {
			Queue<String[]> messages = offlineMessages.get(username);

			String send = "<M>";

			while (!messages.isEmpty()) {
				String[] m = messages.remove();
				String sendUsername = m[0];
				String empfaengerUsername = m[1];
				String message = m[2];

				send = send + sendUsername + ": " + message + ";";
			}

			send = send.substring(0, send.length() - 1);
			System.out.println(send);
			// Über <N> Nachichten kommen durcheinander
			client.sendMessage(send);
			offlineMessages.remove(username);

		} else {
			System.out.println("Keine Offline Messages für: " + username);
			// Keine Offline Messages
			client.sendMessage("<K>");
		}
	}

	/**
	 * Fügt eine Nachicht zu den Offline Nachichten hinzu
	 * 
	 * @param sendUsername       - Absender der Nachicht
	 * @param empfaengerUsername - Empfänger der Nachicht
	 * @param message            - Inhalt der Nachicht
	 */
	public void addMessageToOfflineUsers(String sendUsername, String empfaengerUsername, String message) {
		String[] save = new String[3];
		save[0] = sendUsername;
		save[1] = empfaengerUsername;
		save[2] = message;

		if (offlineMessages.get(empfaengerUsername) == null) {
			// Hinzufügen eines Neuen Users, welcher Offline ist
			Queue<String[]> messages = new LinkedList<String[]>();

			messages.add(save);
			offlineMessages.put(empfaengerUsername, messages);
		} else {
			Queue<String[]> messages = offlineMessages.get(empfaengerUsername);
			messages.add(save);
		}
	}
}
