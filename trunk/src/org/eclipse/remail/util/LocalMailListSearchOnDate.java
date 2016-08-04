package org.eclipse.remail.util;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.remail.Mail;
import org.eclipse.remail.Search;

/**
 * Class used to implement a local search based on a keyword on the mail list
 * currently displayed to the user.
 * 
 * The keyword should be a date as string and the search is performed in the
 * Data field of emails
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class LocalMailListSearchOnDate extends LocalMailListSearch {

	public LocalMailListSearchOnDate(TreeViewer viewer, String keyword) {
		super(viewer, keyword);
	}

	@Override
	public void search() {
		@SuppressWarnings("unchecked")
		LinkedList<Mail> mailList = (LinkedList<Mail>) viewer.getInput();
		LinkedList<Mail> searchResults = new LinkedList<Mail>();
		// System.out.println("In: "+mailList+"\n search: "+keyword);
		Pattern p = Pattern.compile(keyword);
		for (Mail mail : mailList) {
			Matcher data = p.matcher(mail.getTimestamp().toString());

			boolean found = false;
			while (data.find()) {
				found = true;
			}
			if (found)
				searchResults.add(mail);
		}
		// System.out.println("Found: "+searchResults);
		// viewer.setInput(searchResults);
		Search.updateMailView(searchResults);
	}
}
