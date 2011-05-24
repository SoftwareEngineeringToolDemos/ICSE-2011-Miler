package org.eclipse.remail.preferences;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

/**
 * A field editor to edit filter strings.
 * 
 * @author V. Humpa
 */
public class FilterListEditor extends ListEditor
{
	/**
	 * Creates a new path field editor
	 */
	protected FilterListEditor()
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
	public FilterListEditor(String name, String labelText, Composite parent)
	{
		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected String createList(String[] items)
	{
		StringBuffer path = new StringBuffer("");//$NON-NLS-1$

		for (int i = 0; i < items.length; i++)
		{
			path.append(items[i]);
			path.append(";");
		}
		return path.toString();
	}

	@Override
	protected String getNewInputObject()
	{
		InputDialog dialog = new InputDialog(getShell(), "New Message Filter",
				"Enter Regular Expression", "", null);
		if (dialog.open() == Window.OK)
		{
			// User clicked OK; update the label with the input
			if (!dialog.getValue().matches(""))
				return dialog.getValue();
			else
				return null;
		} else
			return null;
		// TextInputDialog dialog = new TextInputDialog(getShell());
		// return dialog.open();
	}

	@Override
	protected String[] parseString(String stringList)
	{
		StringTokenizer st = new StringTokenizer(stringList, ";");
		ArrayList v = new ArrayList();
		while (st.hasMoreElements())
		{
			v.add(st.nextElement());
		}
		return (String[]) v.toArray(new String[v.size()]);
	}

}
