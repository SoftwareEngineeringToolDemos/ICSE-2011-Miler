package org.eclipse.remail;

import org.eclipse.remail.daemons.ChangeViewDaemon;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.emailrecommender";
	
	// Variables for couchdb settings
	public static final String COUCHDB_HOST = "localhost";
	public static final String COUCHDB_PORT = "5984";
	public static final String COUCHDB_USER = "username";
	public static final String COUCHDB_PASSWORD = "password";
	
	//Variable for the SMTP account
	public static final String ACCOUNTS_SMTP = "accounts";
	public static final String DEFAULT_ACCOUNTS_SMTP = "accounts";


	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator()
	{
		/*
		 * Creates the thread responsible for updating the views
		 * depending on the active class editor
		 */
		Thread gene = new Thread(new ChangeViewDaemon());
		gene.setDaemon(true);
		gene.setPriority(3); //Priorities range: 1-10 default 5
		gene.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return plugin;
	}

	public static String getHost() {
		return plugin.getPreferenceStore().getString(COUCHDB_HOST);
	}
	
	public static String getPort() {
		return plugin.getPreferenceStore().getString(COUCHDB_PORT);
	}
	
	public static String getUsername() {
		return plugin.getPreferenceStore().getString(COUCHDB_USER);
	}
	
	public static String getPassword() {
		return plugin.getPreferenceStore().getString(COUCHDB_PASSWORD);
	}
	
	public static String getAccounts() {
		return plugin.getPreferenceStore().getString(ACCOUNTS_SMTP);
	}
}
