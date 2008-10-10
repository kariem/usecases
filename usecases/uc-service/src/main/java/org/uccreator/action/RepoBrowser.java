// $Id$
package org.uccreator.action;

import javax.ejb.Local;

import org.uccreator.model.ReposAccess;
import org.uccreator.repository.BackendCatalog;


/**
 * Repository browser.
 * 
 * @author Kariem Hussein
 */
@Local
public interface RepoBrowser {

	/**
	 * @param repository
	 *            the repository to open
	 * @return "success", if everything went fine, <code>null</code> otherwise
	 */
	String open(ReposAccess repository);

	/**
	 * Open the current repository
	 * 
	 * @return "success", if everything went fine, <code>null</code> otherwise
	 */
	String open();

	/** Open the current path for the selected repository */
	void browse();

	// getters and setters

	/**
	 * @return the catalog
	 */
	BackendCatalog getCatalog();

	/**
	 * @param catalog
	 */
	void setCatalog(BackendCatalog catalog);

}
