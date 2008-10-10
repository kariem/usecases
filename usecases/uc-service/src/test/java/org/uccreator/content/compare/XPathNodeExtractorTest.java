// $Id$
package org.uccreator.content.compare;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.uccreator.BaseTest;
import org.uccreator.content.ProcessingException;
import org.uccreator.content.compare.XPathNodeExtractor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * Tests {@link XPathNodeExtractor}
 * 
 * @author Kariem Hussein
 */
public class XPathNodeExtractorTest extends BaseTest {

	@Autowired
	XPathNodeExtractor extractor;

	/**
	 * Test method for {@link XPathNodeExtractor#getElement(Document, String)}.
	 * 
	 * @throws ProcessingException
	 */
	@Test
	public final void testGetElement() throws ProcessingException {
		String xpath = "/use-cases[1]/section[2]/uc[2]/post[1]/ul[1]/li[1]";
		Document ownerDocument = readDocumentFromResource("usecases-7.xml");
		assertNotNull(extractor.getElement(ownerDocument, xpath));

		xpath = "/use-cases[1]/section[2]/uc[2]/post[1]/ul[1]/li[2]";
		ownerDocument = readDocumentFromResource("usecases-8.xml");
		assertNotNull(extractor.getElement(ownerDocument, xpath));
	}

	/**
	 * Test method for {@link XPathNodeExtractor#xpathUp(String, int)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testXpathUp() throws Exception {
		String xpath = "/use-cases[1]/section[2]/text()[4]";
		assertEquals("/use-cases[1]/section[2]", extractor.xpathUp(xpath, 1));
		assertEquals("/use-cases[1]", extractor.xpathUp(xpath, 2));
		assertEquals("/", extractor.xpathUp(xpath, 3));
		assertEquals("/", extractor.xpathUp(xpath, 4));
	}

	/**
	 * Test method for {@link XPathNodeExtractor#calculate(org.w3c.dom.Node)}.
	 * 
	 * @throws ProcessingException
	 */
	@Test
	public final void testCalculate() throws ProcessingException {
		Document ownerDocument = readDocumentFromResource("usecases-7.xml");
		
		String xpath = "/use-cases[1]/section[2]/uc[2]/post[1]/ul[1]/li[1]";
		Node n = extractor.getElement(ownerDocument, xpath);
		assertEquals("li[1]", extractor.calculate(n));
		
		xpath = "/use-cases[1]/section[2]/uc[2]/post[1]/ul[1]/li[2]";
		n = extractor.getElement(ownerDocument, xpath);
		assertEquals("li[2]", extractor.calculate(n));
		
		xpath = "/use-cases[1]/section[2]/uc[2]";
		n = extractor.getElement(ownerDocument, xpath);
		assertEquals("uc[2]", extractor.calculate(n));
		
		xpath = "/use-cases[1]/section[1]/uc[1]/flow[1]/step[4]";
		n = extractor.getElement(ownerDocument, xpath);
		assertEquals("step[4]", extractor.calculate(n));
		
		xpath = "/use-cases[1]/text()[3]";
		n = extractor.getNode(ownerDocument, xpath);
		assertEquals("text()[3]", extractor.calculate(n));
	}
}
