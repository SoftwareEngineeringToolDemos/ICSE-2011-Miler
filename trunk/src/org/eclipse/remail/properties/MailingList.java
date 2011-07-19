package org.eclipse.remail.properties;

import java.util.LinkedHashSet;

/**
 * Class defining a mailing list.
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class MailingList implements Comparable{
	private String location;
	private String username;
	private String password;
	
	public static String DELIMITER=" - ";
	
	public MailingList(String location, String username, String password){
		this.location=location;
		this.username=username;
		this.password=password;
	}

	/**
	 * Creates an empty mailing list
	 */
	public MailingList() {
		
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String toString(){
		return location;
	}

	@Override
	public int compareTo(Object o) {
		if(!(o instanceof MailingList))
			return -1;
		else
			return compareTo((MailingList)o);
	}
	
	public int compareTo(MailingList o) {
		return this.location.trim().compareTo(o.location.trim());
	}
	
	@Override
	public boolean equals (Object o){
		if(!(o instanceof MailingList))
			return false;
		else
			return equals((MailingList)o);
	}
	
	public boolean equals(MailingList ml){
		return this.location.trim().equals(ml.location.trim());
	}
	
	/**
	 * Convert the list of mailing list form a LinkedHashSet to a string
	 * @param list the list to convert
	 * @return a string representation that can be managed by Eclipse
	 */
	public static String listToString(LinkedHashSet<MailingList> list){
		String s="";
		for(MailingList ml : list){
			s+=ml.location+DELIMITER+ml.username+DELIMITER+ml.password+"\n";
		}		
		return s;
	}
	
	/**
	 * Convert the string representation of the list of mailing list
	 * to a LinkedHashSet
	 * @param s the string to convert
	 * @return the list as LinkedHashSet<MailingList>
	 */
	public static LinkedHashSet<MailingList> stringToList (String s){
		LinkedHashSet<MailingList> list= new LinkedHashSet<MailingList>();
		
		String[] lines = s.split("\n");
		for(String str: lines){
			String[] sp = str.split(DELIMITER);
			if(sp.length==3){
				MailingList m = new MailingList(sp[0], sp[1], sp[2]);
				list.add(m);
			}else
				System.err.println("mail list format error: "+str);
		}
		
		return list;
	}
}
