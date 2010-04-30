package org.eclipse.remail.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.remail.Mail;
import org.eclipse.remail.Search;

public class SQLiteMailListConstructor
{
	private IProject project;
	private Connection conn;
	private Statement stat;
	private IResource res;
	private String name;
	private String path;

	public SQLiteMailListConstructor(IResource resource)
	{
		this.project = resource.getProject();
		this.res = resource;
		this.name = this.res.getName().split("\\.")[0];
		this.path = this.res.getProjectRelativePath().toString();
	}

	private void prepareSQLite() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:"
				+ project.getLocation().toString() + File.separator
				+ "remail.db");
		stat = conn.createStatement();
		stat.executeUpdate("create table if not exists emails (permalink, subject, date, author, threadlink, text);");
		stat.executeUpdate("create table if not exists classes (id INTEGER PRIMARY KEY AUTOINCREMENT, name, path);");
		stat.executeUpdate("create table if not exists hits (id INTEGER, permalink);");
	}

	/**
	 * Gets the list of cashed e-mail results of the classname selected
	 * 
	 * @param name
	 *            - name of the class - without extension
	 * @return List of Mail objects - or null if class results weren't cashed
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public LinkedList<Mail> getResultMailList() throws SQLException,
			ClassNotFoundException
	{
		LinkedList<Mail> mailList = new LinkedList<Mail>();
		this.prepareSQLite();
		int id;
		ResultSet rs = stat.executeQuery("select id from classes where name = '" + name + "' and path = '"+path+"';");
		if(rs.next())
		{
			id = rs.getInt(1);
			rs.close();
		}
		else
		{
			rs.close();
			conn.close();
			return mailList;
		}
		rs = stat.executeQuery("select permalink from hits where id="+id+";");
		//System.out.println(rs.getInt(1));
		LinkedList<String> permaList = new LinkedList<String>();
		while(rs.next())
			permaList.add(rs.getString(1));
		rs.close();
		for (String permalink : permaList)
		{
			//System.out.println(permalink);
			ResultSet rs2 = stat.executeQuery("select * from emails where permalink='"+permalink+"'");
			rs2.next();
			Date date = new Date();
			date.setTime(Long.parseLong(rs2.getString("date")));
			Mail mail = new Mail(0, rs2
					.getString("subject"), date, rs2.getString("author"), rs2
					.getString("permalink"), rs2.getString("threadlink"), rs2
					.getString("text"), name);
			mailList.add(mail);
			rs2.close();
		}
		conn.close();
		mailList = Search.applyMessageFilters(mailList);
		if (mailList.size() > 0)
			return mailList;
		return mailList;
	}
}
