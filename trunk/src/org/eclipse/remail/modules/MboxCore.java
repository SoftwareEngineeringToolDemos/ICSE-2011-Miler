package org.eclipse.remail.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.remail.Activator;
import org.eclipse.remail.Mail;
import org.eclipse.remail.preferences.PreferenceConstants;

/**
 * Implements the core of searching through the mbox file for e-mails
 * Basic is the getMailsByClassname, that returns a list of Mail objects
 * that have a classname mentioned in them (Body or any header, really)
 * @author V. Humpa
 *
 */
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
			BufferedReader f = prepareRead(getFirstPath(store
					.getString(PreferenceConstants.P_MBOX_PATH)));
			f.readLine();
			while (getNextMail(f, classname))
				;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mailList;
	}

	public String getFirstPath(String pathlist) {
		return pathlist.split(File.pathSeparator)[0];
	}

	private boolean getNextMail(BufferedReader f, String classname)
			throws IOException {
		Date date = null;
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
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
				if (line.startsWith("Date:")) {
					try {
						date = df.parse(line.substring(6));
					} catch (ParseException e) {
						date = null;
						System.out.println('n');
						continue;
					}
				} else if (line.startsWith("From:")) {
					if (line.length() == 5)
						author = "";
					else
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
				}
				text.append(line + "\n");
			}
		}

		if (hit == true) {
			System.out.println("|");
			if (date != null) {
				System.out.println("X");
				mailList.add(new Mail(0, subject, date, author, permalink,
						threadlink, text.toString(), classname));
			}
		}
		if (line == null)
			return false;
		else
			return true;
	}

	private static BufferedReader prepareRead(String filePath)
			throws java.io.IOException {
		File f = new File(filePath);
		FileReader fr = new FileReader(f);
		BufferedReader buffReader = new BufferedReader(fr);

		return buffReader;
	}
}
