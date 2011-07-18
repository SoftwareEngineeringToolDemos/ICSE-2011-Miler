package org.eclipse.remail.couchdb.util;

import org.eclipse.remail.couchdb.helper.HttpGetView;
import org.eclipse.remail.preferences.PreferenceConstants;

import com.fourspaces.couchdb.Session;

/**
 * Class used to create a database in a couchDB server
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class CouchDBCreator {

	private final Session dbSession;
	private final String databaseName;
	private String host;
	private int port;

	public CouchDBCreator(String databaseName) {
		host = PreferenceConstants.P_COUCHDB_HOST;
		port = Integer.parseInt(PreferenceConstants.P_COUCHDB_PORT);
		dbSession = new Session(host, port);
		this.databaseName = databaseName;
	}

	/**
	 * Check if the database exist
	 * 
	 * @return true if it exists, false otherwise
	 */
	public boolean checkDatabaseExists() {
		// http://localhost:5984/small-db
		/*
		 * {"db_name":"small-db","doc_count":9,"doc_del_count":5,"update_seq":61,
		 * "purge_seq"
		 * :0,"compact_running":false,"disk_size":237657,"instance_start_time"
		 * :"1310740692378378"
		 * ,"disk_format_version":5,"committed_update_seq":61}
		 */
		//http://localhost:5984/non-exist
		/*
		 * {"error":"not_found","reason":"no_db_file"}
		 */
		
		String url = "http://"+host+":"+port+"/"+databaseName;
		System.out.println(url);
		HttpGetView hpv = new HttpGetView(url);
		String response=hpv.sendRequest();
		System.out.println(response);
		if(response.contains("not_found"))
			return false;
		else
			return true;
	}

	/**
	 * Create the database
	 * 
	 * @return true if the creation has been successful, false otherwise
	 */
	public boolean createDatabase() {
		if(!checkDatabaseExists()){
			dbSession.createDatabase(databaseName);
			return true;
		}
		return false;
	}
}
