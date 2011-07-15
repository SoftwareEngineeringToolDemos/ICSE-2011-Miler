package org.eclipse.remail.properties;

/**
 * Class defining a mailing list.
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class MailingList {
	private String location;
	private String username;
	private String password;
	
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

}
