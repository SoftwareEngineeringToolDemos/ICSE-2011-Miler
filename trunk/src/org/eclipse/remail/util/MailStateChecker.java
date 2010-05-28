package org.eclipse.remail.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.resources.IResource;
import org.eclipse.remail.Mail;

public class MailStateChecker
{
	private Connection conn;
	private Statement stat;
	private Mail mail;
	
	public MailStateChecker(Mail mail, IResource resource)
	{
		this.mail = mail;
		try
		{
			Class.forName("org.sqlite.JDBC");
			this.conn = DriverManager.getConnection("jdbc:sqlite:"
					+ resource.getProject().getLocation().toString() + File.separator
					+ "remail.db");
			stat = conn.createStatement();
			stat.executeUpdate("create table if not exists emails (permalink, subject, date, author, threadlink, text, visible);");
			stat.executeUpdate("create table if not exists classes (id INTEGER PRIMARY KEY AUTOINCREMENT, name, path);");
			stat.executeUpdate("create table if not exists hits (id INTEGER, permalink);");
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isVisible()
	{
		boolean visible = true;
		try
		{
			ResultSet rs = stat.executeQuery("select visible from emails where permalink='"+mail.getPermalink()+"';");
			if(rs.next())
			{
				visible = rs.getBoolean(1);
				rs.close();
			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return visible;
	}
	
	public void changeState(boolean state)
	{
		try
		{
			int visible = 0;
			if (state)
				visible = 1;
			System.out.println("update emails set visible="+visible+" where permalink='"+mail.getPermalink()+"';");
			stat.executeUpdate("update emails set visible="+visible+" where permalink='"+mail.getPermalink()+"';");
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
