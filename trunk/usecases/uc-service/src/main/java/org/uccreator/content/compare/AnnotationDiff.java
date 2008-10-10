// $Id$
package org.uccreator.content.compare;

import org.w3c.dom.Node;

/**
 * Single diff information with xpath location and type of change.
 * 
 * @author Kariem Hussein
 */
public class AnnotationDiff {

	/** Type of change */
	public enum ChangeType {
		/** Change with additions */
		ADDED,
		/** Change with removals */
		REMOVED,
		/** Change with general adaptations */
		ADAPTED,
		/** Content changed */
		CONTENT;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	String xpathLocation;
	final ChangeType changeType;
	Node node;

	/**
	 * @param changeType
	 *            the type of change
	 */
	public AnnotationDiff(ChangeType changeType) {
		this.changeType = changeType;
	}

	/**
	 * @return the xpathLocation
	 */
	public String getXpathLocation() {
		return xpathLocation;
	}

	/**
	 * @param xpathLocation
	 *            the xpathLocation to set
	 */
	public void setXpathLocation(String xpathLocation) {
		this.xpathLocation = xpathLocation;
	}

	/**
	 * @return the changeType
	 */
	public ChangeType getChangeType() {
		return changeType;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the newNode to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public String toString() {
		return changeType + ":" + xpathLocation;
	}
}
