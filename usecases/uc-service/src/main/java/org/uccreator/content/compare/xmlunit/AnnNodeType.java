// $Id$
package org.uccreator.content.compare.xmlunit;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.NodeDetail;
import org.uccreator.content.ProcessingException;
import org.uccreator.content.compare.AnnotationDiff;
import org.uccreator.content.compare.AnnotationDiff.ChangeType;
import org.uccreator.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * @author Kariem Hussein
 *
 */
public class AnnNodeType extends DiffAnnotator {

	@Override
	public AnnotationDiff annotate(Difference d)  {
		NodeDetail old = d.getControlNodeDetail();
		String xpathOld = old.getXpathLocation();
		NodeDetail newDetail = d.getTestNodeDetail();
		Node nodeNew = newDetail.getNode();
		String xpathNew = newDetail.getXpathLocation();
		
		AnnotationDiff aDiff = new AnnotationDiff(ChangeType.ADAPTED);
		String commonXpath = XmlUtil.getCommonXpath(xpathNew, xpathOld);
		aDiff.setXpathLocation(commonXpath);
		Document doc = nodeNew.getOwnerDocument();
		try {
			aDiff.setNode(xpathNodeExtractor.getElement(doc, commonXpath));
		} catch (ProcessingException e) {
			log.fatal("Document: #0, Common XPath : #1", doc, commonXpath);
			throw new AssertionError("Cannot happen.");
		}
		
		return aDiff;
	}

}
