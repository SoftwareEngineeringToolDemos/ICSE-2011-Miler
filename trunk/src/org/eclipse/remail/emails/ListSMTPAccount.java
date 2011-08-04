package org.eclipse.remail.emails;

import java.util.ArrayList;

/**
 * Class representing a list of SMTPAccount
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class ListSMTPAccount {

	private static final String DELIMITER = " ; ";

	ArrayList<SMTPAccount> list;

	/**
	 * A lovely constructor
	 * 
	 * @param accounts
	 *            a list of SMTPAccounts
	 */
	public ListSMTPAccount(SMTPAccount... accounts) {
		list = new ArrayList<SMTPAccount>();
		for (SMTPAccount acc : accounts)
			list.add(acc);
	}

	/**
	 * Convert the list of SMTPAccount to a string in the form:
	 * "mailAddress1 : username1 : password1 : server1 : port1 ; mailAddress2 : username2 : password2 : server2 : port2"
	 */
	public String toString() {
		String s = list.get(0).toString();
		for (int i = 1; i < list.size(); i++)
			s += DELIMITER + list.get(i);
		return s;
	}

	/**
	 * Convert a string in the form
	 * "mailAddress1 : username1 : password1 : server1 : port1 ; mailAddress2 : username2 : password2 : server2 : port2"
	 * to a list of SMTPAccounts
	 * 
	 * @param s
	 *            the string to convert
	 * @return the list of SMTPAccounts
	 */
	public static ListSMTPAccount fromString(String s) {
		String[] spl = s.split(DELIMITER);
		SMTPAccount[] accs = new SMTPAccount[spl.length];
		for (int i = 0; i < spl.length; i++)
			accs[i] = SMTPAccount.fromString(spl[i]);
		return new ListSMTPAccount(accs);
	}

	public boolean equals(ListSMTPAccount l) {
		if (this.list.size() != l.list.size())
			return false;

		for (int i = 0; i < list.size(); i++)
			if (!list.get(i).equals(l.list.get(i)))
				return false;

		return true;
	}

	public boolean equals(Object o) {
		if (o instanceof ListSMTPAccount)
			return this.equals((ListSMTPAccount) o);
		else
			return false;
	}
}
