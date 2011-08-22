package org.eclipse.remail.couchdb.helper;

/**
 * This is the associative array of raters in a document stored 
 * inside couchdb
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class Raters {
	String[] raterName;
	int[] rating;
	
	public Raters (String[] raterName, int[] rating){
		this.raterName=raterName;
		this.rating=rating;
	}
	
	public String toString()
	{
		String s="";
		for (int i=0; i<raterName.length; i++){
			s+=raterName[i]+":"+rating[i]+",";
		}
		return s;
	}
	
	public String[] getRaters(){
		return raterName;
	}
	
	public int[] getRate(){
		return rating;
	}
}
