package test.org.eclipse.remail.emails;

import static org.junit.Assert.*;

import org.eclipse.remail.emails.ListSMTPAccount;
import org.eclipse.remail.emails.SMTPAccount;
import org.junit.Before;
import org.junit.Test;

public class TestListSMTPAccount {

	SMTPAccount acc1= SMTPAccount.fromString("mailAddress1 : username1 : password1 : server1 : port1");
	SMTPAccount acc2= SMTPAccount.fromString("mailAddress2 : username2 : password2 : server2 : port2");
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testListSMTPAccount() {		
		assertEquals(new ListSMTPAccount(acc1,acc2), new ListSMTPAccount(new SMTPAccount[]{acc1, acc2}));
	}

	@Test
	public void testToString() {
		String expected= "mailAddress1 : username1 : password1 : server1 : port1 ; mailAddress2 : username2 : password2 : server2 : port2";
		ListSMTPAccount l = new ListSMTPAccount(acc1, acc2);
		assertEquals(expected, l.toString());
		
		expected ="mailAddress1 : username1 : password1 : server1 : port1";
		l = new ListSMTPAccount(acc1);
		assertEquals(expected, l.toString());
	}

	@Test
	public void testFromString() {
		String s= "mailAddress1 : username1 : password1 : server1 : port1 ; mailAddress2 : username2 : password2 : server2 : port2";
		ListSMTPAccount expected = new ListSMTPAccount(acc1, acc2);
		assertEquals(expected, ListSMTPAccount.fromString(s));
		
		s= "mailAddress1 : username1 : password1 : server1 : port1";
		expected = new ListSMTPAccount(acc1);
		assertEquals(expected, ListSMTPAccount.fromString(s));
	}

}
