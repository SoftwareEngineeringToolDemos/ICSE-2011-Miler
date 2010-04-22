package org.eclipse.remail.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.remail.Activator;

public class PreferencePaneFilters extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	public PreferencePaneFilters()
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("E-mail result filtering");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		// addField(new ComboFieldEditor(PreferenceConstants.P_FILTER_SUBJECT,
		// "Subject",
		// new String[][] { /*{ "Case Insensitive", "searchInsensitive" },*/
		// { "Subject", "subject" },
		// { "Author", "author" }
		// }, getFieldEditorParent()));
		addField(new FilterListEditor(PreferenceConstants.P_FILTER_SUBJECT,
				"Messages with Subject matching following Reg.Expressions should be:",
				getFieldEditorParent()));
		addField(new ComboFieldEditor(
				PreferenceConstants.P_FILTER_SUBJECT_EXCLUDE, "",
				new String[][] { { "Excluded", "excluded" },
						{ "Included", "included" }, }, getFieldEditorParent()));
		addField(new FilterListEditor(PreferenceConstants.P_FILTER_AUTHOR,
				"Messages with Author matching following Reg.Expressions should be:",
				getFieldEditorParent()));
		addField(new ComboFieldEditor(
				PreferenceConstants.P_FILTER_AUTHOR_EXCLUDE, "",
				new String[][] { { "Excluded", "excluded" },
						{ "Included", "included" }, }, getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}
}
