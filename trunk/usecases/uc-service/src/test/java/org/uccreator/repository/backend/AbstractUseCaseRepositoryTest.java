// $Id$
package org.uccreator.repository.backend;

import static junit.framework.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uccreator.TestableLog;
import org.uccreator.model.DocumentInstance;
import org.uccreator.repository.EntryLocation;
import org.uccreator.repository.RepositoryConnectionException;
import org.uccreator.repository.RepositoryEntry;
import org.uccreator.repository.RepositoryException;
import org.uccreator.repository.backend.AbstractUseCaseRepository;


/**
 * Tests {@link AbstractUseCaseRepository}
 * 
 * @author Kariem Hussein
 */
public class AbstractUseCaseRepositoryTest {

	AbstractUseCaseRepository repos;

	/** Initializes the repository under test with a plain repository. */
	@Before
	public void setUp() {
		repos = new AbstractUseCaseRepository() {
			public List<RepositoryEntry> getEntries(String path)
					throws RepositoryException {
				return null;
			}
			
			public DocumentInstance getContent(EntryLocation parameterObject) {
				return null;
			}

			public RepositoryEntry open(String path) throws RepositoryException {
				return null;
			}
		};
	}

	/**
	 * Tests handleConnectionException
	 */
	@Test
	public void testHandleConnectionException() {
		// prepare log
		repos.setLog(new TestableLog() {
			@Override
			public void warn(Object object, Throwable t, Object... params) {
				setLogged(true);
				assertEquals("1", params[0]);
				assertEquals("2", params[1]);
				assertEquals("error message", t.getMessage());
				assertEquals("print #0, #1", object);
			}
		});
		RepositoryConnectionException e = repos.connectionException(
				new Exception("error message"), "print #0, #1", "1", "2");
		assertEquals("error message", e.getCause().getMessage());
		assertEquals(repos, e.getRepos());
		assertEquals("error message", e.getMessage());

		assertTrue(((TestableLog) repos.getLog()).hasLogged());
	}
	
	/**
	 * Tests {@link AbstractUseCaseRepository#normalize(String)}
	 */
	@Test
	public void testNormalizePath() {
		assertEquals("/test", repos.normalize("/test/1/.."));
		assertEquals("/test/2", repos.normalize("/test/1/../2/"));
		assertEquals("/test/1", repos.normalize("/test/1/../2/../1"));
		assertEquals("/", repos.normalize("/test/1/../.."));
		assertEquals("/", repos.normalize("/test/.."));
		assertEquals("/", repos.normalize("/.."));
		assertEquals("/", repos.normalize("/../.."));
		assertEquals("/trunk", repos.normalize("//trunk"));
	}

}
