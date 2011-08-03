package org.eclipse.remail.couchdb.util;

import org.eclipse.remail.Activator;
import org.eclipse.remail.preferences.PreferenceConstants;

import com.fourspaces.couchdb.Database;

/**
 * A common interface for all the classes creating a View for couchDB
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public interface CouchDBView {
	
	public static String server="http://"+Activator.getHost()+":"+Activator.getPort()+"/";//"http://localhost:5984/";

	/**
	 * Set the database where the view will be created
	 * 
	 * @param db
	 *            the database object belonging to
	 *            com.fourspaces.couchdb.Database
	 */
	public void setDatabase(Database db);

	/**
	 * Retrieve the id of the view
	 * 
	 * @return the id!
	 */
	public String getID();

	/**
	 * Add the view to the database
	 */
	public void addView();

	/**
	 * Retrieve the javascript map function
	 * 
	 * @return the string representation of the function
	 */
	public String getMapFunction();
	
	/**
	 * Retrieve the location where the map function is saved
	 * @return the string representing the URI
	 */
	public String getMapURI();
	
}
