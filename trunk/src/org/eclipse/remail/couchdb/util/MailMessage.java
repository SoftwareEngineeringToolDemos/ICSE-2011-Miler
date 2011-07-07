package org.eclipse.remail.couchdb.util;

import com.fourspaces.couchdb.Document;

/**
 * Class extending the Document class of couchdb. Used to get a nice ad simple
 * access to the database. Requires the library couchdb4j
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class MailMessage extends Document {

	private String start;
	private String header;
	private String body;

	private static final String StartFieldName = "start";
	private static final String HeaderFieldName = "header";
	private static final String BodyFieldName = "body";

	public MailMessage() {
		super();
	}

	public MailMessage(String start, String header, String body) {
		super();
		this.start = start;
		this.header = header;
		this.body = body;
	}

	/**
	 * Fills the table with the pair ("field-name", "field")
	 */
	public void putAllFields() {
		this.put(StartFieldName, start);
		this.put(HeaderFieldName, header);
		this.put(BodyFieldName, body);
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
