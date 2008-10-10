// $Id$
package org.uccreator.repository;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.uccreator.model.ReposAccess;
import org.uccreator.repository.BackendCatalog;
import org.uccreator.repository.RepositoryConnectionException;
import org.uccreator.repository.UnsupportedRepositoryException;


/**
 * Tests {@link BackendCatalog}
 * 
 * @author Kariem Hussein
 */
public class BackendCatalogTest extends RepositoryTest {

	@Autowired
	BackendCatalog catalog;
	
	/**
	 * Test method for {@link BackendCatalog#connect(ReposAccess)}.
	 * 
	 * @throws Exception
	 *             if an error has occurred.
	 */
	@Test(expected = UnsupportedRepositoryException.class)
	public final void testConnectEmptyRepository() throws Exception {
		ReposAccess access = new ReposAccess();
		// empty repository URL
		access.setUrl("");
		assertNull(catalog.connect(access));
	}

	/**
	 * Test method for {@link BackendCatalog#connect(ReposAccess)}.
	 * 
	 * @throws Exception
	 *             if an error has occurred.
	 */
	@Test(expected = RepositoryConnectionException.class)
	public final void testConnectInvalidRepository() throws Exception {
		ReposAccess access = new ReposAccess();
		// invalid repository URL
		access.setUrl("http://127.0.0.1");
		assertNull(catalog.connect(access));
	}

}
