package org.eclipse.remail.daemons;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Its a daemon process (it runs in background) which is started together with
 * the REmail plug-in. Its task is to set the mail view to the current class the
 * user is viewing/modifying
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class ChangeViewDaemon implements Runnable {

	IWorkbenchWindow window = null;

	@Override
	public void run() {
	
		System.out.println("daemon start");
		boolean tryGetWorkbench = true;

		// loop to try get the workbench of eclipse
		while (tryGetWorkbench) {
			try {
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow[] array = workbench.getWorkbenchWindows();
				if (array.length == 1) {
					window = array[0];
					if(window.getActivePage()!=null)
						tryGetWorkbench = false;
					// System.out.println("get workbench");
				}

			} catch (NullPointerException e) {
				/* wasn't able to get a significant object,
				 * thus sleep for a while and retry later
				 */
				try {
					// System.out.println("no workbench");
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		if (window != null) {
			window.getActivePage().addSelectionListener(new ISelectionListener() {

				@Override
				public void selectionChanged(IWorkbenchPart part, ISelection selection) {
					// System.out.println("changed selection");
					updateCurrentView();
				}
			});
		}
	}

	/**
	 * Update the mail list view if notified for a window change
	 */
	private void updateCurrentView() {
		//get the class name
		ITextEditor editor = (ITextEditor) window.getActivePage().getActiveEditor();
		String classname = editor.getTitle();
		classname = classname.split("\\.")[0];
		
		System.out.println("-- " + classname + " "
				+ editor.getEditorInput().getPersistable().toString());
				
		//get the class path
		String path = getPath(editor.getEditorInput().getPersistable().toString());
		System.out.println(path);

		//query the database and update the view
		
		QueryDatabase job = new QueryDatabase(classname, path);
		job.setPriority(Job.SHORT);
		job.schedule();

	}

	/**
	 * Get the file path given a string in the format:
	 * "org.eclipse.ui.part.FileName
	 * (/project/src/ProjectName/bla-bla/FileName.java)" and it will return:
	 * "src/ProjectName/bla-bla/FileName.java"
	 * 
	 * @param string
	 *            the string
	 * @return the path
	 */
	public static String getPath(String string) {
		String s = string.substring(string.indexOf("/src/") + 1, string.indexOf(")"));
		// hint: the +1 is the length of the "/" before "src/"
		return s;
	}

}
