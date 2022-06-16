package Messenger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private String host;
	private int port;
	private PrintWriter writer;
	private Socket serverSocket;
	private Scanner s;

	private String clientUsername;

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
		s = new Scanner(System.in);
	}

	public void connectToServer() throws UnknownHostException, IOException {

		serverSocket = new Socket(host, port);
		System.out.println("Erfolgreich zum Server verbunden!");
		System.out.println("");
		buildWriter();

		printComands();

		// Nachichten Empfangen starten
		new ClientReader(serverSocket, this).start();

		// Login
		eingabeUsername();
	}

	/**
	 * Printet alle Befehle die Benutzt werden können
	 */
	private void printComands() {
		System.out.println("Verfügbare Befehle: \n");
		System.out.println("/disconnect - Logout beim Server");
		System.out.println("/all - Ausgabe aller Benutzer die Online sind");
		System.out.println("<Message>-><Empfänger> - Nachicht an Benutzer senden");
		System.out.println("");
	}

	/**
	 * Starte den Chat | WRITER
	 */
	public void initialisiereChat() {
		// Abfrage nach offline Messages
		System.out.println("Offline Nachichten:\n");
		sendMessage("<G>");

		while (true) {
			String eingabe = s.nextLine();
			aufschluesseln(eingabe);
		}
	}

	/**
	 * Nimmt eingegebenen Befehl an und führt ihn
	 * 
	 * @param eingabe - Eingegebene Befehl
	 */
	private void aufschluesseln(String eingabe) {

		if (eingabe.substring(0, 1).equals("/")) {
			if (eingabe.equals("/disconnect")) {
				sendMessage("<O>");
				stopClient();
			}
			if (eingabe.equals("/all")) {
				sendMessage("<B>");
			}
		} else {
			// Nachicht senden
			int pos = eingabe.indexOf("->");

			if (pos == -1) {
				System.out.println("Falsche Syntax: <Message>-><Empfänger>");
			} else {
				String message = eingabe.substring(0, pos);
				String empfaenger = eingabe.substring(pos + 2, eingabe.length());

				String messageOut = buildMessage(empfaenger, message);
				sendMessage(messageOut);
			}
		}

	}

	/**
	 * Erstellt eine Nachicht Message
	 * 
	 * @param usernameEmpfaenger - Benutzername des Empfängers
	 * @param message            - Nachicht welche verschickt werden soll
	 * @return - Nachicht Message
	 */
	private String buildMessage(String usernameEmpfaenger, String message) {

		return "<N><sender:" + clientUsername + "><receiver:" + usernameEmpfaenger + "><message:" + message + ">";
	}

	/**
	 * Erstellt die Login Message
	 * 
	 * @param username - Benutzername des Clients
	 * @return - Login Message
	 */
	private String buildLoginMessage(String username) {
		return "<L><login:" + username + ">";
	}

	/**
	 * Setz den akzeptierten Username des Client
	 * 
	 * @param clientUsername - Benutzername des Client
	 */
	private void setUsername(String clientUsername) {
		this.clientUsername = clientUsername;
	}

	/**
	 * Fasade für buildLoginMessage und sendMessage und setUsername
	 * 
	 * @param username - gwünschter Benutzername
	 */
	private void loginWithUsernameToServer(String username) {
		String message = buildLoginMessage(username);
		sendMessage(message);
		setUsername(username);
	}

	/**
	 * Forder den User auf einen Benutzername an der Console einzugeben und
	 * überprüft den eingegebenen Username am Server ob dieser neu ist
	 */
	public void eingabeUsername() {

		System.out.println("Bitte geben Sie ihren Benutzernamen ein:");

		String vorlaeufigerUsername = s.nextLine();
		loginWithUsernameToServer(vorlaeufigerUsername);
	}

	/**
	 * Erstellt einen Printwriter um Messages zu senden
	 * 
	 * @throws IOException
	 */
	private void buildWriter() throws IOException {
		OutputStream output = serverSocket.getOutputStream();
		writer = new PrintWriter(output, true);
	}

	/**
	 * Sendet eine Message zum verbundenen Server
	 * 
	 * @param message - Message welche gesendet werden soll (Syntax)
	 */
	private void sendMessage(String message) {
		writer.println(message);
	}

	/**
	 * Stoppt die Schleife welche die Eingabe am Client macht
	 */
	private void stopClient() {
		System.exit(0);
	}
}
