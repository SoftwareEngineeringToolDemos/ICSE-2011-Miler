package org.eclipse.remail.couchdb.util;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.remail.Activator;
import org.eclipse.remail.Mail;
import org.eclipse.remail.couchdb.helper.CouchDBResponse;
import org.eclipse.remail.couchdb.helper.HttpGetView;
import org.eclipse.remail.modules.MailSearch;
import org.eclipse.remail.preferences.PreferenceConstants;
import org.eclipse.remail.properties.MailingList;
import org.eclipse.remail.properties.RemailProperties;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Session;

/**
 * Class implementing the search methods for couchDB
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class CouchDBSearch implements MailSearch {

	private Session dbSession;// = new Session("localhost", 5984);
	private LinkedList<Mail> mailList = null;
	private HashMap<String, LinkedHashSet<MailingList>> mapProjectsMailingList;

	public CouchDBSearch() {
		mailList = new LinkedList<Mail>();
		mapProjectsMailingList=new HashMap<String, LinkedHashSet<MailingList>>();
		dbSession = new Session(Activator.getHost(),
				Integer.parseInt(Activator.getPort()));
	
		//get all the projects in the workspace
		IProject[] projects=ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject prj :projects){
			LinkedHashSet<MailingList> arrayMailingList = new LinkedHashSet<MailingList>();
			try {
				String prjLoc=prj.getLocation().toString();
				String property=prj.getPersistentProperty(RemailProperties.REMAIL_MAILING_LIST);
				if(property!=null){
					arrayMailingList.addAll(MailingList.stringToList(property));
					mapProjectsMailingList.put(prjLoc, arrayMailingList);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public LinkedList<Mail> caseSensitiveSearch(String path, String name) {

		//check which project the class belongs
		LinkedHashSet<MailingList> arrayMailingList=checkClassBelongsProject(path);

		for (MailingList ml : arrayMailingList) {
			String dbname = ml.getLocation().replace(".", "_");
			dbname=dbname.replace("@", "-");
			if(!dbname.startsWith(CouchDBCreator.PREFIX))
				dbname=CouchDBCreator.PREFIX+dbname;
//			System.out.println("using: "+Activator.getHost()+" "+Activator.getPort()+" "+dbname);
			Database db = dbSession.getDatabase(dbname);
			// add the view to the database
			CaseSensitiveView csv = new CaseSensitiveView(name, dbname);
			csv.setDatabase(db);
			csv.addView();

			// get the view
			HttpGetView hgv = new HttpGetView(csv.getMapURI());
//			System.out.println("Request: \n"+csv.getMapURI());
			String response = hgv.sendRequest();
			//System.out.println("Response: \n"+response);

			// parse the view result to get a java object out of the json
			CouchDBResponse cdbr = CouchDBResponse.parseJson(response);
			// System.out.println(cdbr.toString());

			// convert the result to Mails
			mailList = MailConverter.convertCouchDBResponseToArrayListMail(cdbr, name);
			// System.out.println(mailList);

		}
		return mailList;
	}

	@Override
	public LinkedList<Mail> caseInsensitiveSearch(String path, String name) {
		
		//check which project the class belongs
		LinkedHashSet<MailingList> arrayMailingList=checkClassBelongsProject(path);
		for (MailingList ml : arrayMailingList) {
			String dbname = ml.getLocation().replace(".", "_");
			dbname=dbname.replace("@", "-");
			if(!dbname.startsWith(CouchDBCreator.PREFIX))
				dbname=CouchDBCreator.PREFIX+dbname;
			Database db = dbSession.getDatabase(dbname);

			// add the view to the database
			CaseInsensitiveView civ = new CaseInsensitiveView(name, dbname);
			civ.setDatabase(db);
			civ.addView();

			// get the view
			HttpGetView hgv = new HttpGetView(civ.getMapURI());
			String response = hgv.sendRequest();
			// parse the view result to get a java object out of the json
			CouchDBResponse cdbr = CouchDBResponse.parseJson(response);

			// convert the result to Mails
			mailList = MailConverter.convertCouchDBResponseToArrayListMail(cdbr, name);
			// System.out.println(mailList);
		}
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

	/**
	 * Check if the given class belongs to some project
	 * @param path the class path
	 * @return the list of mailing list of that project
	 */
	public LinkedHashSet<MailingList> checkClassBelongsProject(String path) {
		LinkedHashSet<MailingList> list = new LinkedHashSet<MailingList>();
		
		for(String prj : mapProjectsMailingList.keySet()){
			if(path.startsWith("/")){
				//its a full path
//				System.out.println("looking for:"+ path);
				if(path.contains(prj)){
					list.addAll(mapProjectsMailingList.get(prj));
				}
			}else{
				//its a relative path
				String search=prj+"/"+path;
//				System.out.println("looking for:"+ search);
				File file = new File(search);
				if(file.exists()){
					list.addAll(mapProjectsMailingList.get(prj));
				}
			}			
		}
		return list;
	}
}
