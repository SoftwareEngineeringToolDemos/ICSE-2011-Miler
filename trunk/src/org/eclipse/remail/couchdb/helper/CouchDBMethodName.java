package org.eclipse.remail.couchdb.helper;

/**
 * Provides the view's name for the different search methods
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public enum CouchDBMethodName {
	CASE_SENSITIVE ("casesensitive"),
	CASE_INSENSITIVE ("caseinsensitive"),
	SEARCH_STRICT ("searchstrict"),
	SEARCH_LOSE ("searchlose"),
	SEARCH_DICT ("searchdict"),
	SEARCH_CAMEL ("searchcamel");
	
	private final String name;
	
	CouchDBMethodName (String name){
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
}
