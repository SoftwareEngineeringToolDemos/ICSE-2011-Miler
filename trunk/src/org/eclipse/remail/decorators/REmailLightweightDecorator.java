package org.eclipse.remail.decorators;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.remail.Search;

public class REmailLightweightDecorator implements ILightweightLabelDecorator
{

	@Override
	public void decorate(Object resource, IDecoration decoration)
	{
		// TODO Auto-generated method stub
		IResource res = (IResource) resource;
		String name = res.getName();
		if (name.contains(".java"))
		{
			Search search = new Search();
			int count = search.Execute(name, res.getFullPath().toString(), true).size();
			decoration.addSuffix(" (" + count + ")");
		}
	}

	@Override
	public void addListener(ILabelProviderListener arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0)
	{
		// TODO Auto-generated method stub

	}

}
