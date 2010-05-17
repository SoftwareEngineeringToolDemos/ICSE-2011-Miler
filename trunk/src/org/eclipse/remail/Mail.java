package org.eclipse.remail;

import java.util.Date;
import java.util.LinkedList;

/**
 * Data class implementing the data structure of a single email. Email content
 * is not included as it is not given by the search, but is instead queried only
 * when its needed by the MailContentView. (To save the data flow between DB and
 * the plugin)
 * 
 * Remark 1: Now the content is included for the use with Mbox data source. When
 * DB is used it is still not used.
 * 
 * @author vita
 */
public class Mail implements Comparable
{
	private int id;
	private String subject;
	private String author;
	private String permalink;
	private String threadlink;
	private Date timestamp;
	private String classname; // name of the class searched
	private String text;

	public Mail(int id, String subject, Date timestamp, String classname)
	{
		this.setId(id);
		this.setSubject(subject);
		this.setTimestamp(timestamp);
		this.setClassname(classname);
	}

	public Mail(int id, String subject, Date timestamp, String author,
			String permalink, String threadlink, String text, String classname)
	{
		this.setId(id);
		this.setSubject(subject);
		this.setTimestamp(timestamp);
		this.setAuthor(author);
		this.setPermalink(permalink);
		this.setThreadlink(threadlink);
		this.setText(text);
		this.setClassname(classname);
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setClassname(String classname)
	{
		this.classname = classname;
	}

	public String getClassname()
	{
		return classname;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setPermalink(String permalink)
	{
		this.permalink = permalink;
	}

	public String getPermalink()
	{
		return permalink;
	}

	public void setThreadlink(String threadlink)
	{
		this.threadlink = threadlink;
	}

	public String getThreadlink()
	{
		return threadlink;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	@Override
	public int compareTo(Object o)
	{
		Mail mail = (Mail) o;
		if (this.getTimestamp().before(mail.getTimestamp()))
			return -1;
		else if (this.getTimestamp().after(mail.getTimestamp()))
			return 1;
		return 0;
	}

	public static LinkedList<Mail> mergeMailLists(LinkedList<Mail> mailList,
			LinkedList<Mail> mailList2)
	{
		if (mailList == null || mailList2 == null)
			return null;
		LinkedList<Mail> mailList3 = new LinkedList<Mail>();
		for (Mail m2 : mailList2)
		{
			boolean newmail = true;
			for (Mail m1 : mailList)
			{
				if (m1.getPermalink().startsWith(m2.getPermalink()))
				{
					newmail = false;
					break;
				}
			}
			if(newmail)
				mailList3.add(m2);
		}
		mailList.addAll(mailList3);
		return mailList;
	}
}
