// $Id$
package org.uccreator.model;

/**
 * @author Kariem Hussein
 */
public enum Revision {

	/** Latest revision */
	LATEST(-1),
	/** First revision */
	FIRST(0);

	private final long revision;

	Revision(long revision) {
		this.revision = revision;
	}

	/**
	 * @return the revision number
	 */
	public long revision() {
		return revision;
	}
}
