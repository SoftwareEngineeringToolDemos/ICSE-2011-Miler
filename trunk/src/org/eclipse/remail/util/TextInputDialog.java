package org.eclipse.remail.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A custom helper dialog used in preferences
 * @author vhumpa
 *
 */
public class TextInputDialog extends Dialog
{
	String result;
	Shell shell;
	Text text;

	public TextInputDialog(Shell parent, int style)
	{
		super(parent, style);
	}

	public TextInputDialog(Shell parent)
	{
		this(parent, 0); // your default style bits go here (not the Shell's
							// style bits)
	}

	public String open()
	{
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		shell.setText("Enter Filter Regexp");
		shell.setSize(250, 80);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 5;
		rowLayout.marginLeft = 5;
		rowLayout.marginRight = 5;
		rowLayout.marginBottom = 5;
		rowLayout.spacing = 0;
		rowLayout.fill = true;
	    shell.setLayout(new FillLayout());
	    text = new Text(shell, SWT.SINGLE | SWT.BORDER);
	    text.setSize(200, 50);
		final Button button = new Button(shell, SWT.PUSH);
	      button.setText("Save");
	      button.addSelectionListener(new SelectionAdapter() {
	         public void widgetSelected(SelectionEvent event) {
	        	 result = text.getText();
	        	 shell.close();
	         }
	      });
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}
}