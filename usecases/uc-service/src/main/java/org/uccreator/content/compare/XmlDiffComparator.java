// $Id$
package org.uccreator.content.compare;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.uccreator.content.AbstractXmlComponent;
import org.uccreator.content.DocumentComparator;
import org.uccreator.content.ProcessingException;
import org.uccreator.model.DocumentInstance;
import org.uccreator.model.TextDocumentInstance;
import org.w3c.dom.Document;


/**
 * {@link DocumentComparator} that uses an internal differ to calculate and
 * annotate differences between {@link DocumentInstance}s.
 * 
 * @author Kariem Hussein
 */
public class XmlDiffComparator extends AbstractXmlComponent implements
		DocumentComparator {

	private XmlDocumentDiffer differ;
	private XmlAnnotator annotator;

	public boolean canProcess(DocumentComparison input) {
		return input.d1.isText() && input.d2.isText();
	}

	@SuppressWarnings("unchecked")
	public DocumentInstance process(DocumentComparison comparison)
			throws ProcessingException {

		String text1 = null, text2 = null;
		DocumentInstance compareBase = comparison.d1;
		DocumentInstance compareChanged = comparison.d2;
		try {
			text1 = compareBase.getTextContents();
			text2 = compareChanged.getTextContents();
		} catch (UnsupportedEncodingException e) {
			throw new ProcessingException(
					"Could not retrieve text contents for comparison", e);
		}

		// check on plain text level, whether the two are identical
		if (text1.equals(text2)) {
			return compareBase;
		}

		// process differences
		String[] compare = new String[] { text1, text2 };
		List<AnnotationDiff> annotationDiffs = differ.process(compare);

		// check whether differences were found
		if (annotationDiffs == null || annotationDiffs.isEmpty()) {
			return compareBase;
		}

		// annotate the base document with the result of the annotations
		Document doc = getDocument(text1, differ.isNamespaceAware());
		AnnotationResult ar = annotator.annotate(doc, annotationDiffs);

		// create a new document instance from the result
		String id = comparison.d1.getId() + "<>" + comparison.d2.getId();
		TextDocumentInstance di = new TextDocumentInstance(id, toString(doc));
		// add annotation results to the document's properties
		di.getProperties().put(DocumentInstance.PROP_COMPARE_RESULTS, ar);
		return di;
	}

	/**
	 * @return the differ
	 */
	public XmlDocumentDiffer getDiffer() {
		return differ;
	}

	/**
	 * @param differ
	 *            the differ to set
	 */
	public void setDiffer(XmlDocumentDiffer differ) {
		this.differ = differ;
	}

	/**
	 * @return the annotator
	 */
	public XmlAnnotator getAnnotator() {
		return annotator;
	}

	/**
	 * @param annotator
	 *            the annotator to set
	 */
	public void setAnnotator(XmlAnnotator annotator) {
		this.annotator = annotator;
	}

}
