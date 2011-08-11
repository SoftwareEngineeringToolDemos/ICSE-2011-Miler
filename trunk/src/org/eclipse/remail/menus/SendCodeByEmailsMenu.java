package org.eclipse.remail.menus;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.remail.views.MailWriter;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * Class used to add a code snippet to the context of the mail. Is the class
 * responsible for the behavior of the command "Send code by Email" in the
 * pop-up menu in the eclipse Editor.
 * 
 * It creates a MailWriter class and set the keywords relatives to the active
 * class in the editor and add the code to the email body.
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class SendCodeByEmailsMenu extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		/*
		 * Get the complete class name and the selected text
		 */
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		String completeClassName = getCompleteClassName();
		String text = getSelectedText(editor);
		
		/*
		 * Open the MailWriter and set the keywords and mail content
		 */
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView("org.eclipse.emailrecommender.MailWriter");
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		ArrayList<String> list = new ArrayList<String>();
		list.add(completeClassName);
		MailWriter mw = (MailWriter) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView("org.eclipse.emailrecommender.MailWriter");
		mw.setKeywords(list);
		mw.setMailContent(text);

		return null;
	}

	/**
	 * Get the complete class name (packages+name+extension) from the editor
	 * passed as input
	 * 
	 * @param editor
	 *            the editor
	 * @return the class name as String
	 */
	private String getCompleteClassName() {	
		IJavaElement el=EditorUtility.getActiveEditorJavaInput();
		return el.getParent().getElementName()+"."+ el.getElementName();
	}

	/**
	 * Get the selected text form the editor passes ad input
	 * 
	 * @param editor
	 *            the editor
	 * @return the text as String
	 */
	private String getSelectedText(IEditorPart editor) {

		if (editor instanceof AbstractTextEditor) {
			IEditorSite iEditorSite = editor.getEditorSite();
			if (iEditorSite != null) {
				ISelectionProvider selectionProvider = iEditorSite.getSelectionProvider();
				if (selectionProvider != null) {
					ISelection iSelection = selectionProvider.getSelection();
					if (!iSelection.isEmpty()) {
						String selectedText = ((ITextSelection) iSelection).getText();
						System.out.println(selectedText);
						return selectedText;
					}
				}
			}
		}
		return "";
	}

}
