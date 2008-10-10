// $Id$
package org.uccreator.model;

import org.junit.Test;
import org.uccreator.model.Document;
import org.uccreator.repository.ConnectedRepositoryTest;
import org.uccreator.repository.RepositoryEntry;


import static org.junit.Assert.*;
/**
 * Tests {@link Document}
 * @author Kariem Hussein
 *
 */
public class DocumentTest extends ConnectedRepositoryTest {
	
	/**
	 * Tests {@link Document#init()}
	 * @throws Exception
	 */
	@Test
	public void testInit() throws Exception {
		RepositoryEntry entry = repos.open(PATH_TO_FILE);
		Document d = new Document(entry);

		// initialize
		assertTrue(d.getRevisionStreams().isEmpty());
		d.init();
		assertFalse(d.getRevisionStreams().isEmpty());
	}

}
