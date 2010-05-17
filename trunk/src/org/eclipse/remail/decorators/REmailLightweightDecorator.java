package org.eclipse.remail.decorators;

import java.io.File;
import java.sql.*;
import java.util.LinkedList;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.remail.Mail;
import org.eclipse.remail.Search;
import org.eclipse.remail.util.SQLiteMailListConstructor;
import org.eclipse.swt.graphics.Color;

public class REmailLightweightDecorator implements ILightweightLabelDecorator
{

	@Override
	public void decorate(Object resource, IDecoration decoration)
	{
		// TODO Auto-generated method stub
		IResource res = (IResource) resource;
		LinkedList<Mail> mailList = new LinkedList<Mail>();
		//System.out.println(JavaCore.create(res).getClass().getName());
		//if(JavaCore.create(res) instanceof IPackageFragment)
		//	System.out.println("Sulin");
		if (JavaCore.create(res) instanceof ICompilationUnit)
		{
			if((mailList = this.getMailList(res)) != null)
				decoration.addSuffix(" (" + this.getMailList(res).size() + ")");	
		}
		else if (JavaCore.create(res) instanceof IPackageFragment)
		{
			IPackageFragment pf = (IPackageFragment) JavaCore.create(res);
			ICompilationUnit[] compilationUnits = null;
			//LinkedList<ICompilationUnit> compList = new LinkedList<ICompilationUnit>();
			try
			{
				compilationUnits = pf.getCompilationUnits();
			} catch (JavaModelException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (ICompilationUnit cu : compilationUnits)
			{
				mailList = Mail.mergeMailLists(mailList, this.getMailList(cu.getResource()));
			}
			if (mailList != null && compilationUnits.length > 0)
				decoration.addSuffix(" (" + mailList.size() + ")");	
			//decoration.setForegroundColor(new Color(null, 0, 0, 254));
		}
	}

	private LinkedList<Mail> getMailList(IResource res)
	{
		String name = res.getName();
		String path = res.getProjectRelativePath().toString();
		LinkedList<Mail> mailList = new LinkedList<Mail>();
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
			stat.executeUpdate("create table if not exists emails (permalink, subject, date, author, threadlink, text, visible);");
			stat.executeUpdate("create table if not exists classes (id INTEGER PRIMARY KEY AUTOINCREMENT, name, path);");
			stat.executeUpdate("create table if not exists hits (id INTEGER, permalink);");
			//ResultSet rs = stat.executeQuery("select hits from hits where name = '" + name
			//		+ "';");
			name = name.split("\\.")[0];
			
			/*int id;
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
				return null;
			}*/
			conn.close();
			SQLiteMailListConstructor mailListConstructor = new SQLiteMailListConstructor(res);
			try
			{
				mailList = mailListConstructor.getResultMailList();
			} catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mailList;
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
