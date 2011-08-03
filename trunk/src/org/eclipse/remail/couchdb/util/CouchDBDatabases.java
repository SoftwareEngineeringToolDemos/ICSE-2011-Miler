package org.eclipse.remail.couchdb.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.remail.Activator;
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
		String host = Activator.getHost();
		int port = Integer.parseInt(Activator.getPort());
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
	
	/**
	 * Convert the names of the database into the name of the mailing list
	 * @param arr the array to converts
	 * @return the array converted
	 */
	public static String[] fromRealNameToNiceName(String[] arr){
		String[] newArr=new String[arr.length];
		for(int i=0; i<arr.length; i++){
			String s=arr[i];
			s=s.replace("_", ".");
			s=s.replace("-", "@");
			s=s.replace(CouchDBCreator.PREFIX, "");
			newArr[i]=s;
		}
		return newArr;
	}
}
