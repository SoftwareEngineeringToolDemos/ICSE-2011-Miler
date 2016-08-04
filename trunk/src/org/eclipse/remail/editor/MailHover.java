package org.eclipse.remail.editor;

import org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

/**
 * Part of a future plan to provide REmail extension the the "javadoc"
 * hovers
 * @author V. HUmpa
 *
 */
public class MailHover implements IJavaEditorTextHover
{

	@Override
	public void setEditor(IEditorPart editor)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
