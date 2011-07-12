package org.eclipse.remail.util;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.remail.Mail;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

/**
 * Class used to implement a local search based on a keyword
 * on the mail list currently displayed to the user. 
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class LocalMailListSearch {

	private ContainerCheckedTreeViewer viewer;
	private String keyword;
	
	public LocalMailListSearch(ContainerCheckedTreeViewer viewer, String keyword){
		this.viewer=viewer;
		this.keyword=keyword;
	}
	
	/**
	 * Search for the keyword and update the given view
	 */
	public void search(){
		
		@SuppressWarnings("unchecked")
		LinkedList<Mail> mailList= (LinkedList<Mail>)viewer.getInput();
		LinkedList<Mail> searchResults = new LinkedList<Mail>();
		
		System.out.println("In: "+mailList);
		//search
		Pattern p = Pattern.compile(keyword);
//		System.out.println(p.pattern());
		for(Mail mail : mailList){
			Matcher subject = p.matcher(mail.getSubject());
			Matcher text = p.matcher(mail.getText());

			boolean found=false;
			while(subject.find()){
				found=true;
			}
			while(text.find()){
				found=true;
			}
			if(found)
				searchResults.add(mail);
		}
		System.out.println("Found: "+searchResults);
		//update view
		viewer.setInput(searchResults);
	}
}
