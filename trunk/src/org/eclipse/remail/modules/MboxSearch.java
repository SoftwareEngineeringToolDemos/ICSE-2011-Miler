package org.eclipse.remail.modules;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.remail.Mail;
import org.eclipse.remail.Search;

public class MboxSearch implements MailSearch
{

	public MboxCore source;
	LinkedList<Mail> mailList = null;

	public MboxSearch()
	{
		this.source = new MboxCore();
		mailList = new LinkedList<Mail>();
	}

	@Override
	public LinkedList<Mail> camelCaseSearch(String path, String name)
	{
		if (!name.matches(".*[A-Z].*[A-Z].*")) {
			return this.strictRegexpSearch(path, name);
		} else {
			return this.caseSensitiveSearch(name);
		}

	}

	@Override
	public LinkedList<Mail> dictionarySearch(String path, String name)
	{
		return this.camelCaseSearch(path, name);

	}

	@Override
	public LinkedList<Mail> looseRegexpSearch(String path, String name)
	{
		mailList = source.getMailsByClassname(name);
		String entirePackage = path.replaceFirst("/" + name
				+ "(\\.java|\\.class)", "");
		entirePackage = entirePackage.replaceFirst("src/", "");
		entirePackage = entirePackage.replaceAll("/", "(\\\\.|\\\\\\\\|/)");
//		System.out.println(entirePackage);
//		System.out.println(".*(\\s*)(" + entirePackage + ")?(\\.|\\\\|/|\\s)"
//				+ name + "(\\.java|\\.class|\\s+|\"|,).*");
		Pattern p = Pattern.compile(".*(\\s*)(" + entirePackage
				+ ")?(\\.|\\\\|/)" + name + "(\\.java|\\.class|\\s+|\"|,).*",
				Pattern.DOTALL | Pattern.MULTILINE);
		// Pattern p = Pattern.compile(".*"+name+".*",Pattern.DOTALL |
		// Pattern.MULTILINE);
		LinkedList<Mail> mailList2 = new LinkedList<Mail>();
		for (Mail mail : mailList)
		{
			if (p.matcher(mail.getText()).matches())
				mailList2.add(mail);
		}
		return mailList2;
		//Search.updateMailView(mailList2);
	}

	@Override
	public LinkedList<Mail> strictRegexpSearch(String path, String name)
	{
		String packageLastPart = "";
		String restOfPackage = "";
		if (path.split("/").length > 2) // at least src/ + one package part
		// have
		// to be present
		{
			packageLastPart = path.split("/")[path.split("/").length - 2];
			restOfPackage = path.replaceFirst("src/", "");
			restOfPackage = restOfPackage.replaceFirst("/" + packageLastPart
					+ "/" + name + "(\\.java|\\.class)", "");
			restOfPackage = restOfPackage.replaceAll("/", "(\\\\.|\\\\\\\\|/)");
			mailList = source.getMailsByClassname(name);
//			System.out.println(".*(\\s*)(" + restOfPackage + ")?(\\.|\\\\|/|\\s)"
//					+ packageLastPart + "(\\.|\\\\|/)" + name
//					+ "(\\.java|\\.class|\\s+).*");
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
			//Search.updateMailView(mailList2);
			return mailList2;
		} else
		{
			MessageDialog.openInformation(null, "Warning",
					"No package name available, using Loose Regexp technique.");
			this.looseRegexpSearch(path, name);
			return null;
		}
	}

	@Override
	public LinkedList<Mail> caseInsensitiveSearch(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedList<Mail> caseSensitiveSearch(String name)
	{
		// System.out.println("HEYYYY");
		return source.getMailsByClassname(name);
		// System.out.println(mailList.get(0).getAuthor().split("(")[0]);
		//Search.updateMailView(mailList);
		// System.out.println(mailList.get(2).getTimestamp());
		// System.out.println(mailList.get(2).getText());
		// System.out.println(mailList.get(2).getAuthor());
		// System.out.println(mailList.get(2).getSubject());
		// System.out.println(mailList.get(2).getThreadlink());
		// System.out.println(mailList.get(2).getPermalink());
	}

}
