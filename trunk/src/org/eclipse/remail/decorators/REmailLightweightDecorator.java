package org.eclipse.remail.decorators;

import java.io.File;
import java.sql.*;
import java.util.LinkedList;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.remail.Mail;
import org.eclipse.remail.Search;
import org.eclipse.remail.util.SQLiteMailListConstructor;

public class REmailLightweightDecorator implements ILightweightLabelDecorator
{

	@Override
	public void decorate(Object resource, IDecoration decoration)
	{
		// TODO Auto-generated method stub
		IResource res = (IResource) resource;
		String name = res.getName();
		String path = res.getProjectRelativePath().toString();
		if (name.contains(".java"))
		{
			try
			{
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Connection conn;
			try
			{
				conn = DriverManager.getConnection("jdbc:sqlite:"
						+ res.getProject().getLocation().toString()
						+ File.separator + "remail.db");
				Statement stat = conn.createStatement();
				stat.executeUpdate("create table if not exists emails (permalink, subject, date, author, threadlink, text);");
				stat.executeUpdate("create table if not exists classes (id INTEGER PRIMARY KEY AUTOINCREMENT, name, path);");
				stat.executeUpdate("create table if not exists hits (id INTEGER, permalink);");
				//ResultSet rs = stat.executeQuery("select hits from hits where name = '" + name
				//		+ "';");
				name = name.split("\\.")[0];
				
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
					return;
				}
				conn.close();
				
				LinkedList<Mail> mailList = new LinkedList<Mail>();
				SQLiteMailListConstructor mailListConstructor = new SQLiteMailListConstructor(res);
				try
				{
					mailList = mailListConstructor.getResultMailList();
				} catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				rs = stat.executeQuery("select count(*) from hits where id = "+id+";");
//				rs.next();
//				{
//					System.out.println(name + ": " + rs.getInt(1));
					decoration.addSuffix(" (" + /*rs.getInt(1)*/mailList.size() + ")");
//				} 
				//				if (rs.next())
//				{
//					System.out.println(name + ": " + rs.getString("hits"));
//					decoration.addSuffix(" (" + rs.getString("hits") + ")");
//				}
//				rs.close();
//				conn.close();
			} catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void addListener(ILabelProviderListener arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0)
	{
		// TODO Auto-generated method stub

	}

}
