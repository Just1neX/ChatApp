package Messenger;

public class Protokoll {

	public Protokoll() {

	}

	/**
	 * Gibt aus einer Nachicht vom Typ
	 * <N><sender:Justin><receiver:Tom><message:Hallo>
	 * 
	 * @param line - Nachicht
	 * @return String[] mit Sender,Empfänger,Nachicht
	 */
	public static String[] getDataFromMessage(String line) {
		String beginnSender = line.substring(11, line.length());
		String sender = beginnSender.substring(0, beginnSender.indexOf(">"));

		String ohneSender = beginnSender.substring(beginnSender.indexOf(">") + 1, beginnSender.length());
		String beginnEmpfaenger = ohneSender.substring(ohneSender.indexOf(":") + 1, ohneSender.length());
		String empfaenger = beginnEmpfaenger.substring(0, beginnEmpfaenger.indexOf(">"));

		String nurNachicht = beginnEmpfaenger.substring(beginnEmpfaenger.indexOf(">") + 1, beginnEmpfaenger.length());
		String nachicht = nurNachicht.substring(nurNachicht.indexOf(":") + 1, nurNachicht.length() - 1);

		String[] result = new String[3];
		result[0] = sender;
		result[1] = empfaenger;
		result[2] = nachicht;

		return result;
	}

	/**
	 * Gibt den Usernamen von einer Nachicht vom Typ <L><login:Just1neX>
	 * 
	 * @param line - Nachicht
	 * @return Usernmae des Client
	 */
	public static String getUsernameFromLoginMessage(String line) {
		int loginPosition = line.indexOf("<login:");
		return line.substring(loginPosition + 7, line.length() - 1);
	}

	public static String getUsernameFromAllOnlineMessage(String line) {

		if (line.length() != 3) {
			String users = line.substring(3, line.length());
			String[] names = users.split(";");
			String resultString = "Diese Nutzer sind online: ";

			for (int i = 0; i < names.length; i++) {

				if (i == names.length - 1) {
					resultString = resultString + names[i];
				} else {
					resultString = resultString + names[i] + ", ";
				}
			}

			return resultString;
		} else {
			return "Es sind keine anderen Nutzer online";
		}

	}
}
