package org.eclipse.remail.util;

import java.text.SimpleDateFormat;

import org.eclipse.remail.Mail;

public class ContentDecorator
{
	private Mail mail;
	private String text;
	private String preText;
	private String postText;

	public ContentDecorator(Mail mail)
	{
		this.mail = mail;
		this.text = mail.getText();
		//makeHTML();
	}

	public void makeHTML()
	{
		text = text.replaceAll(mail.getClassname(), "<b><font color=\"red\">"
				+ mail.getClassname() + "</font></b>");
		text = text.replaceAll("\\n", "<br/>\n");
		preText = ("<html>\n"
				+ "<head>\n"
				+ "<title>"+mail.getSubject()+"</title>\n"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />\n "
				+ "<style type=\"text/css\">\n" + 
						"body, div {\n" + 
						"font-family: verdana, tahoma, arial, helvetica, sans-serif;\n" + 
						"font-size: 12px;\n" + 
						"background:#f5ecca;\n" + 
						"}\n" + 
						"\n" + 
						"#topcontainer{margin: 2px;padding: 5px;background: #ffffff; }\n" + 
						"#headers,\n" + 
						"table.innerheaders {\n" + 
						"    border: 1px solid #aaa;\n" + 
						"    background-color: rgb(245, 245, 245);\n" + 
						"    padding: 5px;\n" + 
						"    margin-bottom: 5px;\n" + 
						"    color: black;\n" + 
						"}\n" + 
						"\n" + 
						"#headers td,\n" + 
						"#headers th,\n" + 
						"table.innerheaders td,\n" + 
						"table.innerheaders th {\n" + 
						"    font-size: 12px;\n" + 
						"}\n" + 
						"\n" + 
						"#headers th,\n" + 
						"table.innerheaders th {\n" + 
						"    text-align: right;\n" + 
						"    vertical-align: top;\n" + 
						"    color: rgb(106, 106, 106);\n" + 
						"    padding-right: 5px;\n" + 
						"    width: 1%;\n" + 
						"}\n" + 
						"#level1, #level6, #level11{\n" + 
						"	border-left-style:solid;\n" + 
						"	border-width:2px;\n" + 
						"	border-color: #0000ff;\n" + 
						"	padding-left: 4px;\n" + 
						"	margin-left: 5px;\n" + 
						"	background: #ffffff;\n" + 
						"}\n" + 
						"#level2, #level7, #level12{\n" + 
						"	border-left-style:solid;\n" + 
						"	border-width:2px;\n" + 
						"	border-color: #00ff00;\n" + 
						"	padding-left: 4px;\n" + 
						"	margin-left: 5px;\n" + 
						"	background: #ffffff;\n" + 
						"}\n" + 
						"#level3, #level8, #level13{\n" + 
						"	border-left-style:solid;\n" + 
						"	border-width:2px;\n" + 
						"	border-color: #ff0000;\n" + 
						"	padding-left: 4px;\n" + 
						"	margin-left: 5px;\n" + 
						"	background: #ffffff;\n" + 
						"}\n" + 
						"#level4, #level9, #level14{\n" + 
						"	border-left-style:solid;\n" + 
						"	border-width:2px;\n" + 
						"	border-color: #00ffff;\n" + 
						"	padding-left: 4px;\n" + 
						"	margin-left: 5px;\n" + 
						"	background: #ffffff;\n" + 
						"}\n" + 
						"#level5, #level10, #level15{\n" + 
						"	border-left-style:solid;\n" + 
						"	border-width:2px;\n" + 
						"	border-color: #000000;\n" + 
						"	padding-left: 4px;\n" + 
						"	margin-left: 5px;\n" + 
						"	background: #ffffff;\n" + 
						"}" +
						"</style>" + "</head> " + "" + "<body>	" + ""
				+ "<div id=\"topcontainer\">\n");
				postText = "</div>\n</body>\n</html>\n";
	}

	public void insertHeader()
	{
		SimpleDateFormat df = new SimpleDateFormat("dd.MM. yyyy HH:mm");
		String author = mail.getAuthor().replaceAll(
				"^(.+)\\s*\\(.*@.*\\).*$", "$1");
		String subject = mail.getSubject();
		if(subject.length() > 30)
			subject = subject.substring(0, 30) + "...";
		text = "<table id=\"headers\">\n" + 
				"<tbody>\n" + 
				"<tr><th>Subject:</th><td>"+ subject +"</td></tr>\n" + 
				"<tr><th>From:</th><td>"+author+"</td></tr>\n" + 
				"<tr><th>Date:</th><td>"+df.format(mail.getTimestamp())+"</td></tr>\n" + 
				"</tbody>\n" + 
				"</table>\n" + text;
	}
	
	public void highLightPreviousMessages()
	{
		int level = 0;
		int indent = 0;
		String newText = "";
		String[] lines = text.split("\\n");
		for (String line : lines)
		{
			if(!line.startsWith(">"))
				indent = 0;
			if(line.startsWith(">"))
				indent = 1;
			if(line.startsWith(">>"))
				indent = 2;
			if(line.startsWith(">>>"))
				indent = 3;
			if(line.startsWith(">>>>"))
				indent = 4;
			if(line.startsWith(">>>>>"))
				indent = 5;
			if(line.startsWith(">>>>>>"))
				indent = 6;
			if(line.startsWith(">>>>>>>"))
				indent = 7;
			if(line.startsWith(">>>>>>>>"))
				indent = 8;
			if(line.startsWith(">>>>>>>>>"))
				indent = 9;
			line = line.substring(indent);
			if (indent > level)
			{
				level++;
				String divs = "";
				for(int i=level;i<=indent;i++)
					divs = divs + "<div id=\"level"+i+"\">";
				line = divs + line;
				level = indent;
			}
			if (indent < level)
			{
				String divs = "";
				for(int i=0; i < (level-indent); i++)
					divs = divs + "</div>";
				line = divs + line;
				level = indent;
			}
			newText = newText + line + '\n';
		}
		this.text = newText;
	}
	
	public String getText()
	{
		return preText + text + postText;
	}
}
