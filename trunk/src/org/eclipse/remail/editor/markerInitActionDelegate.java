package org.eclipse.remail.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
// "prase BookMatch kokot" matches name
public class markerInitActionDelegate implements IEditorActionDelegate
{

	IEditorPart editor;
	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor)
	{
		editor = targetEditor;
	}

	@Override
	public void run(IAction action)
	{
		System.out.println(editor.getEditorInput().getName());
		IEditorInput input = editor.getEditorInput();
		IDocument document = (((ITextEditor)editor).getDocumentProvider()).getDocument(input);
		String text = document.get();
		System.out.println(text);
		String[] lines = text.split("\\n");
		//"SELECT name from classes";
		for (String line : lines)
		{
			
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
		// TODO Auto-generated method stub

	}

}
