// $Id$
package org.uccreator.repository.backend;

import static org.junit.Assert.*;

import org.junit.Test;
import org.uccreator.repository.ConnectedRepositoryTest;
import org.uccreator.repository.RepositoryEntry;
import org.uccreator.repository.backend.SubversionRepository;


/**
 * Tests {@link SubversionRepository}
 * 
 * @author Kariem Hussein
 */
public class SubversionRepositoryTest extends ConnectedRepositoryTest {

	/**
	 * Test method for {@link SubversionRepository#open(String)}.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public final void testOpenRelative() throws Exception {
		assertEquals(DEFAULT_PATH, repos.getCurrentPath());

		repos.open("");
		assertEquals(DEFAULT_PATH, repos.getCurrentPath());

		repos.open("style");
		assertEquals(DEFAULT_PATH + "/style", repos.getCurrentPath());

		repos.open("..");
		assertEquals(DEFAULT_PATH, repos.getCurrentPath());
	}

	/**
	 * Test method for {@link SubversionRepository#open(String)}.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public final void testOpenAbsolute() throws Exception {
		repos.open(DEFAULT_PATH);
		assertEquals(DEFAULT_PATH, repos.getCurrentPath());

		repos.open(DEFAULT_PATH + "/style");
		assertEquals(DEFAULT_PATH + "/style", repos.getCurrentPath());
	}

	/**
	 * Test method for {@link SubversionRepository#getContent(String)}.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public final void testGetContents() throws Exception {
		RepositoryEntry entry = repos.open(PATH_TO_FILE);
		String entryContents = entry.getContents();
		assertNotNull(entryContents);

		String reposContents = repos.getContent(PATH_TO_FILE).getTextContents();
		assertNotNull(reposContents);

		assertEquals(entryContents, reposContents);
	}

}
