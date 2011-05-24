package org.eclipse.remail.decorators;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.eclipse.remail.util.SQLiteMailListConstructor;

/**
 * Class implements creation of package explorer decoration, taking care of the class results numbering
 * @author V. Humpa
 *
 */
public class REmailLightweightDecorator implements ILightweightLabelDecorator
{

	/**
	 * Provides packages explorer decoration. Check RCP manual for
	 * rationale
	 */
	@Override
	public void decorate(Object resource, IDecoration decoration)
	{
		IResource res = (IResource) resource;
		LinkedList<Mail> mailList = new LinkedList<Mail>();
		if (JavaCore.create(res) instanceof ICompilationUnit)
		{
			if((mailList = this.getMailList(res)) != null)
				decoration.addSuffix(" (" + this.getMailList(res).size() + ")");	
		}
		else if (JavaCore.create(res) instanceof IPackageFragment)
		{
			IPackageFragment pf = (IPackageFragment) JavaCore.create(res);
			ICompilationUnit[] compilationUnits = null;
			try
			{
				compilationUnits = pf.getCompilationUnits();
			} catch (JavaModelException e)
			{
				e.printStackTrace();
			}
			for (ICompilationUnit cu : compilationUnits)
			{
				mailList = Mail.mergeMailLists(mailList, this.getMailList(cu.getResource()));
			}
			if (mailList != null && compilationUnits.length > 0)
				decoration.addSuffix(" (" + mailList.size() + ")");	
		}
	}

	/**
	 * Returns a list of Mails associated with particular kind of resource
	 * @param res
	 * @return
	 */
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
			name = name.split("\\.")[0];
			conn.close();
			SQLiteMailListConstructor mailListConstructor = new SQLiteMailListConstructor(res);
			try
			{
				mailList = mailListConstructor.getResultMailList();
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		} catch (SQLException e)
		{
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
