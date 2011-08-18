package org.eclipse.remail.views;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.remail.Mail;
import org.eclipse.remail.Search;
import org.eclipse.remail.util.CacheCouchDB;
import org.eclipse.remail.util.LocalMailListSearch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Implementation of the view with the results of the email search.
 */
public class MailView extends ViewPart {

	public static final String ID = "org.eclipse.emailrecommender.MailView";
	public MailContentView mailContentView = null; // instance of the content
	private static TreeViewer viewer;
	private Action selectionChangedAction;
	private Action doubleClickAction;
	private IResource activeResource;

	private static Text searchBox;
	private static GridLayout layout;
	private static Button searchButton;

	/**
	 * The constructor.
	 */
	public MailView() {

	}

	public class MailTreeContentProvider extends ArrayContentProvider implements
			ITreeContentProvider {
		@SuppressWarnings("unchecked")
		@Override
		public Object[] getElements(Object inputElement) {
//			return super.getElements(inputElement);
			LinkedList<Mail> mailList = (LinkedList<Mail>) inputElement;
			LinkedList<Mail> topLevelMails = new LinkedList<Mail>();
			for (Mail mail : mailList) {
				if ((hasChildren(mail) || getParent(mail) == null) && mail != null)
					topLevelMails.add(mail);
			}
			Collections.sort(topLevelMails);
			return topLevelMails.toArray();
		}

		public Object[] getChildren(Object parentElement) {
			Mail mail = (Mail) parentElement;
			LinkedList<Mail> mailList = (LinkedList<Mail>) viewer.getInput();

			LinkedList<Mail> children = new LinkedList<Mail>();
			System.out.println("looking for child");
			for (Mail m : mailList) {
//				System.out.println("  -- "+ mail.getThreadlink()+"  "+m.getThreadlink());
				try{
				if(m.getThreadlink().equals(mail.getThreadlink()) && (!mail.getTimestamp().equals(m.getTimestamp()))){
//				if ((mail != null && mail.getPermalink() != null && !mail.getPermalink()
//						.startsWith(m.getPermalink()))
//						&& (m != null && m.getThreadlink() != null && m.getThreadlink().startsWith(
//								mail.getThreadlink()))) {
					children.add(m);
					System.out.println(m.toString());
				}
//				else
//					System.err.println(m.getThreadlink()+" "+mail.getThreadlink()+" - "+mail.getPermalink()+" "+m.getPermalink());
				}catch (NullPointerException e) {
					// nothing to do
				}
			}
			Collections.sort(children);
			return children.toArray();
		}

		public Object getParent(Object element) {
			Mail mail = (Mail) element;
			LinkedList<Mail> mailList = (LinkedList<Mail>) viewer.getInput();
			LinkedList<Mail> threadMails = new LinkedList<Mail>();
			for (Mail m : mailList) {
				if (mail != null && m != null && m.getThreadlink() != null
						&& mail.getThreadlink() != null
						&& (m.getThreadlink().startsWith(mail.getThreadlink())))
					threadMails.add(m);
			}
			if (threadMails.size() < 2) {
				return null;
			}
			Mail parent = threadMails.get(0);
			for (Mail m : threadMails) {
				if (m.getTimestamp().before(parent.getTimestamp()))
					parent = m;
			}

			if (parent == mail)
				return null;
			else
				return parent;
		}

		public boolean hasChildren(Object element) {
			Mail mail = (Mail) element;
			LinkedList<Mail> mailList = (LinkedList<Mail>) viewer.getInput();
			LinkedList<Mail> threadMails = new LinkedList<Mail>();
			for (Mail m : mailList) {
				if (mail != null && m != null && m.getThreadlink() != null
						&& mail.getThreadlink() != null
						&& (m.getThreadlink().startsWith(mail.getThreadlink())))
					threadMails.add(m);
			}
			if (threadMails.size() < 2) {
				return false;
			}
			Mail parent = threadMails.get(0);
			for (Mail m : threadMails) {
				if (m.getTimestamp().before(parent.getTimestamp()))
					parent = m;
			}
			if (parent == mail) {
				return true;
			} else {
				return false;
			}

			// return true;
		}
	}

	public class MailTreeLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			SimpleDateFormat df = new SimpleDateFormat("dd.MM. yyyy HH:mm");
			Mail mail = (Mail) element;
			try {
				String author = mail.getAuthor().replaceAll("^(.+)\\s*\\(.*@.*\\).*$", "$1");
				switch (columnIndex) {
					case 0:
						return df.format(mail.getTimestamp());

					case 1:
						return author;

					case 2:
						return mail.getSubject();
				}
			} catch (NullPointerException e) {
				// if there is a mail with something null, we throw it away ;)
				System.err.println("Null mail:" + mail.toString());
				if (mail == null)
					System.err.println("The mail is null!");
			}

			return null;
		}
	}

	// public class CheckStateProvider implements ICheckStateProvider {
	//
	// @Override
	// public boolean isChecked(Object element) {
	// MailStateChecker mailStateChecker = new MailStateChecker((Mail) element,
	// activeResource);
	// return mailStateChecker.isVisible();
	// }
	//
	// @Override
	// public boolean isGrayed(Object element) {
	// return false;
	// }
	// }
	//
	// public class CheckStateListener implements ICheckStateListener {
	// @Override
	// public void checkStateChanged(CheckStateChangedEvent event) {
	// boolean checked = event.getChecked();
	// Mail mail = (Mail) event.getElement();
	// MailStateChecker mailStateChecker = new MailStateChecker(mail,
	// activeResource);
	// mailStateChecker.changeState(checked);
	// }
	// }

	/**
	 * This is a callback that will allows to create the view controls
	 */
	public void createPartControl(Composite parent) {
		// viewer = new ContainerCheckedTreeViewer(parent, SWT.DEFAULT);
		viewer = new TreeViewer(parent, SWT.MULTI);

		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);

		String[] columnNames = new String[] { "Date", "Author", "Subject" };
		int[] columnWidths = new int[] { 140, 150, 500 };
		int[] columnAlignments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT };
		for (int i = 0; i < columnNames.length; i++) {
			TreeColumn treeColumn = new TreeColumn(tree, columnAlignments[i]);
			treeColumn.setText(columnNames[i]);
			treeColumn.setWidth(columnWidths[i]);
		}

		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
		viewer.setContentProvider(new MailTreeContentProvider());
		viewer.setLabelProvider(new MailTreeLabelProvider());
		viewer.expandAll();
		// viewer.setCheckStateProvider(new CheckStateProvider());
		// viewer.addCheckStateListener(new CheckStateListener());

		makeActions();
		hookActions();

		// set the view layout
		layout = new GridLayout();
		parent.setLayout(layout);
		Composite inner = new Composite(parent, SWT.None);
		Layout layout2 = new GridLayout(2, false);
		inner.setLayout(layout2);

		// widgets for the view
		searchButton = new Button(inner, SWT.PUSH);
		searchButton.setText("Search");
		searchBox = new Text(inner, SWT.SINGLE | SWT.FOCUSED);

		/*
		 * For the mail's Tree view, tells to occupy all the space left from the
		 * other controls
		 */
		GridData gd1 = new GridData();
		gd1.horizontalAlignment = GridData.FILL;
		gd1.grabExcessHorizontalSpace = true;
		gd1.verticalAlignment = GridData.FILL;
		gd1.grabExcessVerticalSpace = true;
		tree.setLayoutData(gd1);

		/*
		 * The text search occupy the space in the "row" left from the button
		 */
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		gd2.grabExcessHorizontalSpace = true;
		inner.setLayoutData(gd2);
		searchBox.setLayoutData(gd2);

		/*
		 * Tells the button to stay at left and to not grow/shrink depending on
		 * the size of the view
		 */
		GridData gd3 = new GridData();
		gd3.grabExcessHorizontalSpace = false;
		searchButton.setLayoutData(gd3);

		/*
		 * Add the control on button press!
		 */
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String toSearch = searchBox.getText();
				System.out.println("searching for : " + toSearch);
				LocalMailListSearch lmls = new LocalMailListSearch(viewer, toSearch);
				lmls.search();
			}
		});
	}

	/**
	 * Creates the action to be done on the doubleclick
	 */
	private void makeActions() {
		selectionChangedAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				if (!selection.isEmpty()) {
					Mail mail = (Mail) ((IStructuredSelection) selection).getFirstElement();
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.showView("org.eclipse.emailrecommender.MailContentView");
					} catch (PartInitException e) {
						MessageDialog.openError(null, "Error",
								"Error in showing the view: " + e.getMessage());
						e.printStackTrace();
					}
					MailContentView mw = new MailContentView();
					mw.setMail(mail);
				}
			}
		};

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Mail mail = (Mail) ((IStructuredSelection) selection).getFirstElement();
				if (!viewer.getExpandedState(mail))
					viewer.expandToLevel(mail, 1);
				else
					viewer.collapseToLevel(mail, 1);
			}
		};
	}

	private void hookActions() {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectionChangedAction.run();
			}
		});

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});

	}

	private ISelectionListener listener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			// we ignore our own selections
			if (sourcepart != MailView.this) {
				processSelection(sourcepart, selection);
			}
		}
	};

	public void processSelection(IWorkbenchPart sourcepart, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.getFirstElement() instanceof ICompilationUnit)
				this.activeResource = ((ICompilationUnit) ss.getFirstElement()).getResource();
			this.loadFromCache((ICompilationUnit) ss.getFirstElement());
		}
	}

	private void loadFromCache(ICompilationUnit compilationUnit) {
		if (compilationUnit != null) {
			IResource res = compilationUnit.getResource();
			String name = res.getName();
			String path = res.getLocation().toString();
			name = name.split("\\.")[0];
			if (CacheCouchDB.containsClass(name, path)) {
				Search search = new Search();
				IPath fullPath = res.getProjectRelativePath();
				// System.out.println(fullPath.toString());
				LinkedList<Mail> mailList = search.Execute(name, fullPath.toString(), true);
				if (mailList == null)
					Search.updateMailView(new LinkedList<Mail>());
				else
					Search.updateMailView(mailList);
				System.out.println("|" + mailList.size() + "|");
			}
		}

		// System.out.println(name+" "+mailList);
		/*
		 * Uncomment to to use the cache
		 */
		// SQLiteMailListConstructor mailListConstructor = new
		// SQLiteMailListConstructor(
		// this.activeResource);
		// LinkedList<Mail> mailList = new LinkedList<Mail>();
		// try
		// {
		// if ((mailList = mailListConstructor.getResultMailList()) == null)
		// Search.updateMailView(new LinkedList<Mail>());
		// else
		// Search.updateMailView(mailList);
		// System.out.println("|" + mailList.size() + "|");
		// } catch (SQLException e)
		// {
		// e.printStackTrace();
		// } catch (ClassNotFoundException e)
		// {
		// e.printStackTrace();
		// }
	}

	public static TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}