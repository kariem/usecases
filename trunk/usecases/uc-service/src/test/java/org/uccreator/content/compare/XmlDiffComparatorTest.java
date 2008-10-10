// $Id$
package org.uccreator.content.compare;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.uccreator.BaseTest;
import org.uccreator.content.TestableDocumentInstance;
import org.uccreator.content.compare.DocumentComparison;
import org.uccreator.content.compare.XmlDiffComparator;
import org.uccreator.model.DocumentInstance;


/**
 * Tests {@link XmlDiffComparator}
 * 
 * @author Kariem Hussein
 */
public class XmlDiffComparatorTest extends BaseTest {

	@Autowired
	XmlDiffComparator comparator;

	/**
	 * Tests {@link XmlDiffComparator#process(DocumentComparison)} using equal
	 * documents as input.
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@Test
	public void testProcessNoDifference() throws Exception {
		DocumentInstance d1 = createDocument("usecases-3.xml");
		DocumentComparison comparison = new DocumentComparison(d1, d1);
		assertTrue(comparator.canProcess(comparison));

		DocumentInstance result = comparator.process(comparison);

		assertEquals(d1.getTextContents(), result.getTextContents());
	}

	/**
	 * Tests {@link XmlDiffComparator#process(DocumentComparison)} with a single
	 * use case added.
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@Test
	public void testProcessAddedUseCase() throws Exception {
		DocumentInstance d1 = createDocument("usecases-3.xml");
		DocumentInstance d2 = createDocument("usecases-7.xml");
		DocumentComparison comparison = new DocumentComparison(d1, d2);
		DocumentInstance result = comparator.process(comparison);

		assertTrue(result.isText());
		// check text of compared documents
		String text1 = d1.getTextContents();
		String text2 = d2.getTextContents();
		assertFalse(text1.contains("change=\"added\""));
		assertFalse(text2.contains("change=\"added\""));

		String resultingText = result.getTextContents();
		assertTrue(resultingText.contains("<uc change=\"added\""));
	}

	/**
	 * Tests {@link XmlDiffComparator#process(DocumentComparison)} with a single
	 * use case added, and an additional adaption in the post conditions of
	 * another use case.
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@Test
	public void testProcessAddAndAdapt() throws Exception {
		DocumentInstance d1 = createDocument("usecases-7.xml");
		DocumentInstance d2 = createDocument("usecases-8.xml");
		DocumentComparison comparison = new DocumentComparison(d1, d2);
		DocumentInstance result = comparator.process(comparison);

		// check text of compared documents
		String text1 = d1.getTextContents();
		String text2 = d2.getTextContents();
		// no "change" attributes in original text contents
		assertFalse(text1.contains(" change=\""));
		assertFalse(text2.contains(" change=\""));

		String resultingText = result.getTextContents();
		Pattern addedAttribute = Pattern
				.compile("<\\w+[\\s\\w\\.\\-\\:=\"]+change=\"added\"");
		Pattern removedAttribute = Pattern
				.compile("<\\w+[\\s\\w\\.\\-\\:=\"]+change=\"removed\"");
		Pattern addedElement = Pattern
				.compile("<change type=\"added\">.+\\s+</change>");
		Pattern removedElement = Pattern
				.compile("<change type=\"removed\">.+\\s+</change>");

		assertPatternCount(3, resultingText, addedAttribute);
		assertPatternCount(2, resultingText, removedAttribute);
		assertPatternCount(1, resultingText, addedElement);
		assertPatternCount(1, resultingText, removedElement);
	}
	
	/**
	 * Tests {@link XmlDiffComparator#process(DocumentComparison)} with one
	 * additional section that contains a reference to another use case.
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@Test
	public void testProcessNewSectionWithReference() throws Exception {
		DocumentInstance d1 = createDocument("uc-viewer-9.xml");
		DocumentInstance d2 = createDocument("uc-viewer-10.xml");

		DocumentComparison comparison = new DocumentComparison(d1, d2);
		String resultingText = comparator.process(comparison).getTextContents();

		// all reference elements
		assertTrue(resultingText.contains("<ref ref=\"uc.view_available_docs\"/>"));
		assertTrue(resultingText.contains("xml:id=\"uc.view_available_docs\""));
	}
	
	private void assertPatternCount(int expectedNbOcurrences, String text,
			Pattern searchPattern) {
		Matcher m = searchPattern.matcher(text);
		int count = 0;
		while (m.find()) {
			count++;
		}
		assertEquals("Expected " + expectedNbOcurrences + " occurrence(s) of "
				+ searchPattern.pattern() + " in text, but found " + count
				+ ".", expectedNbOcurrences, count);
	}

	private TestableDocumentInstance createDocument(String resourceLocation) {
		return new TestableDocumentInstance(getClass().getResourceAsStream(
				resourceLocation));
	}

}
