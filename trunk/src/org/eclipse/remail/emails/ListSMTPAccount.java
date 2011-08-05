package org.eclipse.remail.emails;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class representing a list of SMTPAccount
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class ListSMTPAccount implements Iterable<SMTPAccount>{

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
	 * Construct an empty list
	 */
	public ListSMTPAccount() {
		list = new ArrayList<SMTPAccount>();
	}
	
	public void append(SMTPAccount account) {
		list.add(account);
	}

	/**
	 * Convert the list of SMTPAccount to a string in the form:
	 * "mailAddress1 : username1 : password1 : server1 : port1 ; mailAddress2 : username2 : password2 : server2 : port2"
	 */
	public String toString() {
		String s="";
		if(list.size()>0){
			s = list.get(0).toString();
			for (int i = 1; i < list.size(); i++)
				s += DELIMITER + list.get(i);
		}
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
	
	/**
	 * Return the list of string which the user is supposed to see
	 * @return a string array with all the account's names
	 */
	public String[] toDisplay(){
		String[] disp=new String[list.size()];
		for(int i=0; i<list.size(); i++){
			try{
				disp[i]=list.get(i).toDisplay();
			}catch(NullPointerException e){
				disp[i]="";
			}
		}
		return disp;
	}
	
	public int length(){
		return list.size();
	}
	
	/**
	 * Get the element at the given position
	 * @param index the position
	 * @return the element
	 */
	public SMTPAccount get(int index){
		return list.get(index);
	}

	@Override
	public Iterator<SMTPAccount> iterator() {
		return list.iterator();
	}
	
	/**
	 * Update the given element in the list
	 * @param mailAddress the mailAddress to update
	 */
	public void update(String mailAddress){
		
	}

	public void delete(int i) {
		list.remove(i);
	}
}
