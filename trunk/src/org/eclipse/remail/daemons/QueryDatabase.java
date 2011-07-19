package org.eclipse.remail.daemons;

import java.util.LinkedList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.remail.Mail;
import org.eclipse.remail.Search;
import org.eclipse.remail.util.CacheCouchDB;

/**
 * Its a daemon thread used to query the database 
 * in background without bothering the user! 
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class QueryDatabase extends Job {
	
	String classname;
	String path;

	public QueryDatabase(String classname, String path){
		super("QueryFor"+classname);
		this.classname=classname;
		this.path=path;
	}
	
	protected IStatus run(IProgressMonitor monitor) {
		Search.updateMailView(new LinkedList<Mail>());
		if (!CacheCouchDB.containsClass(classname)) {
			/*if its not present in the cache
			 *sleep for a while, to avoid freezing the interface 
			 */
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Search search = new Search();
		LinkedList<Mail> mailList = search.Execute(classname, path, true);
		if (mailList == null)
			Search.updateMailView(new LinkedList<Mail>());
		else
			Search.updateMailView(mailList);
//		System.out.println("|" + mailList.size() + "|");
		return Status.OK_STATUS;
	}

}
