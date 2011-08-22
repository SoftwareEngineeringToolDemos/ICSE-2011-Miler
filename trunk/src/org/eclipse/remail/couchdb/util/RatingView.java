package org.eclipse.remail.couchdb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;

/**
 * Function used to call the _desing/rating view on couchDB Which is used to
 * update the rating
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class RatingView implements CouchDBView {

	private String databaseName;
	private Database database;
	private String id;
	private String mapFunction;
	private String mapURI;

	private String idToSearch;
	private int rating;
	private String rater;

	public RatingView(String idToSearch, int rating, String rater, String databaseName) {
		this.idToSearch = idToSearch;
		this.databaseName = databaseName;
		this.rating=rating;
		this.rater=rater;
		this.id = "_design/rating";
		createMapUri();

		this.mapFunction = "function(doc,req) { var rating = parseInt(req.query.rating); var rater = req.query.rater; if (!(doc.raters)) { doc.raters = {}; } doc.raters[rater] = rating; var count = 0; var currentRating = 0; for (var each_rater in doc.raters) { count = count + 1; currentRating = currentRating + doc.raters[each_rater]; } var avgRating = currentRating / count; return [doc, avgRating.toString()]; }";
	}

	private void createMapUri() {
		mapURI = server + databaseName + "/"+id + "/_update/rating/"+idToSearch + "?"+"rating="+rating+"&rater="+rater;
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
	
	public void update() {
		DefaultHttpClient httpClient=new DefaultHttpClient();
		
		HttpPut put = new HttpPut(this.mapURI);
		
		HttpResponse response;
		String responseValue="";
		try {
			response = httpClient.execute(put);
			HttpEntity entity = response.getEntity();
			java.io.InputStream instream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					instream));
			
			String strdata = null;
			while ((strdata = reader.readLine()) != null) {
				responseValue+=strdata;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(this.mapURI);
		System.out.println(responseValue);
	}

}
