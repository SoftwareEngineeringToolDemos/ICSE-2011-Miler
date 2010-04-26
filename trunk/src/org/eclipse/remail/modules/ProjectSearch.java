package org.eclipse.remail.modules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

import java.sql.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.remail.Activator;
import org.eclipse.remail.Mail;
import org.eclipse.remail.MailView;
import org.eclipse.remail.Search;

public class ProjectSearch implements Runnable
{
	LinkedList<ICompilationUnit> compList;
	IProject project;
	FileOutputStream out; // declare a file output object
	PrintStream p; // declare a print stream object
	Connection conn;
	String projectName;
	Statement stat;

	public ProjectSearch(IResource resource,
			LinkedList<ICompilationUnit> compList)
	{
		this.project = resource.getProject();
		this.compList = compList;
		this.projectName = project.getName();
	}

	private void prepareFile()
	{
		try
		{
			String filePath = this.project.getLocation()
					+ File.separator + ".remail";
			File file = new File(filePath);

			// Create file if it does not exist
			if (!file.createNewFile())
			{
				file.delete();
				file.createNewFile();
			}
			out = new FileOutputStream(filePath);
			p = new PrintStream(out);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void prepareSQLite() throws Exception
	{
		Class.forName("org.sqlite.JDBC");
		this.conn = DriverManager.getConnection("jdbc:sqlite:"
				+ project.getLocation().toString()
				+ File.separator + "remail.db");
		stat = conn.createStatement();
		// stat.executeUpdate("drop table if exists "+ this.projectName +";");
		//stat.executeUpdate("create table hits (name, hits);");
		stat.executeUpdate("create table if not exists emails (permalink, subject, date, author, threadlink, text);");
		stat.executeUpdate("create table if not exists classes (id INTEGER PRIMARY KEY AUTOINCREMENT, name, path);");
		stat.executeUpdate("create table if not exists hits (id INTEGER, permalink);");
	}

	private void searchAll() throws Exception
	{
		// this.prepareFile();
//		PreparedStatement prep = conn
//				.prepareStatement("insert into hits values (?, ?);");
		for (ICompilationUnit cu : compList)
		{
			this.searchCompilationUnit(cu);
		}
//		conn.setAutoCommit(false);
//		prep.executeBatch();
//		conn.setAutoCommit(true);
		conn.close();
		
		Activator.getDefault().getWorkbench().getDecoratorManager().setEnabled("org.eclipse.remail.decorators.REmailLightweightDecorator", false);
		Activator.getDefault().getWorkbench().getDecoratorManager().setEnabled("org.eclipse.remail.decorators.REmailLightweightDecorator", true);
		
	}

	private  void saveResults(String name, String path, LinkedList<Mail> MailList) throws SQLException
	{
		name = name.split("\\.")[0];
		ResultSet rs = stat.executeQuery("select count(*) from classes where name = '" + name + "' and path = '"+path+"';");
		rs.next();
		if(rs.getInt(1) == 0){
			stat.executeUpdate("insert into classes (name, path) values('"+name+"','"+path+"')");
		}
		rs.close();
		rs = stat.executeQuery("select id from classes where name = '" + name + "' and path = '"+path+"';");
		rs.next();
		int id = rs.getInt(1);
		rs.close();
		//stat.executeUpdate("drop table if exists " + name + ";");
		//stat.executeUpdate("create table " + name + " (id, subject, date, author, permalink, threadlink, text, classname);");
		PreparedStatement mailPrep = conn.prepareStatement("insert into emails values (?,?,?,?,?,?);");
		PreparedStatement hitsPrep = conn.prepareStatement("insert into hits values (?,?);");
		for (Mail mail : MailList)
		{
			ResultSet rs2 = stat.executeQuery("select count(*) from emails where permalink = '" + mail.getPermalink() + "';");
			rs2.next();
			if(rs2.getInt(1) == 0){
				//classPrep.setString(1, "0");
				mailPrep.setString(1, mail.getPermalink());
				mailPrep.setString(2, mail.getSubject());
				mailPrep.setString(3, String.valueOf(mail.getTimestamp().getTime()));
				//classPrep.setString(3, mail.getTimestamp().toString());
				mailPrep.setString(4, mail.getAuthor());
				//classPrep.setString(5, mail.getPermalink());
				mailPrep.setString(5, mail.getThreadlink());
				mailPrep.setString(6, mail.getText());
				//classPrep.setString(7, mail.getClassname());
				mailPrep.addBatch();
			}
			hitsPrep.setInt(1, id);
			hitsPrep.setString(2, mail.getPermalink());
			hitsPrep.addBatch();
		}
		stat.executeUpdate("delete from hits where id = "+id+";");
		conn.setAutoCommit(false);
		mailPrep.executeBatch();
		hitsPrep.executeBatch();
		conn.setAutoCommit(true);
	}
	
//	private void insertHitsRow(String name, int count, PreparedStatement prep) throws SQLException
//	{
//		stat.executeUpdate("delete from hits where name = '"+name+"'");
//		prep.setString(1, name);
//		prep.setString(2, String.valueOf(count));
//		prep.addBatch();
//	}
	
	private void searchCompilationUnit(ICompilationUnit cu) throws Exception
	{
		IResource res = cu.getResource();
		String name = res.getName();
		System.out.println(name);
		
		IPath fullPath = res.getProjectRelativePath();
		Search search = new Search();
		LinkedList<Mail> mailList = search.Execute(name, fullPath.toString(), true);
		
		this.saveResults(name, fullPath.toString(), mailList);
//		if (this.compList.size() == 1)
//			Search.updateMailView(mailList);
		//this.insertHitsRow(name, MailList.size(), prep);
	}

	@Override
	public void run()
	{
		try
		{
			this.prepareSQLite();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			this.searchAll();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
