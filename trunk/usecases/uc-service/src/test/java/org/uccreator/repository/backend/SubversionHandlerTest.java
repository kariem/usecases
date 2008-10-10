// $Id$
package org.uccreator.repository.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.uccreator.model.ReposAccess;
import org.uccreator.repository.RepositoryTest;
import org.uccreator.repository.UseCaseRepository;
import org.uccreator.repository.backend.SubversionHandler;


/**
 * Tests {@link SubversionHandler}
 * 
 * @author Kariem Hussein
 */
public class SubversionHandlerTest extends RepositoryTest {

	@Autowired
	SubversionHandler svnHandler;

	/**
	 * Tests {@link SubversionHandler#canProcess(String)}.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testCanHandle() throws Exception {
		assertTrue(svnHandler.canProcess("http://test"));
		assertTrue(svnHandler.canProcess("https://test"));
		assertTrue(svnHandler.canProcess("http://test/something"));
		assertTrue(svnHandler.canProcess("http://test/some thing"));
	}

	/**
	 * Tests {@link SubversionHandler#connect(ReposAccess)}.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testConnect() throws Exception {
		UseCaseRepository ucRepos = connectTo(DEFAULT_URL);

		// check repository settings
		assertEquals("http://usecases.googlecode.com/svn", ucRepos.getName());
		List<String> paths = ucRepos.getPaths();

		// check contained paths
		assertNotNull(paths);
		assertEquals(1, paths.size());
		assertEquals("/trunk/uc", paths.get(0));
	}

	/**
	 * Tests {@link SubversionHandler#connect(ReposAccess)}.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testConnectRootUrl() throws Exception {
		UseCaseRepository ucRepos = connectTo("http://usecases.googlecode.com/svn/");

		// check repository settings
		assertEquals("http://usecases.googlecode.com/svn", ucRepos.getName());
		List<String> paths = ucRepos.getPaths();

		// check contained paths
		assertNotNull(paths);
		assertEquals(1, paths.size());
		assertEquals("/", paths.get(0));
	}

}
