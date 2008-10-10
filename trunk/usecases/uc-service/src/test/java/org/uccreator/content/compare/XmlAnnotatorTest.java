// $Id$
package org.uccreator.content.compare;

import static org.junit.Assert.*;
import static org.uccreator.content.compare.AnnotationDiff.ChangeType.*;

import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.Diff;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.uccreator.BaseTest;
import org.uccreator.content.compare.AnnotationDiff;
import org.uccreator.content.compare.AnnotationResult;
import org.uccreator.content.compare.XmlAnnotator;
import org.uccreator.content.compare.AnnotationDiff.ChangeType;
import org.uccreator.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * Tests {@link XmlAnnotator}.
 * 
 * @author Kariem Hussein
 * 
 */
public class XmlAnnotatorTest extends BaseTest {

	@Autowired
	XmlAnnotator annotator;

	/**
	 * @throws Exception
	 */
	@Test
	public void testAnnotateNewElement() throws Exception {
		Document doc = readDocumentFromText("<ul><li>1</li></ul>");
		// new element <li>2</li> added
		AnnotationDiff diff = createDiff(ADDED, "/ul[1]/li[2]");
		Element e = doc.createElement("li");
		e.appendChild(doc.createTextNode("2"));
		diff.setNode(e);

		AnnotationResult ar = annotator.annotate(doc, diff);
		assertEquals(1, ar.added);

		String docAsString = XmlUtil.toString(doc);
		Diff d = new Diff("<ul><li>1</li><li change=\"added\">2</li></ul>",
				docAsString);
		assertTrue(d.toString(), d.identical());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAnnotateRemoved() throws Exception {
		Document doc = readDocumentFromText("<ul><li>1</li></ul>");
		// element <li> removed
		AnnotationDiff diff = createDiff(REMOVED, "/ul[1]/li[1]");

		AnnotationResult ar = annotator.annotate(doc, diff);
		assertEquals(1, ar.removed);

		String docAsString = XmlUtil.toString(doc);
		Diff d = new Diff("<ul><li change=\"removed\">1</li></ul>", docAsString);
		assertTrue(d.toString(), d.identical());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAnnotateTextContentChanged() throws Exception {
		Document doc = readDocumentFromText("<ul><li>1</li></ul>");
		// text in <li> has changed from "1" to "2"
		AnnotationDiff diff = createDiff(ADAPTED, "ul[1]/li[1]/text()[1]");
		diff.setNode(createText(doc, "2"));

		AnnotationResult ar = annotator.annotate(doc, diff);
		assertEquals(1, ar.added);
		assertEquals(1, ar.removed);
		
		String docAsString = XmlUtil.toString(doc);
		Diff d = new Diff(
				"<ul><li><change type=\"removed\">1</change><change type=\"added\">2</change></li></ul>",
				docAsString);
		assertTrue(d.toString(), d.identical());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAnnotateTextContentChangedTwice() throws Exception {
		Document doc = readDocumentFromText("<ul><li>1</li><li>unchanged</li><li>a</li></ul>");
		List<AnnotationDiff> diffs = new ArrayList<AnnotationDiff>();
		// text in li[1] has changed from "1" to "2"
		AnnotationDiff diff = createDiff(ADAPTED, "/ul[1]/li[1]/text()[1]");
		diff.setNode(createText(doc, "2"));
		diffs.add(diff);
		// text in li[2] has changed from "a" to "b"
		diff = createDiff(ADAPTED, "/ul[1]/li[3]/text()[1]");
		diff.setNode(createText(doc, "b"));
		diffs.add(diff);

		AnnotationResult ar = annotator.annotate(doc, diffs);
		assertEquals(2, ar.added);
		assertEquals(2, ar.removed);

		String docAsString = XmlUtil.toString(doc);
		Diff d = new Diff(
				"<ul><li>"
						+ "<change type=\"removed\">1</change><change type=\"added\">2</change>"
						+ "</li><li>unchanged</li><li>"
						+ "<change type=\"removed\">a</change><change type=\"added\">b</change>"
						+ "</li></ul>", docAsString);
		assertTrue(d.toString(), d.identical());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAnnotateMixedTextContentChanged() throws Exception {
		Document doc = readDocumentFromText("<ul>text<li>1</li></ul>");
		// text in ul has changed from "text" to "newtext"
		AnnotationDiff diff = createDiff(ADAPTED, "/ul[1]/text()[1]");
		diff.setNode(createText(doc, "newtext"));

		AnnotationResult ar = annotator.annotate(doc, diff);
		assertEquals(1, ar.added);
		assertEquals(1, ar.removed);

		String docAsString = XmlUtil.toString(doc);
		Diff d = new Diff(
				"<ul>"
						+ "<change type=\"removed\">text</change><change type=\"added\">newtext</change>"
						+ "<li>1</li></ul>", docAsString);
		assertTrue(d.toString(), d.identical());
	}

	/**
	 * @param doc
	 *            the document
	 * @param text
	 *            the text to be put into the node
	 * @return a newly created text node from {@code doc} with an referencable
	 *         parent node
	 */
	private Text createText(Document doc, String text) {
		Text n = doc.createTextNode(text);
		doc.createElement("e").appendChild(n);
		return n;
	}

	private AnnotationDiff createDiff(ChangeType change, String xpath) {
		AnnotationDiff diff = new AnnotationDiff(change);
		diff.setXpathLocation(xpath);
		return diff;
	}
}
