// $Id$
package org.uccreator.content.compare;

import org.uccreator.model.DocumentInstance;

/**
 * @author Kariem Hussein
 * 
 */
public class DocumentComparison {

	/** First document instance */
	public final DocumentInstance d1;
	/** Second document instance */
	public final DocumentInstance d2;

	/**
	 * @param d1
	 * @param d2
	 */
	public DocumentComparison(DocumentInstance d1, DocumentInstance d2) {
		this.d1 = d1;
		this.d2 = d2;
	}

}
