/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * This file is modified version of PathEditor.java. It's been changed
 * to allow working with complete file paths instead of directory ones.
 */
package org.eclipse.remail.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * A field editor to edit file paths. (Modified from PathEditor)
 * 
 * @author V. Humpa
 */
public class FilePathEditor extends ListEditor
{

	/**
	 * The last path, or <code>null if none.
	 */
	private String lastPath;

	/**
	 * The special label text for directory chooser, or <code>null if none.
	 */
	private String fileChooserLabelText;

	/**
	 * Creates a new path field editor
	 */
	protected FilePathEditor()
	{
	}

	/**
	 * Creates a path field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param fileChooserLabelText
	 *            the label text displayed for the file chooser
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public FilePathEditor(String name, String labelText,
			String fileChooserLabelText, Composite parent)
	{
		init(name, labelText);
		this.fileChooserLabelText = fileChooserLabelText;
		createControl(parent);
	}

	/*
	 * (non-Javadoc) Method declared on ListEditor. Creates a single string from
	 * the given array by separating each string with the appropriate
	 * OS-specific path separator.
	 */
	protected String createList(String[] items)
	{
		StringBuffer path = new StringBuffer("");//$NON-NLS-1$

		for (int i = 0; i < items.length; i++)
		{
			path.append(items[i]);
			path.append(File.pathSeparator);
		}
		return path.toString();
	}

	/*
	 * (non-Javadoc) Method declared on ListEditor. Creates a new path element
	 * by means of a directory dialog.
	 */
	protected String getNewInputObject()
	{

		FileDialog dialog = new FileDialog(getShell());
		if (fileChooserLabelText != null)
		{
			dialog.setText(fileChooserLabelText);
		}
		if (lastPath != null)
		{
			if (new File(lastPath).exists())
			{
				dialog.setFilterPath(lastPath);
			}
		}
		String dir = dialog.open();
		if (dir != null)
		{
			dir = dir.trim();
			if (dir.length() == 0)
			{
				return null;
			}
			lastPath = dir;
		}
		return dir;
	}

	/*
	 * (non-Javadoc) Method declared on ListEditor.
	 */
	protected String[] parseString(String stringList)
	{
		StringTokenizer st = new StringTokenizer(stringList, File.pathSeparator
				+ "\n\r");//$NON-NLS-1$
		ArrayList v = new ArrayList();
		while (st.hasMoreElements())
		{
			v.add(st.nextElement());
		}
		return (String[]) v.toArray(new String[v.size()]);
	}
}
