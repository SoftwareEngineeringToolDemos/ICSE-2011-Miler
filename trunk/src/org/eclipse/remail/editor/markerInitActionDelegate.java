package org.eclipse.remail.editor;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Implements the setting of markers next to class with search-relevant
 * classnames. A predefined Bookmark marker is used, which also allows
 * for summary.
 * @author V. Humpa
 *
 */
public class markerInitActionDelegate implements IEditorActionDelegate
{

	IEditorPart editor;
	private IProject project;
	private Connection conn;
	private Statement stat;
	private IResource resource;
	
	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor)
	{
		editor = targetEditor;
		IEditorInput iei = editor.getEditorInput();
		if (iei == null)
			return;
		resource = ResourceUtil.getResource(iei);
		project = resource.getProject();
	}
	
	private class IdName
	{
		public IdName(String id, String name)
		{
			this.id = id;
			this.name = name;
		}
		
		String id;
		String name;
	}
	
	@Override
	public void run(IAction action)
	{
		try
		{
			IMarker[] markers = resource.findMarkers(IMarker.BOOKMARK, false, IResource.DEPTH_ONE);
			if(markers.length > 0)
			{
				for (IMarker marker : markers)
					marker.delete();
				return;
			}
				
		} catch (CoreException e1)
		{
			e1.printStackTrace();
		}
		System.out.println(editor.getEditorInput().getName());
		IEditorInput input = editor.getEditorInput();
		IDocument document = (((ITextEditor)editor).getDocumentProvider()).getDocument(input);
		String text = document.get();
		String[] lines = text.split("\\r?\\n");
		try
		{
			this.prepareSQLite();
			LinkedList<IdName> idNameList = new LinkedList<IdName>(); 
			ResultSet rs = stat.executeQuery("SELECT id,name FROM classes;");
			while(rs.next())
				idNameList.add(new IdName(rs.getString(1), rs.getString(2)));
			int lineNumber = 1;
			for (String line : lines)
			{
				int nameNumber = 0;
				for (IdName idName : idNameList)
				{
					if (line.contains(idName.name))
					{
						ResultSet rs2 = stat.executeQuery("select count(permalink) from hits where id="+idName.id+";");
						int count = rs2.getInt(1);
						rs2.close();
						if(count > 0)
						{
							IMarker marker = resource.createMarker(IMarker.BOOKMARK);
							marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
							marker.setAttribute(IMarker.MESSAGE, idName.name + ": "+ count +" emails found");
						}
					}
					nameNumber++;
				}
				lineNumber++;
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	private void prepareSQLite() throws SQLException, ClassNotFoundException
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
	
	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
		// TODO Auto-generated method stub

	}

}
