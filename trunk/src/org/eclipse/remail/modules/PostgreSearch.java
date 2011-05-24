package org.eclipse.remail.modules;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.remail.Mail;

/**
* MailSearch implementation that allows for the linking search using different
* linking methods using postgre storage.
* 
* @author V. Humpa
*
*/
public class PostgreSearch implements MailSearch {

	private PostgreCore search;

	public PostgreSearch(String conn_string, String login, String password) {
		search = new PostgreCore(conn_string,login,password);
	}

	@Override
	public LinkedList<Mail> caseInsensitiveSearch(String name) {
		LinkedList<Mail> mailList = null;
		try {
			mailList = search.caseInsensitiveSearch(name);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mailList;
	}

	/**
	 * Initiates the DB e-mail search using the simple case sensitive classname
	 * matching.
	 * 
	 * @param name
	 *            - classname
	 * @throws SQLException
	 * @throws IOException
	 */
	@Override
	public LinkedList<Mail> caseSensitiveSearch(String name) {
		LinkedList<Mail> mailList = null;
		try {
			mailList = search.caseSensitiveSearch(name);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mailList;
	}

	/**
	 * Initiates the search using the case insensitive strict regexp method, as
	 * described in the wcre2009 paper.
	 * 
	 * @param path
	 *            - the entire path to the class
	 * @param name
	 *            - classname
	 * @throws SQLException 
	 * @throws IOException
	 */
	@Override
	public LinkedList<Mail> strictRegexpSearch(String path, String name) {
		LinkedList<Mail> mailList = null;
		String packageLastPart = "";
		String restOfPackage = "";
		try {
			if (path.split("/").length > 2) // at least src/ + one package part
			// have
			// to be present
			{
				packageLastPart = path.split("/")[path.split("/").length - 2];
				restOfPackage = path.replaceFirst("src/", "");
				restOfPackage = restOfPackage.replaceFirst("/"
						+ packageLastPart + "/" + name + "(\\.java|\\.class)",
						"");
				restOfPackage = restOfPackage.replaceAll("/",
						"(\\\\.|\\\\\\\\|/)");
			} else {
				MessageDialog
						.openInformation(null, "Warning",
								"No package name available, using Loose Regexp technique.");
				mailList = search.LooseRegexpSearch("", name);
				return mailList;
			}

			mailList = search.StrictRegexpSearch(restOfPackage,
					packageLastPart, name);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mailList;
	}

	/**
	 * Initiates the search using the case sensitive loose regexp method
	 * 
	 * @param path
	 *            - the entire path to the class
	 * @param name
	 *            - classname
	 * @throws SQLException
	 * @throws IOException
	 */
	@Override
	public LinkedList<Mail> looseRegexpSearch(String path, String name) {
		LinkedList<Mail> mailList = null;
		String entirePackage = path.replaceFirst("/" + name
				+ "(\\.java|\\.class)", "");
		entirePackage = entirePackage.replaceFirst("src/", "");
		entirePackage = entirePackage.replaceAll("/", "(\\\\.|\\\\\\\\|/)");
		try {
			mailList = search.LooseRegexpSearch(entirePackage, name);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mailList;
	}

	/**
	 * Does either Strict regexp search or simple Case sensitive search, based
	 * on whether the classname exists in English dictionary. (Uses dict in the
	 * db, actual search is implemented in the PosgreSearch class.)
	 * 
	 * @param path
	 *            - the entire path to the class
	 * @param name
	 *            - classname
	 * @throws SQLException
	 * @throws IOException
	 */
	@Override
	public LinkedList<Mail> dictionarySearch(String path, String name) {
		try {
			if (search.isInDictionary(name)) {
				MessageDialog.openInformation(null, "Information",
						"Classname is an English word, using Strict search");
				return this.strictRegexpSearch(path, name);
			} else {
				MessageDialog
						.openInformation(null, "Information",
								"Classname not found in Dictionary, using case sensitive search");
				return this.caseSensitiveSearch(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Does either Strict regexp search or simple Case sensitive search, based
	 * on the number of capital letters in the classname.
	 * 
	 * @param path
	 *            - the entire path to the class
	 * @param name
	 *            - classname
	 * @throws SQLException
	 * @throws IOException
	 */
	@Override
	public LinkedList<Mail> camelCaseSearch(String path, String name) {
		if (!name.matches(".*[A-Z].*[A-Z].*")) {
			return this.strictRegexpSearch(path, name);
		} else {
			return this.caseSensitiveSearch(name);
		}
	}

}
