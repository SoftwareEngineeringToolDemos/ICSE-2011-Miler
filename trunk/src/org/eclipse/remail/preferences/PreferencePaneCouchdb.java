package org.eclipse.remail.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.remail.Activator;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePaneCouchdb extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	StringFieldEditor couchdbHost;
	IntegerFieldEditor couchdbPort;
	
	public PreferencePaneCouchdb (){
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("CouchDB settings");
	}
	
	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
		couchdbHost=new StringFieldEditor(PreferenceConstants.P_COUCHDB_HOST, "CouchDB server: ", getFieldEditorParent());
		addField(couchdbHost);
		couchdbPort=new IntegerFieldEditor(PreferenceConstants.P_COUCHDB_PORT, "CouchDB server port: ", getFieldEditorParent());
		addField(couchdbPort);
	}
	
	@Override
	protected void performDefaults(){
		super.performDefaults();
		couchdbHost.setStringValue("localhost");
		couchdbPort.setStringValue("5984");
	}
}
