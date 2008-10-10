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
public class AnnChildNotFound extends DiffAnnotator {

	@Override
	public AnnotationDiff annotate(Difference d) {
		NodeDetail old = d.getControlNodeDetail();
		Node nodeOld = old.getNode();
		String xpathOld = old.getXpathLocation();
		NodeDetail newDetail = d.getTestNodeDetail();
		Node nodeNew = newDetail.getNode();
		String xpathNew = newDetail.getXpathLocation();

		AnnotationDiff aDiff;
		if (nodeOld == null) {
			// node was added
			aDiff = new AnnotationDiff(ChangeType.ADDED);
			if (nodeNew.getNodeType() == Node.TEXT_NODE) {
				String val = nodeNew.getNodeValue();
				if (val == null || val.trim().length() == 0) {
					// empty text node -> ignore
					return null;
				}
			}
			aDiff.setNode(nodeNew);
			String xpathBase = xpathNodeExtractor.xpathUp(xpathNew, 1);
			String xpathSuffix = xpathNodeExtractor.calculate(nodeNew);
			aDiff.setXpathLocation(xpathBase + "/" + xpathSuffix);
		} else {
			// node was removed
			aDiff = new AnnotationDiff(ChangeType.REMOVED);
			aDiff.setXpathLocation(xpathOld);
		}

		return aDiff;
	}

}
