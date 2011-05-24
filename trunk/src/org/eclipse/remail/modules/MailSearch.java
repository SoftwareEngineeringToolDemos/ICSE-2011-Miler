package org.eclipse.remail.modules;

import java.util.LinkedList;

import org.eclipse.remail.Mail;

/**
 * Defines the methods that need to be implemented by any data-source solution.
 * Inputs of these methods are given by the particular needs of the linking method.
 * 
 * @author V. Humpa
 *
 */
public interface MailSearch {
	LinkedList<Mail> caseSensitiveSearch(String name);
	LinkedList<Mail> caseInsensitiveSearch(String name);
	LinkedList<Mail> strictRegexpSearch(String path,String name);
	LinkedList<Mail> looseRegexpSearch(String path,String name);
	LinkedList<Mail> dictionarySearch(String path,String name);
	LinkedList<Mail> camelCaseSearch(String path, String name);
}
