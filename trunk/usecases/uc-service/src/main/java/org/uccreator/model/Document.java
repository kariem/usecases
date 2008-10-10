// $Id$
package org.uccreator.model;

import java.util.ArrayList;
import java.util.List;

import org.uccreator.repository.RepositoryEntry;
import org.uccreator.repository.RepositoryException;


/**
 * @author Kariem Hussein
 */
public class Document {

	final RepositoryEntry entry;
	final List<RevisionStream> revisionStreams = new ArrayList<RevisionStream>();

	/**
	 * @param entry
	 *            the repository entry.
	 */
	public Document(RepositoryEntry entry) {
		this.entry = entry;
	}

	/**
	 * Initiliazes this document with versioned information.
	 * 
	 * @throws UnsupportedOperationException
	 * @throws RepositoryException
	 */
	public void init() throws UnsupportedOperationException,
			RepositoryException {
		this.revisionStreams.clear();
		this.revisionStreams.addAll(entry.getRevisionStreams());
	}

	/**
	 * @param stream
	 *            the name of the stream
	 * @param revision
	 *            the revision
	 * @return an instance of this document at {@code revision}
	 * @throws UnsupportedOperationException
	 *             if the document (respectively, the underlying entry) does not
	 *             provide any content information
	 * @throws RepositoryException
	 *             if an error occurred while trying to acces the information
	 *             from the repository
	 */
	public DocumentInstance getInstance(String stream, long revision)
			throws UnsupportedOperationException, RepositoryException {
		if (revisionStreams.isEmpty()) {
			return entry.getDocumentInstance(revision);
		}
		// FIXME stream is currently ignored
		RevisionStream rStream = revisionStreams.get(0);
		return rStream.loadInstance(revision);
	}

	/**
	 * @return the revisionStreams
	 */
	public List<RevisionStream> getRevisionStreams() {
		return revisionStreams;
	}
}
