package org.eclipse.remail.modules;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.remail.Activator;
import org.eclipse.remail.Mail;
import org.eclipse.remail.preferences.PreferenceConstants;

public class MboxCore {

	String lastFrom;
	LinkedList<Mail> mailList;
	IPreferenceStore store;

	public MboxCore() {
		store = Activator.getDefault().getPreferenceStore();
	}

	public LinkedList<Mail> getMailsByClassname(String classname) {
		mailList = new LinkedList<Mail>();
		try {
			BufferedReader f = prepareRead(getFirstPath(store.getString(PreferenceConstants.P_MBOX_PATH)));
			f.readLine();
			// System.out.println(line);
			while (getNextMail(f, classname))
				;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("ThunderSource OK");
		return mailList;
	}

	public String getFirstPath(String pathlist)
	{
		return pathlist.split(File.pathSeparator)[0];
	}

	private boolean getNextMail(BufferedReader f, String classname)
			throws IOException {
		Date date = null;
		SimpleDateFormat df = new SimpleDateFormat("MMM dd HH:mm:ss z yyyy");
		String author = "";
		String permalink = "";
		String threadlink = "";
		String subject = "";
		String line = "";
		StringBuffer text = new StringBuffer();
		boolean body = false;
		boolean hit = false;
		while (((line = f.readLine()) != null) && !line.startsWith("From ")) {
			if (!body) {
				// System.out.println(line);
				if (line.startsWith("Date:")) {
					try
					{ // eg. Thu Jan 01 09:19:42 CET 2009	
						date = df.parse(line.substring(10));
					} catch (ParseException e)
					{
						// TODO Auto-generated catch block
						date = null;
						continue;
						//e.printStackTrace();
					}
				} else if (line.startsWith("From:")) {
					author = line.substring(6);
				} else if (line.startsWith("Subject:")) {
					subject = line.substring(9);
				} else if (line.startsWith("X-Permalink:")) {
					permalink = line.substring(13);
				} else if (line.startsWith("X-Thread:")) {
					threadlink = line.substring(10);
				} else if (line.equals("")) {
					body = true;
					continue;
				}
			} else {
				if (line.contains(classname)) {
					hit = true;
					// System.out.println(date);
					// System.out.print("1");
				}
				text.append(line + "\n");
			}
		}
		if (hit == true) {
			if(date != null)
			mailList.add(new Mail(0, subject, date, author, permalink,
					threadlink, text.toString(), classname));
		}
		if (line == null)
			return false;
		else
			return true;
	}

	private static BufferedReader prepareRead(String filePath)
			throws java.io.IOException {
		/*
		 * char[] buf = new char[512]; StringBuilder builder = new
		 * StringBuilder(); FileReader reader = new FileReader(filePath);
		 * BufferedReader buffReader = new BufferedReader(reader); try { int
		 * len; while (((len = buffReader.read(buf, 0, buf.length)) != -1)) {
		 * builder.append(buf, 0, len); } } finally { buffReader.close(); }
		 * return builder;
		 */

		File f = new File(filePath);
		FileReader fr = new FileReader(f);
		BufferedReader buffReader = new BufferedReader(fr);

		return buffReader;

		// char[] c = new char[(int) f.length()];
		// fr.read(c, 0, (int) f.length());
		//		
		// fr.close();
		// System.out.print(c);
		// return c;
	}
}
