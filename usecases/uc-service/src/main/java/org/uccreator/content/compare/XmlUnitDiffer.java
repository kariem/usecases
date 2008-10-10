// $Id$
package org.uccreator.content.compare;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.uccreator.content.ProcessingException;
import org.uccreator.content.compare.AnnotationDiff.ChangeType;
import org.uccreator.content.compare.xmlunit.DiffAnnotator;
import org.w3c.dom.Node;


/**
 * {@link XmlDocumentDiffer} using <a
 * href="http://xmlunit.sourceforge.net/">xmlunit</a> to generate the required
 * difference information.
 * 
 * @author Kariem Hussein
 */
public class XmlUnitDiffer extends XmlDocumentDiffer {

	private XPathNodeExtractor xpathNodeExtractor;
	private Map<Integer, DiffAnnotator> diffAnnotators;

	boolean configured;

	private void configure() {
		if (configured) {
			return;
		}
		XMLUnit.setIgnoreAttributeOrder(true);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		XMLUnit.setControlDocumentBuilderFactory(dbf);
		XMLUnit.setTestDocumentBuilderFactory(dbf);
		configured = true;
	}

	public List<AnnotationDiff> process(String[] input)
			throws ProcessingException {
		return diff(input[0], input[1]);
	}

	@Override
	public boolean isNamespaceAware() {
		return false;
	}

	@SuppressWarnings("unchecked")
	List<AnnotationDiff> diff(String text1, String text2)
			throws ProcessingException {
		configure();
		DetailedDiff diff;
		try {
			diff = new DetailedDiff(new Diff(text1, text2));
		} catch (Exception e) {
			throw new ProcessingException(
					"Error while trying to compare document instances", e);
		}

		List<Difference> diffs = diff.getAllDifferences();
		List<AnnotationDiff> annotationDiffs = assembleDifferences(diffs);
		return annotationDiffs;
	}

	private List<AnnotationDiff> assembleDifferences(List<Difference> diffs) {
		SortedMap<String, AnnotationDiff> annDiffs = new TreeMap<String, AnnotationDiff>();

		for (Difference d : diffs) {
			AnnotationDiff aDiff = createDiff(d);
			if (aDiff == null) {
				// ignored -> no diff
				continue;
			}

			String location = aDiff.getXpathLocation();
			AnnotationDiff prev = annDiffs.put(location, aDiff);

			if (prev != null) {
				log.debug("Overwriting diff on #0 for #1[#2] with #3[#4]",
						location, prev.getChangeType(), nodeName(prev), aDiff
								.getChangeType(), nodeName(aDiff));
			}
		}

		consolidate(annDiffs);

		return new ArrayList<AnnotationDiff>(annDiffs.values());
	}

	private AnnotationDiff createDiff(Difference d) {
		DiffAnnotator diffAnn = diffAnnotators.get(new Integer(d.getId()));
		if (diffAnn == null) {
			// no corresponding annotator found
			log.debug("Unhandled type of difference: #0 (location: #1)", d
					.getDescription(), d.getControlNodeDetail()
					.getXpathLocation());
			return null;
		}
		AnnotationDiff diff = diffAnn.annotate(d);
		if (diff == null) {
			// empty annotation returned
			return null;
		}
		log.debug("adding #0[#1] for #2", diff.getChangeType(), nodeName(diff),
				diff.getXpathLocation());
		return diff;
	}

	/**
	 * Removes redundant information from the diffs.
	 * 
	 * @param annDiffs
	 *            the diffs to consolidate
	 */
	private void consolidate(SortedMap<String, AnnotationDiff> annDiffs) {
		logEntries(annDiffs, "before");

		AnnotationDiff last = null;
		Iterator<AnnotationDiff> i = annDiffs.values().iterator();
		while (i.hasNext()) {
			AnnotationDiff curr = i.next();
			if (last == null) {
				if (curr.changeType == ChangeType.ADAPTED) {
					last = curr;
				}
				continue;
			}
			if (curr.getXpathLocation().contains(last.getXpathLocation())) {
				// last is parent of current diff
				i.remove();
			} else {
				// update comparison element
				last = curr.changeType == ChangeType.ADAPTED ? curr : null;
			}
		}

		logEntries(annDiffs, "after");
	}

	private void logEntries(SortedMap<String, AnnotationDiff> annDiffs,
			String comment) {
		if (true) {
			// disabled logging
			return;
		}
		log.debug(comment);
		for (Map.Entry<String, AnnotationDiff> e : annDiffs.entrySet()) {
			String s = e.getKey();
			AnnotationDiff d = e.getValue();
			log.debug("#0: #1[#2]", s, d.getChangeType(), nodeName(d));
		}
	}

	private String nodeName(AnnotationDiff d) {
		Node n = d.getNode();
		if (n == null) {
			return "null";
		}
		return n.getNodeName();
	}

	/**
	 * @return the xpathNodeExtractor
	 */
	public XPathNodeExtractor getXpathNodeExtractor() {
		return xpathNodeExtractor;
	}

	/**
	 * @param xpathNodeExtractor
	 *            the xpathNodeExtractor to set
	 */
	public void setXpathNodeExtractor(XPathNodeExtractor xpathNodeExtractor) {
		this.xpathNodeExtractor = xpathNodeExtractor;
	}

	/**
	 * @return the diffAnnotators
	 */
	public Map<Integer, DiffAnnotator> getDiffAnnotators() {
		return diffAnnotators;
	}

	/**
	 * @param diffAnnotators
	 *            the diffAnnotators to set
	 */
	public void setDiffAnnotators(Map<Integer, DiffAnnotator> diffAnnotators) {
		this.diffAnnotators = diffAnnotators;
	}
}
