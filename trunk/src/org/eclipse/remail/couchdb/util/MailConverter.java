package org.eclipse.remail.couchdb.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.remail.Mail;
import org.eclipse.remail.couchdb.helper.CouchDBMail;
import org.eclipse.remail.couchdb.helper.CouchDBMailContainer;
import org.eclipse.remail.couchdb.helper.CouchDBResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Utility class used to convert a e-mail form the "couchdb format" to the Mail
 * format used in REmail
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class MailConverter {

	public static final String SUBJECT_PREFIX = "Subject:";
	public static final String AUTHOR_PREFIX = "From:";
	public static final String PERMALINK_PREFIX = "X-Permalink:";
	public static final String THREADLINK_PREFIX = "X-Thread:";
	public static final String DATE_PREFIX = "Date:";

	/**
	 * Convert all the mail contained in the couchDB reply to a query-view into
	 * a list of Mail to be used by REmail
	 * 
	 * @param cdbResponse
	 *            the reply from couchDB
	 * @param nameSearched
	 *            the name of the class searched in the view
	 * @return the ArrayList representation of all the e-mail
	 */
	public static LinkedList<Mail> convertCouchDBResponseToArrayListMail(
			final CouchDBResponse cdbResponse, final String nameSearched) {
		LinkedList<Mail> mailList = new LinkedList<Mail>();

		ArrayList<CouchDBMailContainer> cdbMCArray = cdbResponse.getRows();
		for (CouchDBMailContainer cdbMC : cdbMCArray) {
			Mail mail = convertCouchDBMailToMail(cdbMC.getValue(), nameSearched);
			mailList.add(mail);
		}

		return mailList;
	}

	/**
	 * Convert the e-mail from the format given in output by the couchdb view,
	 * to the Mail format used by REmail to store them
	 * 
	 * @param cdbMail
	 *            the e-mail in couchdb format
	 * @param nameSearched
	 *            the name of the class searched in the view
	 * @return the Mail that can be used by REmail
	 */
	public static Mail convertCouchDBMailToMail(final CouchDBMail cdbMail, final String nameSearched) {

		ArrayList<String> headerList = extractHeader(cdbMail.getHeader());
		String subject = null;
		String author = null;
		String permalink = null;
		String threadlink = null;
		Date timeStamp = null;

		for (String s : headerList) {
			s = s.trim();
			s = s.replace("\n", "");
			// I hate Java for non having a decent Switch-Case!
			if (s.startsWith(SUBJECT_PREFIX)) {
				subject = s;
			} else if (s.startsWith(AUTHOR_PREFIX)) {
				author = s;
			} else if (s.startsWith(PERMALINK_PREFIX)) {
				permalink = s;
			} else if (s.startsWith(THREADLINK_PREFIX)) {
				threadlink = s;
			} else if (s.startsWith(DATE_PREFIX)) {
				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
				try {
					timeStamp = df.parse(s.substring(6));
				} catch (ParseException e) {
					timeStamp = null;
				}
			}
		}
		Mail mail = new Mail(0, subject, timeStamp, author, permalink, threadlink,
				"", nameSearched);

		return mail;
	}

	/**
	 * Convert the json array format of the mail's headers into a list of String
	 * 
	 * @param header
	 *            the header in json
	 * @return the equivalent ArrayList representation
	 */
	private static ArrayList<String> extractHeader(String header) {
		ArrayList<String> headerList = new ArrayList<String>();
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(header).getAsJsonArray();
		for (JsonElement el : array) {
			String message = gson.fromJson(el, String.class);
			headerList.add(message);
		}
		return headerList;
	}

}
