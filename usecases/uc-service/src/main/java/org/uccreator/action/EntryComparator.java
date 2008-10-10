// $Id$
package org.uccreator.action;

import javax.ejb.Local;

/**
 * Use case viewer.
 * 
 * @author Kariem Hussein
 */
@Local
public interface EntryComparator {

	/** Compares two instances of a document */
	public void compare();

	/**
	 * @return the result of the comparison as string representation
	 */
	String getView();

	/** Cleanup */
	void destroy();
}
