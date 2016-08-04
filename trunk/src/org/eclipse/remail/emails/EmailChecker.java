package org.eclipse.remail.emails;

/**
 * This class contains static methods to check the validity of emails parameters
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class EmailChecker {

	/**
	 * Check that the parameters inserted are correct
	 * 
	 * @param from
	 *            the string identifying the sender
	 * @param to
	 *            the string identifying the receiver
	 * @return true if they are correct, false otherwise
	 */
	public static boolean checkFromToParameters(String from, String to) {
		boolean correct = false;
		if (!from.equals("") && !to.equals(""))
			correct = true;
		return correct;
	}

	/**
	 * Check that one of the two parameters is non-empty (different form "")
	 * 
	 * @param cc
	 *            the cc field
	 * @param bcc
	 *            the bcc field
	 * @return true if at least one of the two is non empty, false otherwise
	 */
	public static boolean checkCcAndBcc(String cc, String bcc) {
		boolean correct = false;
		if (!cc.equals("") || !bcc.equals(""))
			correct = true;
		return correct;
	}

}
