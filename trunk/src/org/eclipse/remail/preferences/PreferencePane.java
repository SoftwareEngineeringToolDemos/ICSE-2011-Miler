package org.eclipse.remail.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.remail.Activator;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing FieldEditorPreferencePage, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 * 
 * @author V. Humpa
 */

public class PreferencePane extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	public PreferencePane()
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("General settings");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{

		addField(new RadioGroupFieldEditor(
				PreferenceConstants.P_SOURCE,
				"Mailing list source: ",
				1,
				new String[][] { { "&Thunderbird (Mbox)", "mbox" },
						{ "Po&stgreSQL", "postgre" }, 
						{ "CouchDB", "couchdb"} },
				getFieldEditorParent()));
		addField(new ComboFieldEditor(
				PreferenceConstants.P_METHOD,
				"Prefered classname matching method: ",
				new String[][] { /*{ "Case Insensitive", "searchInsensitive" },*/
						{ "Case Sensitive", "searchSensitive" },
						{ "Strict Regular Exp.", "searchStrict" },
						{ "Loose Regular Exp.", "searchLoose" },
						{ "Dictionary Matching (Postgre only)", "searchDict" },
						{ "CamelCase", "searchCamel" },},
				getFieldEditorParent()));
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