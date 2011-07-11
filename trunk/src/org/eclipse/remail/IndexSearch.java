package org.eclipse.remail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.remail.util.CacheCouchDB;
import org.eclipse.swt.widgets.Display;

/**
 * Based on the given list of classes to search, IndexSearch commits the linking
 * and subsequently indexing process on all of them. It depends on the remail.modules.Search
 * class. IndexSearch needs to manipulate UI to inform the user about the progress of the
 * search, which is the reason why it has been included outside the non-UI modules package.
 * Inside IndexSearch, we use the Eclipse status bar progressMonitor extension to work with
 * the progress bar that notifies users about proceedings of the search.
 * 
 * @author V. Humpa
 */
public class IndexSearch implements Runnable
{
	LinkedList<ICompilationUnit> compList;
	IProject project;
	FileOutputStream out; // declare a file output object
	PrintStream p; // declare a print stream object
	Connection conn;
	String projectName;
	Statement stat;
	IProgressMonitor progressMonitor;

	public IndexSearch(LinkedList<ICompilationUnit> compList,
			IProgressMonitor pm)
	{
		this.project = compList.get(0).getResource().getProject();
		this.compList = compList;
		this.projectName = project.getName();
		this.progressMonitor = pm;
	}

	private void prepareSQLite() throws Exception
	{
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:"
				+ project.getLocation().toString() + File.separator
				+ "remail.db");
		stat = conn.createStatement();
		stat.executeUpdate("create table if not exists emails (permalink, subject, date, author, threadlink, text, visible);");
		stat.executeUpdate("create table if not exists classes (id INTEGER PRIMARY KEY AUTOINCREMENT, name, path);");
		stat.executeUpdate("create table if not exists hits (id INTEGER, permalink);");
	}

	private void searchAll() throws Exception
	{
		try
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					progressMonitor.beginTask("REmail search...", compList
							.size());
				}
			});
			for (ICompilationUnit cu : compList)
			{
				this.searchCompilationUnit(cu);
			}
		} finally
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					progressMonitor.done();
				}
			});
			conn.close();

			Activator
					.getDefault()
					.getWorkbench()
					.getDecoratorManager()
					.setEnabled(
							"org.eclipse.remail.decorators.REmailLightweightDecorator",
							false);
			Activator
					.getDefault()
					.getWorkbench()
					.getDecoratorManager()
					.setEnabled(
							"org.eclipse.remail.decorators.REmailLightweightDecorator",
							true);
		}
	}

	private void searchCompilationUnit(ICompilationUnit cu)
			throws SQLException, InterruptedException
	{
		IResource res = cu.getResource();
		String name = res.getName();
		System.out.println(name);
		IPath fullPath = res.getProjectRelativePath();
		System.out.println("Starting search!");
		Search search = new Search();
		LinkedList<Mail> mailList = search.Execute(name, fullPath.toString(),
				true);
		System.out.println("---" + mailList.size() + "---");
		CacheCouchDB.addClass(name);
//		try
//		{
//			this.saveResults(name, fullPath.toString(), mailList);
//		} catch (SQLException e)
//		{
//			System.out.println("oops");
//			Thread.sleep(10);
//			this.saveResults(name, fullPath.toString(), mailList);
//		}
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				progressMonitor.worked(1);
			}
		});
	}

	private void saveResults(String name, String path, LinkedList<Mail> MailList)
			throws SQLException
	{
		name = name.split("\\.")[0];
		ResultSet rs = stat
				.executeQuery("select count(*) from classes where name = '"
						+ name + "' and path = '" + path + "';");
		rs.next();
		if (rs.getInt(1) == 0)
		{
			stat.executeUpdate("insert into classes (name, path) values('"
					+ name + "','" + path + "')");
		}
		rs.close();
		rs = stat.executeQuery("select id from classes where name = '" + name
				+ "' and path = '" + path + "';");
		rs.next();
		int id = rs.getInt(1);
		rs.close();
		PreparedStatement mailPrep = conn
				.prepareStatement("insert into emails values (?,?,?,?,?,?,?);");
		PreparedStatement hitsPrep = conn
				.prepareStatement("insert into hits values (?,?);");
		for (Mail mail : MailList)
		{
			ResultSet rs2 = stat
					.executeQuery("select count(*) from emails where permalink = '"
							+ mail.getPermalink() + "';");
			rs2.next();
			if (rs2.getInt(1) == 0)
			{
				mailPrep.setString(1, mail.getPermalink());
				mailPrep.setString(2, mail.getSubject());
				mailPrep.setString(3, String.valueOf(mail.getTimestamp()
						.getTime()));
				mailPrep.setString(4, mail.getAuthor());
				mailPrep.setString(5, mail.getThreadlink());
				mailPrep.setString(6, mail.getText());
				mailPrep.setBoolean(7, true);
				mailPrep.addBatch();
			}
			hitsPrep.setInt(1, id);
			hitsPrep.setString(2, mail.getPermalink());
			hitsPrep.addBatch();
		}
		stat.executeUpdate("delete from hits where id = " + id + ";");
		conn.setAutoCommit(false);
		mailPrep.executeBatch();
		hitsPrep.executeBatch();
		conn.setAutoCommit(true);
	}

	@Override
	public void run()
	{
		try
		{
			this.prepareSQLite();
			this.searchAll();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
