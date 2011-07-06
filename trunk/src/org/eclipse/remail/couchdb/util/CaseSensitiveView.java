package org.eclipse.remail.couchdb.util;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;

/**
 * A view used to implement the case sensitive search method for couchDB
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class CaseSensitiveView implements CouchDBView {

	private String databaseName;
	private Database database;
	private String id;
	private String mapFunction;
	private String mapURI;

	private String nameToSearch;

	public CaseSensitiveView(String nameToSearch, String databaseName) {
		this.nameToSearch = nameToSearch;
		this.databaseName = databaseName;
		this.id = "_design/casesensitive";
		createMapUri();
		createMapFunction();
	}

	/**
	 * Its the complete URL where the function is located
	 */
	private void createMapUri() {
		mapURI = server + databaseName + "/"+id+"/_view/casesensitive";
	}

	
	private void createMapFunction() {
//		mapFunction = "{\"casesensitive\": {\"map\": \"function(doc) { if (doc.body.indexOf(\""
//				+ nameToSearch + "\"))  emit(null, doc) } \"}}";
		mapFunction= "{\"casesensitive\": {\"map\": \"function(doc) { if (doc._id == '123')  emit(null, doc) } \"}}";
	}

	@Override
	public void setDatabase(Database db) {
		this.database = db;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public void addView() {
		Document doc = new Document();
		doc.setId(id);
		doc.put("views", mapFunction); 
		database.saveDocument(doc);
	}

	@Override
	public String getMapFunction() {
		return mapFunction;
	}

	@Override
	public String getMapURI() {
		return mapURI;
	}

}
