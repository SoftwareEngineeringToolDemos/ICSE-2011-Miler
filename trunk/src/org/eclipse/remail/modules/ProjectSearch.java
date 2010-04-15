package org.eclipse.remail.modules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

import java.sql.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.remail.Search;

public class ProjectSearch implements Runnable
{
	LinkedList<ICompilationUnit> compList;
	IJavaProject javaProject;
	FileOutputStream out; // declare a file output object
	PrintStream p; // declare a print stream object
	Connection conn;
	String projectName;

	public ProjectSearch(IJavaProject javaProject,
			LinkedList<ICompilationUnit> compList)
	{
		this.javaProject = javaProject;
		this.compList = compList;
		this.projectName = javaProject.getProject().getName();
	}

	private void prepareFile()
	{
		try
		{
			String filePath = this.javaProject.getProject().getLocation()
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
        this.conn = DriverManager.getConnection("jdbc:sqlite:remail.db");
        Statement stat = conn.createStatement();
        stat.executeUpdate("drop table if exists "+ this.projectName +";");
        stat.executeUpdate("create table "+this.projectName+" (name, hits);");
	}

	private void searchAll() throws Exception
	{
		//this.prepareFile();
		PreparedStatement prep = conn.prepareStatement("insert into "+this.projectName+" values (?, ?);");
		for (ICompilationUnit cu : compList)
		{
			this.searchCompilationUnit(cu,prep);
		}
		conn.setAutoCommit(false);
        prep.executeBatch();
        conn.setAutoCommit(true);
        conn.close();
	}

	private void searchCompilationUnit(ICompilationUnit cu, PreparedStatement prep) throws Exception
	{
		IResource res = cu.getResource();
		String name = res.getName();
		IPath fullPath = res.getProjectRelativePath();
		Search search = new Search();
		int count = search.Execute(name, fullPath.toString(), true).size();
		prep.setString(1, name);
		prep.setString(2, String.valueOf(count));
		prep.addBatch();
		
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
