package org.eclipse.remail.couchdb.util;

import java.util.Locale;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;

/**
 * A view used to implement the case non-sensitive search method for couchDB
 * (should never be used)
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class CaseInsensitiveView implements CouchDBView {

	private String databaseName;
	private Database database;
	private String id;
	private String mapFunction;
	private String mapURI;

	private String nameToSearch;

	public CaseInsensitiveView(String nameToSearch, String databaseName) {
		this.nameToSearch = nameToSearch;
		this.databaseName = databaseName;
		this.id = "_design/caseinsensitive"+"-"+nameToSearch;
		createMapUri();
		createMapFunction();
	}
	
	/**
	 * Its the complete URL where the function is located
	 */
	private void createMapUri() {
		mapURI = server + databaseName + "/"+id+"/_view/caseinsensitive";
	}

	/**
	 * It's the javascript function that couchdb will use to create the view
	 */
	private void createMapFunction() {
		String nameActuallySearched=nameToSearch.toLowerCase(Locale.ENGLISH);
		mapFunction = "{\"caseinsensitive\": {\"map\": \"function(doc) { if (doc.body.toLowerCase().indexOf('"
				+ nameActuallySearched + "')!=-1)  emit(null, doc) } \"}}";
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
		/*
		 * To test a view's existence: just try to access it in the database
		 * if it doesn't exist it generates an Exception, so you can create the
		 * view to store in database in catch block.
		 * If it exist there is nothing to do and method can happily die!
		 */
		try {
			//check if the view already exists
			Document d = database.getDocument(id);
		} catch (net.sf.json.JSONException e) {
			//create it, if not
			Document doc = new Document();
			doc.setId(id);
			doc.put("views", mapFunction); 
			database.saveDocument(doc);
		}		
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
