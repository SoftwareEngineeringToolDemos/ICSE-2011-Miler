package test.org.eclipse.remail.emails;

import static org.junit.Assert.*;

import org.eclipse.remail.emails.EmailSender;
import org.junit.Before;
import org.junit.Test;

public class EmailSenderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSplitMailAddress() {
		String single="ciao@ciao.com";
		String more="ciao@ciao.com, foo@bar.org, mailme@myprovider.ch";
		
		String[] singleArr = EmailSender.splitMailAddress(single);
		String[] shouldBe=new String[]{"ciao@ciao.com"};
		assertArrayEquals(shouldBe, singleArr);
		String[] moreArr=EmailSender.splitMailAddress(more);
		shouldBe=new String[]{"ciao@ciao.com", "foo@bar.org", "mailme@myprovider.ch"};
		assertArrayEquals(shouldBe, moreArr);
	}

}
