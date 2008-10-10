// $Id$
package org.uccreator.action;

/**
 * Provides methods to render the current use case instance.
 * 
 * @author Kariem Hussein
 */
public interface UseCaseRenderer {

	/** Prepares the instance currently requested */
	void prepareInstance();

	/**
	 * @return the document as string representation
	 */
	String getView();

}
