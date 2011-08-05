package org.eclipse.remail.emails;

/**
 * This class defines a user's SMTP account. It is used to store the preference
 * of the page PreferencePaneEmailUser in a nice way
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class SMTPAccount {

	private static final String DELIMITER = " : ";

	private String mailAddress;
	private String username;
	private String password;
	private String server;
	private String port;

	public SMTPAccount(String mailAddress, String username, String password, String server,
			String port) {
		this.mailAddress = mailAddress.trim();
		this.username = username.trim();
		this.password = password.trim();
		this.server = server.trim();
		this.port = port.trim();
	}

	/**
	 * Update this SMTPAccount with the given values
	 * 
	 * @param username
	 * @param password
	 * @param server
	 * @param port
	 */
	public void update(String username, String password, String server, String port) {
		this.username = username.trim();
		this.password = password.trim();
		this.server = server.trim();
		this.port = port.trim();
	}

	/**
	 * Convert the SMTPAccount into a String in the form:
	 * "mailAddress : username : password : server : port"
	 * 
	 * @return the string
	 */
	public String toString() {
		String s = mailAddress;
		s += DELIMITER + username;
		s += DELIMITER + password;
		s += DELIMITER + server;
		s += DELIMITER + port;
		return s;
	}

	/**
	 * Convert a String in the form
	 * "mailAddress : username : password : server : port" to an SMTPAccount
	 * 
	 * @param s
	 *            the string to conver
	 * @return the SMTPAccount
	 */
	public static SMTPAccount fromString(String s) {
		String[] spl = s.split(DELIMITER);
		return new SMTPAccount(spl[0], spl[1], spl[2], spl[3], spl[4]);
	}

	public boolean equals(SMTPAccount acc) {
		return mailAddress.equals(acc.mailAddress) && username.equals(acc.username)
				&& password.equals(acc.password) && server.equals(acc.server)
				&& port.equals(acc.port);
	}

	public boolean equals(Object o) {
		if (o instanceof SMTPAccount)
			return this.equals((SMTPAccount) o);
		else
			return false;
	}

	/**
	 * Return the string which the user is supposed to see
	 * 
	 * @return a string with the account's name
	 */
	public String toDisplay() {
		return mailAddress;
	}
	
	/**
	 * Check if all the parameters are valid,
	 * i.e. non null and non empty
	 */
	public boolean checkValidity(){
		boolean valid=true;
		valid=valid && mailAddress!=null && !mailAddress.equals("");
		valid=valid && username!=null && !username.equals("");
		valid=valid && password!=null && !password.equals("");
		valid=valid && server!=null && !server.equals("");
		valid=valid && port!=null && !port.equals("");
		return valid;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getServer() {
		return server;
	}

	public String getPort() {
		return port;
	}

	public SMTPAccount copy() {
		SMTPAccount newAcc = new SMTPAccount(mailAddress, username, password, server, port);
		return newAcc;
	}
}
