package test.org.eclipse.remail.emails;

import static org.junit.Assert.*;

import org.eclipse.remail.emails.SMTPAccount;
import org.junit.Before;
import org.junit.Test;

public class TestSMTPAccount {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testToString() {
		SMTPAccount acc = new SMTPAccount("ciao@ciao.com", "hello", "hi", "mail.ciao.it", "465");
		String expected = "ciao@ciao.com : hello : hi : mail.ciao.it : 465";
		String s = acc.toString();
		assertEquals(expected, s);
	}

	@Test
	public void testFromString() {
		String s = "ciao@ciao.com : hello : hi : mail.ciao.it : 465";
		SMTPAccount expected = new SMTPAccount("ciao@ciao.com", "hello", "hi", "mail.ciao.it", "465");
		SMTPAccount acc = SMTPAccount.fromString(s);
		assertEquals(expected, acc);
	}

}
