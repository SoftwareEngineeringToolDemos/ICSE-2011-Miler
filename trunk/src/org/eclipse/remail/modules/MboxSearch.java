package org.eclipse.remail.modules;

import java.util.LinkedList;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.remail.Mail;

/**
 * MailSearch implementation that allows for the linking search using different
 * linking methods from the MBOX source
 * 
 * @author V. Humpa
 *
 */
public class MboxSearch implements MailSearch
{

	public MboxCore source;
	LinkedList<Mail> mailList = null;

	public MboxSearch()
	{
		this.source = new MboxCore();
		mailList = new LinkedList<Mail>();
	}

	/**
	 * Implements the CamelCase method.
	 */
	@Override
	public LinkedList<Mail> camelCaseSearch(String path, String name)
	{
		if (!name.matches(".*[A-Z].*[A-Z].*")) {
			return this.strictRegexpSearch(path, name);
		} else {
			return this.caseSensitiveSearch(name);
		}

	}

	/**
	 * Implements the dictionary method - which is not supported with MBox,
	 * so it really just calls similarly performing CamelCase instead.
	 */
	@Override
	public LinkedList<Mail> dictionarySearch(String path, String name)
	{
		return this.camelCaseSearch(path, name);

	}

	/**
	 * Implements the Loose Regular expression linking method.
	 */
	@Override
	public LinkedList<Mail> looseRegexpSearch(String path, String name)
	{
		mailList = source.getMailsByClassname(name);
		String entirePackage = path.replaceFirst("/" + name
				+ "(\\.java|\\.class)", "");
		entirePackage = entirePackage.replaceFirst("src/", "");
		entirePackage = entirePackage.replaceAll("/", "(\\\\.|\\\\\\\\|/)");
		Pattern p = Pattern.compile(".*(\\s*)(" + entirePackage
				+ ")?(\\.|\\\\|/)" + name + "(\\.java|\\.class|\\s+|\"|,).*",
				Pattern.DOTALL | Pattern.MULTILINE);
		LinkedList<Mail> mailList2 = new LinkedList<Mail>();
		for (Mail mail : mailList)
		{
			if (p.matcher(mail.getText()).matches())
				mailList2.add(mail);
		}
		return mailList2;
	}

	/**
	 * Implements the Strict Regular expression method.
	 */
	@Override
	public LinkedList<Mail> strictRegexpSearch(String path, String name)
	{
		String packageLastPart = "";
		String restOfPackage = "";
		if (path.split("/").length > 2)
		{
			packageLastPart = path.split("/")[path.split("/").length - 2];
			restOfPackage = path.replaceFirst("src/", "");
			restOfPackage = restOfPackage.replaceFirst("/" + packageLastPart
					+ "/" + name + "(\\.java|\\.class)", "");
			restOfPackage = restOfPackage.replaceAll("/", "(\\\\.|\\\\\\\\|/)");
			mailList = source.getMailsByClassname(name);
			Pattern p = Pattern.compile(".*(\\s*)(" + restOfPackage
					+ ")?(\\.|\\\\|/|\\s)" + packageLastPart + "(\\.|\\\\|/)"
					+ name + "(\\.java|\\.class|\\s+).*", Pattern.DOTALL
					| Pattern.MULTILINE);
			LinkedList<Mail> mailList2 = new LinkedList<Mail>();
			for (Mail mail : mailList)
			{
				if (p.matcher(mail.getText()).matches())
					mailList2.add(mail);
			}
			return mailList2;
		} else
		{
			MessageDialog.openInformation(null, "Warning",
					"No package name available, using Loose Regexp technique.");
			this.looseRegexpSearch(path, name);
			return null;
		}
	}

	/**
	 * Just returns null as insensitive method has been dropped
	 * from REmail
	 */
	@Override
	public LinkedList<Mail> caseInsensitiveSearch(String name)
	{
		return null;
	}

	/**
	 * Implements the Case Sensitive method.
	 */
	@Override
	public LinkedList<Mail> caseSensitiveSearch(String name)
	{
		return source.getMailsByClassname(name);
	}

}
