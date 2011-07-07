package org.eclipse.remail.couchdb.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This class connects to the couchDB database, retrieve the result of the view, 
 * parse it and return just a string.
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class HttpGetView {
	
	private HttpClient httpclient;
	private final String URI;
	
	/**
	 * Construct a object HttpGetView
	 * @param URI is the address where the request will be sent
	 */
	public HttpGetView (String URI)
	{
		httpclient = new DefaultHttpClient();
		this.URI=URI;
	}
	
	/**
	 * Send the request to the server at the address identified by URI 
	 * @return the response 
	 */
	public String sendRequest (){
		String responseValue="";
		
		HttpGet get = new HttpGet(URI);
		
		HttpResponse response;
		try {
			response = httpclient.execute(get);
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
		
		return responseValue;
	}

}
