package org.eclipse.remail.couchdb.util;

import java.util.LinkedList;
import org.eclipse.remail.Mail;
import org.eclipse.remail.couchdb.helper.CouchDBResponse;
import org.eclipse.remail.couchdb.helper.HttpGetView;
import org.eclipse.remail.modules.MailSearch;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Session;

public class CouchDBSearch implements MailSearch{
	
	//big test
	private String dbname= "big-test";
	//Uncomment for use the small test
//	private String dbname="small-db";
	private Session dbSession = new Session("localhost", 5984);
	private LinkedList<Mail> mailList = null;
	
	public CouchDBSearch ()
	{
		mailList= new LinkedList<Mail>();
	}

	@Override
	public LinkedList<Mail> caseSensitiveSearch(String name) {

		Database db = dbSession.getDatabase(dbname);

		//add the view to the database
		CaseSensitiveView csv = new CaseSensitiveView(name, dbname);
		csv.setDatabase(db);
		csv.addView();
//		System.out.println(csv.getMapURI());
//		System.out.println(csv.getMapFunction());
		
		//get the view 
		HttpGetView hgv = new HttpGetView(csv.getMapURI());
		String response= hgv.sendRequest();
//		System.out.println("Response: \n"+response);
		
		//parse the view result to get a java object out of the json
		CouchDBResponse cdbr = CouchDBResponse.parseJson(response);
//		System.out.println(cdbr.toString());

		//convert the result to Mails
		mailList=MailConverter.convertCouchDBResponseToArrayListMail(cdbr, name);
//		System.out.println(mailList);
		return mailList;
	}

	@Override
	public LinkedList<Mail> caseInsensitiveSearch(String name) {
		Database db = dbSession.getDatabase(dbname);

		//add the view to the database
		CaseInsensitiveView civ = new CaseInsensitiveView(name, dbname);
		civ.setDatabase(db);
		civ.addView();
		
		//get the view 
		HttpGetView hgv = new HttpGetView(civ.getMapURI());
		String response= hgv.sendRequest();
		//parse the view result to get a java object out of the json
		CouchDBResponse cdbr = CouchDBResponse.parseJson(response);
		
		//convert the result to Mails
		mailList=MailConverter.convertCouchDBResponseToArrayListMail(cdbr, name);
//		System.out.println(mailList);
		return mailList;
	}

	@Override
	public LinkedList<Mail> strictRegexpSearch(String path, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedList<Mail> looseRegexpSearch(String path, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedList<Mail> dictionarySearch(String path, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedList<Mail> camelCaseSearch(String path, String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
