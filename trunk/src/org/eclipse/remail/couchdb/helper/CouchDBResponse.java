package org.eclipse.remail.couchdb.helper;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class used to convert the JSON string response received from couchdb
 * into a Java Object which can be used by REmail. 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class CouchDBResponse {
	private int total_rows;
	private int offset;
	private LinkedList<CouchDBMailContainer> rows;
	
	public CouchDBResponse(){
		
	}
	
	public CouchDBResponse (int total_rows, int offset, LinkedList<CouchDBMailContainer> rows){
		this.total_rows=total_rows;
		this.offset=offset;
		this.rows=rows;
	}
	
	@Override
	public String toString()
	{
		return String.format("(total_rows=%s, offset=%s, rows=%s)", total_rows, offset, rows.toString());
	}
	
	/**
	 * Take a json string as parameter which represent this object
	 * and parse it to construct the object!
	 * @param json the object representation in json format
	 * @return the java object
	 */
	public static CouchDBResponse parseJson (String json){
//		Gson gson = new Gson();
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(Raters.class, new RaterClassDeserializer());
		Gson gson = gb.create();
		CouchDBResponse reply= gson.fromJson(json, CouchDBResponse.class);
		return reply;
	}
	
	public int getTotal_rows() {
		return total_rows;
	}
	public void setTotal_rows(int total_rows) {
		this.total_rows = total_rows;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public LinkedList<CouchDBMailContainer> getRows() {
		return rows;
	}
	public void setRows(LinkedList<CouchDBMailContainer> rows) {
		this.rows = rows;
	}
}
