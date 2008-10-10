// $Id$
package org.uccreator.action;

import javax.ejb.Local;

/**
 * Use case viewer.
 * 
 * @author Kariem Hussein
 */
@Local
public interface UseCaseViewer extends UseCaseRenderer {

	/** Prepares the document */
	void prepareDocument();

	/**
	 * Initialize the document's history
	 */
	void showHistory();

	/**
	 * @return whether document history is available. This method can only
	 *         return true after a call to {@link #showHistory()}.
	 */
	boolean isHistoryAvailable();

	/** Cleanup */
	void destroy();
}
