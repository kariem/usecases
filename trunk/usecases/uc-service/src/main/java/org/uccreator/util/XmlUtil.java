// $Id$
package org.uccreator.util;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Kariem Hussein
 * 
 */
public class XmlUtil {

	/**
	 * @param n
	 *            the node
	 * @return the first element in the ancestor tree of {@code n}. If
	 *         {@code n} is an {@link Element}, {@code n} is returned. If
	 *         {@code n} is <code>null</code>, this method returns
	 *         <code>null</code>.
	 */
	public static Element getAncestorElement(Node n) {
		if (n == null) {
			return null;
		}
		if (n.getNodeType() == Node.ELEMENT_NODE) {
			return (Element) n;
		}
		Node parent = n.getParentNode();
		if (parent == null) {
			Document doc = (Document) (n instanceof Document ? n : n
					.getOwnerDocument());
			return doc == null ? null : doc.getDocumentElement();
		}
		return getAncestorElement(parent);
	}

	/**
	 * The common xpath
	 * 
	 * @param x1
	 * @param x2
	 * @return the common xpath string in both {@code x1} and {@code x2}
	 */
	public static String getCommonXpath(String x1, String x2) {
		if (x1.equals(x2)) {
			return x1;
		}
		// remove everything after last node
		String s1 = x1.substring(0, x1.lastIndexOf('/'));
		String s2 = x2.substring(0, x2.lastIndexOf('/'));

		return getCommonString(s1, s2);
	}

	/**
	 * @param s1
	 * @param s2
	 * @return the common string in both {@code s1} and {@code s2}
	 */
	public static String getCommonString(String s1, String s2) {
		int l1 = s1.length();
		int l2 = s2.length();
		if (l1 == l2) {
			return s1;
		}

		int length = Math.min(l1, l2);

		String returnString = null;
		for (int i = 0; i < length; i++) {
			if (s1.charAt(i) == s2.charAt(i)) {
				continue;
			}
			returnString = s1.substring(0, i);
			break;
		}
		if (returnString == null) {
			returnString = l1 < l2 ? s1 : s2;
		}

		if (returnString.endsWith("/")) {
			int l = returnString.length();
			if (l > 1) {
				return returnString.substring(0, l - 1);
			}
		}
		return returnString;
	}

	/**
	 * Returns a string representation of {@code node}. This method should only
	 * be used for debugging purposes, at it creates a new transformer factory
	 * and transformer upon each call.
	 * 
	 * @param n
	 *            the node
	 * @return a string representation of {@code node}
	 */
	public static String toString(Node n) {
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			StringWriter sw = new StringWriter();
			t.transform(new DOMSource(n), new StreamResult(sw));
			return sw.toString();
		} catch (TransformerException e) {
			return null;
		}
	}

}
