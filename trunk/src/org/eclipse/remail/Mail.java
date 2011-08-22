package org.eclipse.remail;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Data class implementing the data structure of a single email. Email content
 * is not included as it is not given by the search, but is instead queried only
 * when its needed by the MailContentView. (To save the data flow between DB and
 * the plugin)
 * 
 * @author V. Humpa
 */
public class Mail implements Comparable {
	private String id;
	private String subject;
	private String author;
	private String permalink;
	private String threadlink;
	private Date timestamp;
	private String classname; // name of the class searched
	private String text;
	
	private double globalRating;

	public Mail(String id, String subject, Date timestamp, String classname) {
		this.setId(id);
		this.setSubject(subject);
		this.setTimestamp(timestamp);
		this.setClassname(classname);
	}

	public Mail(String id, String subject, Date timestamp, String author, String permalink,
			String threadlink, String text, String classname) {
		this.setId(id);
		this.setSubject(subject);
		this.setTimestamp(timestamp);
		this.setAuthor(author);
		this.setPermalink(permalink);
		this.setThreadlink(threadlink);
		this.setText(text);
		this.setClassname(classname);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getClassname() {
		return classname;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setThreadlink(String threadlink) {
		this.threadlink = threadlink;
	}

	public String getThreadlink() {
		return threadlink;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public double getGlobalRating() {
		return globalRating;
	}

	public void setGlobalRating(double globalRating) {
		this.globalRating = globalRating;
	}

	/**
	 * Implements mandatory method from Comparable, allows for <,> operation
	 */
	@Override
	public int compareTo(Object o) {
		Mail mail = (Mail) o;
		if (this.getTimestamp()==null)
			return 1;
		if (mail.getTimestamp()==null)
			return -1;
		if (this.getTimestamp().before(mail.getTimestamp()))
			return -1;
		else if (this.getTimestamp().after(mail.getTimestamp()))
			return 1;
		return 0;
	}

	/**
	 * @deprecated Inefficient, has complexity O(n). Replaced by
	 *             {@link #mergeSortMailLists(LinkedList, LinkedList)} Merges two
	 *             lists of Mail into one, intersection compared by permalink
	 *             values
	 * @param mailList
	 * @param mailList2
	 * @return
	 */
	public static LinkedList<Mail> mergeMailLists(LinkedList<Mail> mailList,
			LinkedList<Mail> mailList2) {
		if (mailList == null || mailList2 == null)
			return null;
		LinkedList<Mail> mailList3 = new LinkedList<Mail>();
		for (Mail m2 : mailList2) {
			boolean newmail = true;
			for (Mail m1 : mailList) {
				if (m1.getPermalink().startsWith(m2.getPermalink())) {
					newmail = false;
					break;
				}
			}
			if (newmail)
				mailList3.add(m2);
		}
		mailList.addAll(mailList3);
		return mailList;
	}

	/**
	 * Merges two lists of Mail into one, intersection compared by permalink
	 * values. Complexity of this is n*log(n);
	 * 
	 * @param mailList1
	 * @param mailList2
	 * @return the merged list
	 */
	@SuppressWarnings("unchecked")
	public static LinkedList<Mail> mergeSortMailLists(LinkedList<Mail> mailList1,
			LinkedList<Mail> mailList2) {
		LinkedList<Mail> mergedList = new LinkedList<Mail>();

		// sort the two collections
		Collections.sort(mailList1);
		Collections.sort(mailList2);

		// merge them
		boolean list1end = false;
		boolean list2end = false;
//		int cont=0;
		while (!list1end || !list2end) {
			Mail mailFromList1 = null;
			Mail mailFromList2 = null;
//			System.out.println(cont);
//			cont++;
			try {
				// get and remove the first element in the list
				mailFromList1 = mailList1.removeFirst();
			} catch (NoSuchElementException e) {
				// list is empty
				list1end = true;
				mailFromList1 = null;
			}
			try {
				// get and remove the first element in the list
				mailFromList2 = mailList2.removeFirst();
			} catch (NoSuchElementException e) {
				// list is empty
				list2end = true;
				mailFromList2 = null;
			}

			// compare the two mail to insert in order
			if (mailFromList1 == null && mailFromList2 != null){
				mergedList.add(mailFromList2);
//				System.out.println("list2-> "+mailFromList2.subject );
			}
			else if (mailFromList2 == null && mailFromList1 != null){
				mergedList.add(mailFromList1);
//				System.out.println("list1->"+mailFromList1.subject );
			}
			else if (mailFromList1 != null && mailFromList2 != null) {
//				System.out.println("list1->"+mailFromList1.subject+" "+"list2-> "+mailFromList2.subject);
				int compare = mailFromList1.compareTo(mailFromList2);
				switch (compare) {
					case -1: {
						/*
						 * The mail from List1 has to be inserted before mail
						 * form List2
						 */
						mergedList.add(mailFromList1);
						mergedList.add(mailFromList2);
						break;
					}
					case 1: {
						/*
						 * The mail from List2 has to be inserted before mail
						 * from List1
						 */
						mergedList.add(mailFromList2);
						mergedList.add(mailFromList1);
						break;
					}
					case 0: {
						/*
						 * The mail from List1 and List2 are the same. Insert
						 * only one of them!
						 */
						mergedList.add(mailFromList1);
						break;
					}
				}
			}

		}

		return mergedList;
	}
	
	public String toString(){
		String s=super.toString()+"\n";
		if(this.author!=null)
			s+=" -- "+this.author+"\n";
		else
			s+=" -- author null";
		if(this.subject!=null)
			s+=" -- "+this.subject+"\n";
		else
			s+=" -- subject null";
		if(this.timestamp!=null)
			s+=" -- "+this.timestamp.toString();
		else
			s+=" -- date null";
		return s;
	}
}
