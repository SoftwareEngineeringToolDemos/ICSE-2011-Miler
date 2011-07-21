package org.eclipse.remail.couchdb.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.remail.preferences.PreferenceConstants;

import com.fourspaces.couchdb.Session;

/**
 * Class used to get a list of databases present in couchdb
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class CouchDBDatabases {

	private final Session dbSession;
	
	public CouchDBDatabases (){
		String host = PreferenceConstants.P_COUCHDB_HOST;
		int port = Integer.parseInt(PreferenceConstants.P_COUCHDB_PORT);
		dbSession = new Session(host, port);
	}
	
	/**
	 * Get all the databases in couchdb which contains mailinglist
	 * @return a list of the databases names 
	 */
	public List<String> getListOfDatabases(){
		List<String> list = new ArrayList<String>();
		List<String> allDB= dbSession.getDatabaseNames();
		
		for(String db : allDB){
			if(db.startsWith(CouchDBCreator.PREFIX))
				list.add(db);
		}
		
		return list;
	}
	
	/**
	 * Same as getListOfDatabases() but as an array
	 * @return an array of the databases names
	 */
	public String[] getArrayOfDatabases(){
		List<String> list = getListOfDatabases();
		String[] arr = new String[list.size()];
		
		int i=0;
		for(String s : list){
			arr[i]=s;
			i++;
		}
		
		return arr;
	}
}
