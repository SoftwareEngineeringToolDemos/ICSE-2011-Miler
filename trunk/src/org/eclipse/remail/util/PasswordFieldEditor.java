package org.eclipse.remail.util;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * A subclass of StringFieldEditor used to insert password
 * in a secret way!
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class PasswordFieldEditor extends StringFieldEditor {
	public PasswordFieldEditor(String name, String label, Composite parent) {
		super(name, label, parent);
	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);
		getTextControl().setEchoChar('*');
	}
}
