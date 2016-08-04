package org.eclipse.remail.preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.remail.Activator;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

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
		addField(new FilterListEditor(PreferenceConstants.P_FILTER_SUBJECT,
				"Messages with Subject matching following should be:",
				getFieldEditorParent()));
		addField(new ComboFieldEditor(
				PreferenceConstants.P_FILTER_SUBJECT_EXCLUDE, "",
				new String[][] { { "Excluded", "excluded" },
						{ "Included", "included" }, }, getFieldEditorParent()));
		addField(new FilterListEditor(PreferenceConstants.P_FILTER_AUTHOR,
				"Messages with Author matching following should be:",
				getFieldEditorParent()));
		addField(new ComboFieldEditor(
				PreferenceConstants.P_FILTER_AUTHOR_EXCLUDE, "",
				new String[][] { { "Excluded", "excluded" },
						{ "Included", "included" }, }, getFieldEditorParent()));
	}
	
	@Override
	public boolean performOk()
	{
		try
		{
			Activator.getDefault().getWorkbench().getDecoratorManager().setEnabled("org.eclipse.remail.decorators.REmailLightweightDecorator", false);
			Activator.getDefault().getWorkbench().getDecoratorManager().setEnabled("org.eclipse.remail.decorators.REmailLightweightDecorator", true);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.performOk();
	}
	
	@Override
	protected void performApply()
	{
		try
		{
			Activator.getDefault().getWorkbench().getDecoratorManager().setEnabled("org.eclipse.remail.decorators.REmailLightweightDecorator", false);
			Activator.getDefault().getWorkbench().getDecoratorManager().setEnabled("org.eclipse.remail.decorators.REmailLightweightDecorator", true);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.performApply();
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
