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

	public SQLiteMailListConstructor(IResource resource)
	{
		this.project = resource.getProject();
	}

	private void prepareSQLite() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:"
				+ project.getLocation().toString() + File.separator
				+ "remail.db");
		stat = conn.createStatement();
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
	public LinkedList<Mail> getResultMailList(String name) throws SQLException,
			ClassNotFoundException
	{
		LinkedList<Mail> mailList = new LinkedList<Mail>();
		this.prepareSQLite();
		ResultSet rs = stat.executeQuery("select * from " + name + ";");
		while (rs.next())
		{
			Date date = new Date();
			date.setTime(Long.parseLong(rs.getString("date")));
			Mail mail = new Mail(Integer.parseInt(rs.getString("id")), rs
					.getString("subject"), date, rs.getString("author"), rs
					.getString("permalink"), rs.getString("threadlink"), rs
					.getString("text"), rs.getString("classname"));
			mailList.add(mail);
		}
		rs.close();
		mailList = Search.applyMessageFilters(mailList);
		if (mailList.size() > 0)
			return mailList;
		return null;
	}
}
