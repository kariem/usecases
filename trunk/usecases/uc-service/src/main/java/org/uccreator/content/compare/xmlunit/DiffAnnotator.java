// $Id$
package org.uccreator.content.compare.xmlunit;

import org.custommonkey.xmlunit.Difference;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.uccreator.content.compare.AnnotationDiff;
import org.uccreator.content.compare.XPathNodeExtractor;
import org.uccreator.util.XmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author Kariem Hussein
 */
public abstract class DiffAnnotator {

	/** The logger */
	protected Log log = Logging.getLog(getClass());
	XPathNodeExtractor xpathNodeExtractor;

	/**
	 * @param d
	 *            the difference
	 * @return the annotation
	 */
	public abstract AnnotationDiff annotate(Difference d);

	Element getElement(Node n) {
		return XmlUtil.getAncestorElement(n);
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

}