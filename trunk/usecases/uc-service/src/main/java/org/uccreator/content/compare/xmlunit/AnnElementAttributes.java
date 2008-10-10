// $Id$
package org.uccreator.content.compare.xmlunit;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.NodeDetail;
import org.uccreator.content.compare.AnnotationDiff;
import org.uccreator.content.compare.AnnotationDiff.ChangeType;


/**
 * Handles differences in element attributes.
 * @author Kariem Hussein
 */
public class AnnElementAttributes extends DiffAnnotator {

	@Override
	public AnnotationDiff annotate(Difference d) {
		NodeDetail newDetail = d.getTestNodeDetail();
		NodeDetail oldDetail = d.getControlNodeDetail();

		// set node to 'adapted'
		AnnotationDiff aDiff = new AnnotationDiff(ChangeType.CONTENT);
		aDiff.setNode(newDetail.getNode());
		// add xpath to attribute nodes
		aDiff.setXpathLocation(oldDetail.getXpathLocation());

		return aDiff;
	}

}
