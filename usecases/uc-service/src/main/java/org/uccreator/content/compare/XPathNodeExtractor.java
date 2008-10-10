// $Id$
package org.uccreator.content.compare;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.uccreator.content.ProcessingException;
import org.uccreator.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Provides methods to extract nodes from documents via XPath.
 * 
 * @author Kariem Hussein
 */
public class XPathNodeExtractor {

	private final XPath xpath;
	private final Map<String, XPathExpression> expressions;

	/** Initializes XPath environment */
	public XPathNodeExtractor() {
		XPathFactory xpFactory = XPathFactory.newInstance();
		xpath = xpFactory.newXPath();

		expressions = new HashMap<String, XPathExpression>();
	}

	/**
	 * @param doc
	 *            the document
	 * @param loc
	 *            the xpath location
	 * @return the element at {@code loc} in {@code doc}. If the node at
	 *         {@code loc} is not an element, the next element in the ancestor
	 *         hierarchy is returned.
	 * @throws ProcessingException
	 */
	public Element getElement(Document doc, String loc)
			throws ProcessingException {
		Node n = getNode(doc, loc);
		return XmlUtil.getAncestorElement(n);
	}

	/**
	 * @param doc
	 *            the document
	 * @param loc
	 *            the xpath location
	 * @return the node at {@code loc} in {@code doc}
	 * @throws ProcessingException
	 */
	public Node getNode(Document doc, String loc) throws ProcessingException {
		try {
			
			XPathExpression expression = getXpathExpression(loc);
			Object o = expression.evaluate(doc, XPathConstants.NODE);
			return (Node) o;
		} catch (XPathExpressionException e) {
			throw new ProcessingException(
					"Could not evaluate XPath for " + loc, e);
		}
	}

	private XPathExpression getXpathExpression(String loc)
			throws XPathExpressionException {
		XPathExpression expression = expressions.get(loc);
		if (expression == null) {
			expression = xpath.compile(loc);
			expressions.put(loc, expression);
		}
		return expression;
	}

	/**
	 * @param xpathString
	 *            the xpath string
	 * @param levels
	 *            number of levels to go up in {@code xpathString}
	 * @return the xpath corresponding to the number of levels higher parent of
	 *         {@code xpathString}, or <tt>/</tt>, if the number of
	 *         ancestors is less than {@code levels}
	 */
	public String xpathUp(String xpathString, int levels) {
		String base = xpathString.endsWith("/") ? xpathString.substring(0,
				xpathString.length() - 1) : xpathString;

		int pos = 0;
		for (int i = base.length() - 1; i > 0; i--) {
			if (base.charAt(i) == '/') {
				pos++;
			}
			if (pos == levels) {
				return base.substring(0, i);
			}
		}
		return "/";
	}

	/**
	 * @param n
	 * @return the xpath suffix for this node, by finding previous siblings with
	 *         the same type and node name.
	 */
	public String calculate(Node n) {
		short type = n.getNodeType();
		String name = n.getNodeName();

		int count = 1;
		Node p = n.getPreviousSibling();
		while (p != null) {
			if (p.getNodeType() == type && p.getNodeName() == name) {
				count++;
			}
			p = p.getPreviousSibling();
		}
		String base = type == Node.ELEMENT_NODE ? name : "text()";
		return base + "[" + count + "]";
	}

}
