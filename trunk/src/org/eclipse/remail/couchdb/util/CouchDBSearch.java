package org.eclipse.remail.couchdb.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.remail.Mail;
import org.eclipse.remail.modules.MailSearch;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Session;

public class CouchDBSearch implements MailSearch{
	
	private String dbname= "small-db";
	private Session dbSession = new Session("localhost", 5984);
	private LinkedList<Mail> mailList = null;
	
	public CouchDBSearch ()
	{
		mailList= new LinkedList<Mail>();
	}

	@Override
	public LinkedList<Mail> caseSensitiveSearch(String name) {

		Database db = dbSession.getDatabase(dbname);
		// ViewResults result = db.getAllDocuments();
		//
//		Document doc = new Document();
//		doc.setId("123");
//		doc.put("foo", "bar");
//		db.saveDocument(doc);

		//add the view to the database
		CaseSensitiveView csv = new CaseSensitiveView(name, dbname);
		csv.setDatabase(db);
		csv.addView();
		System.out.println(csv.getMapURI());
		System.out.println(csv.getMapFunction());
		
		HttpClient httpclient = new DefaultHttpClient();

//		HttpGet get = new HttpGet(
//				"http://localhost:5984/small-db/_design/couchview/_view/javalanguage");
		HttpGet get = new HttpGet(csv.getMapURI());

		try {
			HttpResponse response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			java.io.InputStream instream = entity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					instream));
			String strdata = null;

			while ((strdata = reader.readLine()) != null) {
				System.out.println(strdata);
			}
		} catch (Exception e) {

		}

		// TODO Auto-generated method stub
		return mailList;
	}

	@Override
	public LinkedList<Mail> caseInsensitiveSearch(String name) {
		// TODO Auto-generated method stub
		return null;
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
