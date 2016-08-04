/**
 * 
 */
package org.eclipse.remail.modules;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Locale;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.remail.Mail;

/**
 * Implementation of the lightweight search methods using POSTGRESQL
 * 
 * @author V. Humpa
 */
public class PostgreCore
{
    public Connection conn = null;
    LinkedList<Mail> mailList;

    /**
     * Initiates the driver and sets up the connection to the database.
     * 
     * @param conn_string
     * @param login
     * @param password
     */
    public PostgreCore(String conn_string, String login, String password)
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            MessageDialog.openError(null, "Error", "SQL error: could not initialize Postgre Driver " + e.getMessage());
            e.printStackTrace();
        }
        try
        {
            conn = DriverManager.getConnection(conn_string, login, password);
        } catch (SQLException e)
        {
            MessageDialog.openError(null, "Error", "SQL error: could not connect to the DB" + e.getMessage());
            System.out.println(e.getErrorCode());
            e.printStackTrace();
        }
        mailList = new LinkedList<Mail>();
    }

    public LinkedList<Mail> caseInsensitiveSearch(String classname) throws SQLException, IOException
    {
        Statement stmt = conn.createStatement();
        String classname_orig = classname;
        classname = classname.toLowerCase(Locale.ENGLISH);
        ResultSet rs = stmt.executeQuery("select * from mail where lower(rawcontent) like '%" + classname
                + "%' order by timestamp");
        mailList.clear();
        while (rs.next())
            mailList.add(new Mail(rs.getString("id"), rs.getString("subject"), rs.getDate("timestamp"), classname_orig));
        conn.close();
        return mailList;
    }

    public LinkedList<Mail> caseSensitiveSearch(String classname) throws SQLException, IOException
    {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from mail where rawcontent like '%" + classname
                + "%' order by timestamp");
        mailList.clear();
        while (rs.next())
            mailList.add(new Mail(rs.getString("id"), rs.getString("subject"), rs.getDate("timestamp"), classname));
        conn.close();
        return mailList;
    }

    public LinkedList<Mail> StrictRegexpSearch(String restOfPackage, String packageLastPart, String classname)
            throws SQLException, IOException
    {
        Statement stmt = conn.createStatement();

        System.out.println("select * from mail where rawcontent ~* '(\\s*)(" + restOfPackage + ")?(\\.|\\\\|/|\\s)"
                + packageLastPart + "(\\.|\\\\|/)" + classname + "(\\.java|\\.class|\\s+)' order by timestamp");

        ResultSet rs = stmt.executeQuery("select * from mail where rawcontent ~* '(\\s*)(" + restOfPackage
                + ")?(\\.|\\\\|/|\\s)" + packageLastPart + "(\\.|\\\\|/)" + classname
                + "(\\.java|\\.class|\\s+)' order by timestamp");
        mailList.clear();
        while (rs.next())
            mailList.add(new Mail(rs.getString("id"), rs.getString("subject"), rs.getDate("timestamp"), classname));
        conn.close();
        return mailList;
    }

    public LinkedList<Mail> LooseRegexpSearch(String entirePackage, String classname) throws SQLException, IOException
    {
        Statement stmt = conn.createStatement();

        System.out.println("select * from mail where rawcontent ~ '(\\s*)(" + entirePackage + ")?(\\.|\\\\|/|\\s)"
                + classname + "(\\.java|\\.class|\\s+|\"|,)' order by timestamp");

        ResultSet rs = stmt.executeQuery("select * from mail where rawcontent ~ '(\\s*)(" + entirePackage
                + ")?(\\.|\\\\|/|\\s)" + classname + "(\\.java|\\.class|\\s+|\"|,)' order by timestamp");
        mailList.clear();
        while (rs.next())
            mailList.add(new Mail(rs.getString("id"), rs.getString("subject"), rs.getDate("timestamp"), classname));
        conn.close();
        return mailList;
    }

    public String getMailTextFromDB(int id) throws SQLException
    {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select rawcontent from mail where id = " + id);
        rs.next();
        String text = rs.getString("rawcontent");
        conn.close();
        return text;
    }

    public boolean isInDictionary(String word) throws SQLException
    {
        Statement stmt = conn.createStatement();
        word = word.toLowerCase(Locale.ENGLISH);
        ResultSet rs = stmt.executeQuery("select word from dict where lower(word) = '" + word + "'");
        boolean isWord = rs.next();
        return isWord;
    }

}
