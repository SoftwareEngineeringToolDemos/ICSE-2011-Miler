package org.eclipse.remail;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.remail.util.SQLiteMailListConstructor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.*;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

/**
 * Implementation of the view with the results of the email search.
 */
public class MailView extends ViewPart
{

	public static final String ID = "org.eclipse.emailrecommender.MailView";
	public MailContentView mailContentView = null; // instance of the content
	// view

	private static TreeViewer viewer;
	private Action selectionChangedAction;
	private Action doubleClickAction;

	// TODO
	class NameSorter extends ViewerSorter
	{
	}

	/**
	 * The constructor.
	 */
	public MailView()
	{
	}

	public class MailTreeContentProvider extends ArrayContentProvider implements
			ITreeContentProvider
	{
		@SuppressWarnings("unchecked")
		@Override
		public Object[] getElements(Object inputElement)
		{
			// TODO Auto-generated method stub
			LinkedList<Mail> mailList = (LinkedList<Mail>) inputElement;
			LinkedList<Mail> topLevelMails = new LinkedList<Mail>();
			for (Mail mail : mailList)
			{
				if (hasChildren(mail) || getParent(mail) == null)
					topLevelMails.add(mail);
			}
			Collections.sort(topLevelMails);
			return topLevelMails.toArray();
		}

		public Object[] getChildren(Object parentElement)
		{
			Mail mail = (Mail) parentElement;
			LinkedList<Mail> mailList = (LinkedList<Mail>) viewer.getInput();
			// System.out.println("|getChildren|");

			LinkedList<Mail> children = new LinkedList<Mail>();

			for (Mail m : mailList)
			{
				if (!mail.getPermalink().startsWith(m.getPermalink())
						&& m.getThreadlink().startsWith(mail.getThreadlink()))
					children.add(m);
			}
			Collections.sort(children);
			return children.toArray();
			// return mail.children;
		}

		public Object getParent(Object element)
		{
			Mail mail = (Mail) element;
			LinkedList<Mail> mailList = (LinkedList<Mail>) viewer.getInput();
			// System.out.println("|getParent|");
			LinkedList<Mail> threadMails = new LinkedList<Mail>();
			for (Mail m : mailList)
			{
				if ((m.getThreadlink().startsWith(mail.getThreadlink())))
					threadMails.add(m);
			}
			if (threadMails.size() < 2)
			{
				// System.out.println("|WTFhasChildren-false|");
				return null;
			}
			Mail parent = threadMails.get(0);
			for (Mail m : threadMails)
			{
				if (m.getTimestamp().before(parent.getTimestamp()))
					parent = m;
			}

			if (parent == mail)
				return null;
			else
				return parent;
		}

		public boolean hasChildren(Object element)
		{
			Mail mail = (Mail) element;
			LinkedList<Mail> mailList = (LinkedList<Mail>) viewer.getInput();
			// .println("|hasChildren|");

			LinkedList<Mail> threadMails = new LinkedList<Mail>();
			for (Mail m : mailList)
			{
				if ((m.getThreadlink().startsWith(mail.getThreadlink())))
					threadMails.add(m);
			}
			if (threadMails.size() < 2)
			{
				// System.out.println("|WTFhasChildren-false|");
				return false;
			}
			Mail parent = threadMails.get(0);
			for (Mail m : threadMails)
			{
				if (m.getTimestamp().before(parent.getTimestamp()))
					parent = m;
			}
			if (parent == mail)
			{
				// System.out.println("|hasChildren-true|");
				return true;
			} else
			{
				// System.out.println("|hasChildren-fallse|");
				return false;
			}

			// return true;
		}
	}

	public class MailTreeLabelProvider extends LabelProvider implements
			ILabelProvider
	{
		@Override
		public String getText(Object element)
		{
			SimpleDateFormat df = new SimpleDateFormat("dd.MM. yyyy HH:mm");
			Mail mail = (Mail) element;
			// String author = mail.getAuthor().split("(")[0];
			String author = mail.getAuthor().replaceAll(
					"^(.+)\\s*\\(.*@.*\\).*$", "$1");
			return df.format(mail.getTimestamp()) + " " + author + ": "
					+ mail.getSubject();
		}
	}

	private ISelectionListener listener = new ISelectionListener()
	{
		public void selectionChanged(IWorkbenchPart sourcepart,
				ISelection selection)
		{
			// we ignore our own selections
			if (sourcepart != MailView.this)
			{
				processSelection(sourcepart, selection);
			}
		}
	};

	public void processSelection(IWorkbenchPart sourcepart, ISelection selection)
	{
		// System.out.println("SELECTION ");
		// setContentDescription(sourcepart.getTitle() + " ("
		//	+ selection.getClass().getName() + ")");
		//System.out.println(selection.getClass().getName());
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection ss = (IStructuredSelection) selection;
			System.out.println(" Str");
			System.out.println(ss.getClass());
			if (ss.getFirstElement() instanceof ICompilationUnit)
				System.out.println("heyyyyyyyy");
				this.loadFromCache((ICompilationUnit) ss.getFirstElement());
		}
	}

	private void loadFromCache(ICompilationUnit compilationUnit)
	{
		String name = compilationUnit.getResource().getName();
		name = name.split("\\.")[0];
		SQLiteMailListConstructor mailListConstructor = new SQLiteMailListConstructor(compilationUnit.getResource());
		LinkedList<Mail> mailList = new LinkedList<Mail>();
		try
		{
			mailList = mailListConstructor.getResultMailList(name);
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Search.updateMailView(mailList);
		//MailView.getViewer().setInput(mailList);
	}

	/**
	 * This is a callback that will allows to create the view controls
	 */
	public void createPartControl(Composite parent)
	{
		viewer = new TreeViewer(parent, SWT.SINGLE /*
													 * | SWT.FULL_SELECTION |
													 * SWT.H_SCROLL |
													 * SWT.V_SCROLL
													 */);
		// viewer.setContentProvider(new ArrayContentProvider());
		// final Table table = viewer.getTable();
		// table.setHeaderVisible(true);
		// table.setLinesVisible(true);
		// String[] columnNames = new String[] { "Date", "Subject" };
		// int[] columnWidths = new int[] { 150, 300 };
		// int[] columnAlignments = new int[] { SWT.LEFT, SWT.LEFT };
		// for (int i = 0; i < columnNames.length; i++)
		// {
		// TableColumn tableColumn = new TableColumn(table,
		// columnAlignments[i]);
		// tableColumn.setText(columnNames[i]);
		// tableColumn.setWidth(columnWidths[i]);
		// }

		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(listener);
		viewer.setContentProvider(new MailTreeContentProvider());
		viewer.setLabelProvider(new MailTreeLabelProvider());
		makeActions();
		hookActions();
	}

	/**
	 * Creates the action to be done on the doubleclick
	 */
	private void makeActions()
	{
		selectionChangedAction = new Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				if (!selection.isEmpty())
				{
					Mail mail = (Mail) ((IStructuredSelection) selection)
							.getFirstElement();
					try
					{
						PlatformUI
								.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage()
								.showView(
										"org.eclipse.emailrecommender.MailContentView");
					} catch (PartInitException e)
					{
						MessageDialog.openError(null, "Error",
								"Error in showing the view: " + e.getMessage());
						e.printStackTrace();
					}
					MailContentView.setMail(mail);
				}
			}
		};

		doubleClickAction = new Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Mail mail = (Mail) ((IStructuredSelection) selection)
						.getFirstElement();
				// System.out.println(" dude ");
				if (!viewer.getExpandedState(mail))
					viewer.expandToLevel(mail, 1);
				else
					viewer.collapseToLevel(mail, 1);
			}
		};
	}

	private void hookActions()
	{
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				selectionChangedAction.run();
			}
		});

		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				doubleClickAction.run();
			}
		});
	}

	public static TreeViewer getViewer()
	{
		return viewer;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}
}