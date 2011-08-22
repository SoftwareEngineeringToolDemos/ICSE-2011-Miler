package org.eclipse.remail.couchdb.helper;


/**
 * Class used to convert the JSON string response received from couchdb
 * into a Java Object which can be used by REmail. 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class CouchDBMail {
	private String _id;
	private String _rev;
	private String body;
	private String key;
	private String header;
	private Raters raters;
	
	public CouchDBMail (String id, String _rev, String body, String header, String start){
		this._id=id;
		this._rev=_rev;
		this.body=body;
		this.header=header;
		this.key=start;
	}
	
	public CouchDBMail (String id, String _rev, String body, String header, String start, Raters raters){
		this._id=id;
		this._rev=_rev;
		this.body=body;
		this.header=header;
		this.key=start;
		this.raters=raters;
	}
	
	@Override
	public String toString()
	{
		return String.format("(_id=%s, _rev=%s, body=%s, start=%s, header=%s)", _id, _rev, body, key, header);
	}
	
	public String getId() {
		return _id;
	}
	public void setId(String id) {
		this._id = id;
	}
	public String get_rev() {
		return _rev;
	}
	public void set_rev(String _rev) {
		this._rev = _rev;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String start) {
		this.key = start;
	}

	public Raters getRaters() {
		return raters;
	}

}
