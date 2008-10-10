// $Id$
package org.uccreator.content.compare;

import static org.uccreator.content.compare.AnnotationDiff.ChangeType.*;

import java.util.Collections;
import java.util.List;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.uccreator.content.ProcessingException;
import org.uccreator.content.compare.AnnotationDiff.ChangeType;
import org.uccreator.util.XmlUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * Apply annotations to the document by processing {@link AnnotationDiff}s.
 * 
 * @author Kariem Hussein
 */
public class XmlAnnotator {

	private static final Log LOG = Logging.getLog(XmlAnnotator.class);

	private XPathNodeExtractor xpathNodeExtractor;

	AnnotationResult annotate(Document doc, List<AnnotationDiff> diffs)
			throws ProcessingException {
		AnnotationResult result = new AnnotationResult();
		// in reverse order
		Collections.reverse(diffs);
		for (AnnotationDiff d : diffs) {
			result.add(annotate(doc, d));
		}
		return result;
	}

	AnnotationResult annotate(Document doc, AnnotationDiff d)
			throws ProcessingException {
		String loc = d.getXpathLocation();
		ChangeType change = d.changeType;

		Node newNode = d.getNode();
		Node affected = xpathNodeExtractor.getNode(doc, loc);

		AnnotationResult ar = new AnnotationResult();
		switch (change) {
		case ADDED:
			// annotate the new node
			Node annotated = addAnnotation(newNode, change, ar);
			// add node to the changed element
			importNodeAt(doc, loc, annotated);
			break;
		case REMOVED:
			// annotate the removed node
			addAnnotation(affected, change, ar);
			break;
		case ADAPTED:
			// create a new node with the correct annotation
			Node nNew = addAnnotation(newNode, ADDED, ar);
			// import this node
			importNodeAt(doc, loc, nNew);
			// add an annotation to the old node
			addAnnotation(affected, REMOVED, ar);
			break;
		case CONTENT:
			// only elements can change their content, don't update 'ar'
			overwriteAttributes((Element) affected, (Element) newNode);
		}

		return ar;
	}

	private void overwriteAttributes(Element target, Element source) {
		NamedNodeMap attrsOld = target.getAttributes();
		Document doc = target.getOwnerDocument();
		// remove old attributes
		for (int i = 0; i < attrsOld.getLength(); i++) {
			attrsOld.removeNamedItem(attrsOld.item(i).getNodeName());
		}
		NamedNodeMap attrsNew = source.getAttributes();
		for (int i = 0; i < attrsNew.getLength(); i++) {
			Node imported = importNode(doc, attrsNew.item(i));
			attrsOld.setNamedItem(imported);
		}
	}

	private void importNodeAt(Document doc, String loc, Node toImport)
			throws ProcessingException {
		Node imported = importNode(doc, toImport);
		Node n = xpathNodeExtractor.getNode(doc, loc);
		if (n == null) {
			try {
				n = getPreviousSibling(doc, loc);
			} catch (NumberFormatException e) {
				LOG.warn("Could not get index for previous sibling for #1", e,
						loc);
			}
			if (n == null) {
				importAtParent(doc, loc, imported);
				return;
			}
		}
		importNodeAfter(n, imported);
	}

	private Node getPreviousSibling(Document doc, String loc)
			throws NumberFormatException, ProcessingException {
		// xpath for new element location -> does not exist in old doc
		int length = loc.length();
		int posStart = loc.lastIndexOf("[");
		String xpathIndex = loc.substring(posStart + 1, length - 1);
		int index = Integer.parseInt(xpathIndex) - 1;
		if (index > 1) {
			String newLoc = loc.substring(0, posStart + 1) + index + "]";
			return xpathNodeExtractor.getNode(doc, newLoc);
		}
		return null;
	}

	private void importAtParent(Document doc, String loc, Node imported)
			throws ProcessingException {
		// get the parent
		String newLoc = xpathNodeExtractor.xpathUp(loc, 1);
		Node n = xpathNodeExtractor.getNode(doc, newLoc);
		if (n == null) {
			throw new ProcessingException(
					"Could not find parent to import new node at " + loc);
		}
		importNode(doc, n, imported);
	}

	private void importNodeAfter(Node sibling, Node toImport) {
		Element parent = XmlUtil.getAncestorElement(sibling.getParentNode());
		// only before is possible
		parent.insertBefore(toImport, sibling);
		parent.insertBefore(sibling, toImport);
	}

	private void importNode(Document doc, Node parent, Node toImport) {
		Node imported = importNode(doc, toImport);
		parent.appendChild(imported);
	}

	private Node importNode(Document doc, Node toImport) throws DOMException {
		return doc.importNode(toImport, true);
	}

	private Node addAnnotation(Node n, ChangeType changeType,
			AnnotationResult ar) throws ProcessingException {
		Node returnNode = null;
		short nodeType = n.getNodeType();
		switch (nodeType) {
		case Node.ELEMENT_NODE:
			((Element) n).setAttribute("change", changeType.toString());
			returnNode = n;
			break;
		case Node.TEXT_NODE:
			Element e = n.getOwnerDocument().createElement("change");
			e.setAttribute("type", changeType.toString());
			e.appendChild(n.cloneNode(true));
			// replace text node with element
			n.getParentNode().replaceChild(e, n);
			returnNode = e;
			break;
		default:
			throw new ProcessingException(
					"Cannot add annotation to node of type " + nodeType);
		}
		ar.addAnnotation(changeType);
		return returnNode;
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
