package test.org.eclipse.remail;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.remail.Mail;
import org.junit.Before;
import org.junit.Test;

public class MailTest {

	Mail m1;
	Mail m2;
	Mail m3;
	Mail m4;
	LinkedList<Mail> list1;
	LinkedList<Mail> list2;

	@Before
	public void setUp() throws Exception {
		m1 = new Mail(0, "test mail 1", new Date(2011, 7, 13), "HelloWorld");
		m2 = new Mail(0, "test mail 2", new Date(2011, 2, 13), "HelloWorld");
		m3 = new Mail(0, "test mail 3", new Date(2011, 7, 10), "HelloWorld");
		m4 = new Mail(0, "test mail 4", new Date(2011, 8, 3), "HelloWorld");

		list1 = new LinkedList<Mail>();
		list1.add(m4);
		list1.add(m2);
		list1.add(m1);

		list2 = new LinkedList<Mail>();
		list2.add(m1);
		list2.add(m3);
	}

	@Test
	public void testListSort() throws Exception {
		setUp();
		Collections.sort(list1);

		assertTrue(list1.get(0) == m2);
		assertTrue(list1.get(1) == m1);
		assertTrue(list1.get(2) == m4);
	}

	@Test
	public void testMergeSortMailListsWithEmptyList() throws Exception {
		setUp();

		LinkedList<Mail> merged = new LinkedList<Mail>();

		int numElem = list1.size();
		assertTrue(numElem != 0);

		merged = Mail.mergeSortMailLists(merged, list1);

		for (Mail m : list1)
			if (!merged.contains(m))
				fail(m.getSubject() + " in not in merged list");

		assertTrue(merged.size() == numElem);
	}

	@Test
	public void testMergeSortMailLists() throws Exception {
		setUp();

		LinkedList<Mail> merged = new LinkedList<Mail>();

		int numElem1 = list1.size();
		assertTrue(numElem1 != 0);
		int numElem2 = list2.size();
		assertTrue(numElem2 != 0);
		int numElem = (numElem1 < numElem2 ? numElem1 : numElem2);
		assertTrue(numElem == numElem2);

		merged = Mail.mergeSortMailLists(list1, list2);

		for (Mail m : list1)
			if (!merged.contains(m))
				fail(m.getSubject() + " in not in merged list");

		for (Mail m : list2)
			if (!merged.contains(m))
				fail(m.getSubject() + " in not in merged list");
				
		assertTrue(merged.size()>=numElem2);
		
		for(int i=0; i<merged.size(); i++){
			for(int j=i+1; j<merged.size(); j++)
				if(merged.get(i)==merged.get(j))
					fail(merged.get(i).getSubject()+" at "+i+" and "+merged.get(j).getSubject()+" at "+j+" are the same");
		}
	}
}
