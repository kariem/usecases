// $Id$
package org.uccreator.content.transform;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.uccreator.content.ProcessingException;
import org.uccreator.content.Processor;
import org.w3c.dom.Node;


/**
 * @author Kariem Hussein
 */
public class ExtractHtmlBodyProcessor implements Processor<Node, Node> {

	XPath xpath;
	XPathExpression expression;

	/** Initializes XPath environment */
	public ExtractHtmlBodyProcessor() {
		xpath = XPathFactory.newInstance().newXPath();
	}

	public boolean canProcess(Node input) {
		return true;
	}

	public Node process(Node input) throws ProcessingException {
		initExpression();
		try {
			return (Node) expression.evaluate(input, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new ProcessingException("Could not process input", e);
		}
	}

	private void initExpression() throws ProcessingException {
		if (expression != null) {
			return;
		}
		String xpathString = "//body/child::node()[1]";
		try {
			expression = xpath.compile(xpathString);
		} catch (XPathExpressionException e) {
			throw new ProcessingException("Could not compile xpath '"
					+ xpathString + "'", e);
		}
	}
}
