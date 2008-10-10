// $Id$
package org.uccreator.content.compare;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.uccreator.BaseTest;
import org.uccreator.content.ProcessingException;
import org.uccreator.content.compare.AnnotationDiff;
import org.uccreator.content.compare.XmlUnitDiffer;
import org.uccreator.content.compare.AnnotationDiff.ChangeType;
import org.w3c.dom.Node;


/**
 * Tests {@link XmlUnitDiffer}
 * 
 * @author Kariem Hussein
 */
public class XmlUnitDifferTest extends BaseTest {

	@Autowired
	XmlUnitDiffer differ;

	/**
	 * Test method for {@link XmlUnitDiffer#process(String[])} with equal
	 * content for differencing.
	 * 
	 * @throws ProcessingException
	 */
	@Test
	public final void testProcessNoDifference() throws ProcessingException {
		String text1 = readFromResource("usecases-3.xml");

		List<AnnotationDiff> diffs = diff(text1, text1);
		assertNotNull(diffs);
		assertTrue(diffs.isEmpty());
	}

	/**
	 * Test method for {@link XmlUnitDiffer#process(String[])}.
	 * 
	 * @throws ProcessingException
	 */
	@Test
	public final void testProcess() throws ProcessingException {
		String text1 = readFromResource("usecases-3.xml");
		String text2 = readFromResource("usecases-7.xml");

		List<AnnotationDiff> diffs = diff(text1, text2);
		assertEquals(1, diffs.size());
		AnnotationDiff d = diffs.get(0);

		// one section was added
		assertNotNull(d.getNode());
		assertTrue(d.getChangeType() == ChangeType.ADDED);
		assertEquals("/use-cases[1]/section[2]/uc[2]", d.getXpathLocation());
	}

	/**
	 * Test method for {@link XmlUnitDiffer#process(String[])}.
	 * 
	 * @throws ProcessingException
	 */
	@Test
	public final void testProcessChangeInPostAndAdditionalUc()
			throws ProcessingException {
		String text1 = readFromResource("usecases-7.xml");
		String text2 = readFromResource("usecases-8.xml");

		List<AnnotationDiff> diffs = diff(text1, text2);
		assertEquals(4, diffs.size());

		// text in post in section[2]/uc[2] was adapted
		AnnotationDiff d = diffs.get(0);
		assertEquals("/use-cases[1]/section[2]/uc[2]/post[1]/text()[1]", d
				.getXpathLocation());
		assertEquals(ChangeType.ADAPTED, d.getChangeType());

		// list elements li[1] and li[2] were adapted
		for (int i = 1; i <= 2; i++) {
			d = diffs.get(i);
			assertEquals("/use-cases[1]/section[2]/uc[2]/post[1]/ul[1]/li[" + i
					+ "]", d.getXpathLocation());
			assertEquals(ChangeType.ADAPTED, d.getChangeType());
		}

		// use case was added in section[2]
		d = diffs.get(3);
		assertEquals("/use-cases[1]/section[2]/uc[3]", d.getXpathLocation());
		assertEquals(ChangeType.ADDED, d.getChangeType());
	}

	/**
	 * Test method for {@link XmlUnitDiffer#process(String[])}.
	 * 
	 * @throws ProcessingException
	 */
	@Test
	public final void testNewSectionAndNewAttribute() throws ProcessingException {
		String text1 = readFromResource("uc-viewer-9.xml");
		String text2 = readFromResource("uc-viewer-10.xml");

		List<AnnotationDiff> diffs = diff(text1, text2);
		assertEquals(2, diffs.size());

		AnnotationDiff d = diffs.get(0);
		assertEquals("/use-cases[1]/section[1]/uc[1]", d.getXpathLocation());
		assertEquals(ChangeType.CONTENT, d.getChangeType());
		assertNotNull(d.getNode());
		
		d = diffs.get(1);
		assertEquals("/use-cases[1]/section[2]", d.getXpathLocation());
		assertEquals(ChangeType.ADDED, d.getChangeType());
		assertNotNull(d.getNode());
	}

	/**
	 * Test method for {@link XmlUnitDiffer#process(String[])} with a changed
	 * element inside a list.
	 * 
	 * @throws ProcessingException
	 */
	@Test
	public final void testChangedListElement() throws ProcessingException {
		String text1 = "<ul><li>1</li><li>2</li></ul>";
		String text2 = "<ul><li>0</li><li>2</li></ul>";

		List<AnnotationDiff> diffs = diff(text1, text2);
		assertEquals(1, diffs.size());
		AnnotationDiff d = diffs.get(0);

		// first li element was adapted
		assertEquals("/ul[1]/li[1]/text()[1]", d.getXpathLocation());
		assertTrue(d.getChangeType() == ChangeType.ADAPTED);
		Node n = d.getNode();
		assertNotNull(n);
		assertEquals("0", n.getNodeValue());
	}

	/**
	 * Test method for {@link XmlUnitDiffer#process(String[])} with several
	 * changed element inside a list.
	 * 
	 * @throws ProcessingException
	 */
	@Test
	public final void testChangedListElements() throws ProcessingException {
		String text1 = "<ul><li>1</li><li>2</li><li>3</li></ul>";
		String text2 = "<ul><li>a</li><li>a</li><li>3</li></ul>";

		List<AnnotationDiff> diffs = diff(text1, text2);
		assertEquals(2, diffs.size());

		AnnotationDiff d = diffs.get(0);
		assertEquals("/ul[1]/li[1]/text()[1]", d.getXpathLocation());
		assertTrue(d.getChangeType() == ChangeType.ADAPTED);
		assertEquals("a", d.getNode().getNodeValue());

		d = diffs.get(1);
		assertEquals("/ul[1]/li[2]/text()[1]", d.getXpathLocation());
		assertTrue(d.getChangeType() == ChangeType.ADAPTED);
		assertEquals("a", d.getNode().getNodeValue());
	}

	/**
	 * Test method for {@link XmlUnitDiffer#process(String[])} with several
	 * changed element inside a list.
	 * 
	 * @throws ProcessingException
	 */
	@Test
	public final void testTextNodeWhitspace() throws ProcessingException {
		String text1 = "<ul><li>\n\t1\n</li></ul>";
		String text2 = "<ul><li>1</li></ul>";

		List<AnnotationDiff> diffs = diff(text1, text2);
		assertEquals(0, diffs.size());
	}

	private List<AnnotationDiff> diff(String... comparison)
			throws ProcessingException {
		return differ.process(comparison);
	}

}
