package org.eclipse.remail.decorators;

import java.io.File;
import java.sql.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.remail.Search;

public class REmailLightweightDecorator implements ILightweightLabelDecorator
{

	@Override
	public void decorate(Object resource, IDecoration decoration)
	{
		// TODO Auto-generated method stub
		IResource res = (IResource) resource;
		String name = res.getName();
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
				ResultSet rs = stat.executeQuery("select hits from hits where name = '" + name
						+ "';");
				if (rs.next())
				{
					System.out.println(name + ": " + rs.getString("hits"));
					decoration.addSuffix(" (" + rs.getString("hits") + ")");
				}
				rs.close();
				conn.close();
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
