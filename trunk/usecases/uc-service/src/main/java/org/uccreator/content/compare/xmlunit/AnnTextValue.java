// $Id$
package org.uccreator.content.compare.xmlunit;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.NodeDetail;
import org.uccreator.content.compare.AnnotationDiff;
import org.uccreator.content.compare.AnnotationDiff.ChangeType;
import org.w3c.dom.Node;


/**
 * @author Kariem Hussein
 * 
 */
public class AnnTextValue extends DiffAnnotator {

	@Override
	public AnnotationDiff annotate(Difference d) {
		NodeDetail newDetail = d.getTestNodeDetail();
		NodeDetail oldDetail = d.getControlNodeDetail();

		String newVal = newDetail.getValue();
		String oldVal = oldDetail.getValue();
		if (newVal != null && oldVal != null) {
			String newTrimmed = newVal.trim();
			String oldTrimmed = oldVal.trim();
			if (newTrimmed.length() == 0 && oldTrimmed.length() == 0) {
				// two empty text nodes have changed -> nothing to do
				return null;
			}
			if (newTrimmed.equals(oldTrimmed)) {
				// no real change in text node
				return null;
			}
		}

		Node nodeNew = newDetail.getNode();
		String xpathNew = newDetail.getXpathLocation();

		AnnotationDiff aDiff = new AnnotationDiff(ChangeType.ADAPTED);
		aDiff.setNode(nodeNew);
		aDiff.setXpathLocation(xpathNew);

		return aDiff;
	}

}
