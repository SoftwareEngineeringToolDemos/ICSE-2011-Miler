package org.eclipse.remail.couchdb.helper;

/**
 * Class used to convert the JSON string response received from couchdb
 * into a Java Object which can be used by REmail. 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class CouchDBMailContainer {
	private String id;
	private String key;
	private CouchDBMail value;
	
	public CouchDBMailContainer (String id, String key, CouchDBMail value)
	{
		this.id=id;
		this.key=key;
		this.value=value;
	}
	
	@Override
	public String toString()
	{
		return String.format("(id=%s, key=%s, value=%s)", id, key, value.toString());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public CouchDBMail getValue() {
		return value;
	}
	public void setValue(CouchDBMail value) {
		this.value = value;
	}

}
