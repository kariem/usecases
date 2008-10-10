// $Id$
package org.uccreator.repository;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.uccreator.model.ContentType;
import org.uccreator.model.DocumentInstance;
import org.uccreator.model.Revision;
import org.uccreator.model.RevisionStream;


/**
 * @author Kariem Hussein
 */
public class RepositoryEntry implements Comparable<RepositoryEntry> {

	final String name;
	final RepositoryLocation location;

	Date lastChangeDate;
	ContentType contentType = ContentType.UNKNOWN;

	private List<RepositoryEntry> entries;
	private boolean entriesValid;

	/**
	 * @param name
	 *            the name of the entry
	 * @param location
	 *            the location of the entry
	 */
	public RepositoryEntry(String name, RepositoryLocation location) {
		this.name = name;
		this.location = location;
	}

	public int compareTo(RepositoryEntry o) {
		if (contentType != o.getContentType()) {
			return isDirectory() ? -1 : 1;
		}
		return name.compareTo(o.name);
	}

	/**
	 * @return the contents of this entry
	 * @throws RepositoryException
	 *             if no content is available for this entry.
	 * @throws UnsupportedOperationException
	 *             if the entry is a directory.
	 * @throws UnsupportedEncodingException
	 *             if the contents could not be encoded in UTF-8
	 */
	public String getContents() throws RepositoryException,
			UnsupportedOperationException, UnsupportedEncodingException {
		return getDocumentInstance(Revision.LATEST.revision())
				.getTextContents();
	}

	/**
	 * @param revision
	 *            the revision of the instance to retrieve
	 * @return the document instance at {@code revision}
	 * @throws RepositoryException
	 *             if an error occurred while trying to access the information
	 *             in the repository.
	 * @throws UnsupportedOperationException
	 *             if this entry does not provide any contents, e.g. the entry
	 *             is a directory: use {@link #getEntries()} to retrieve a list
	 *             of children.
	 */
	public DocumentInstance getDocumentInstance(long revision)
			throws RepositoryException, UnsupportedOperationException {
		checkInformationAccessible();

		UseCaseRepository repos = location.getRepository();
		return repos.getContent(new EntryLocation(location, revision));
	}

	/**
	 * @return the revision streams of this entry
	 * @throws RepositoryException
	 *             if an error occurred while trying to access the information
	 * @throws UnsupportedOperationException
	 *             if the entry does not provide any information on its version
	 *             information
	 */
	public List<RevisionStream> getRevisionStreams()
			throws RepositoryException, UnsupportedOperationException {
		checkInformationAccessible();

		return location.getRepository().getRevisionStreams(
				new EntryLocation(location.getPath(), Revision.LATEST
						.revision()));
	}

	private void checkInformationAccessible() throws RepositoryException,
			UnsupportedOperationException {
		if (isDirectory()) {
			throw new UnsupportedOperationException(
					"Entry is a directory, cannot list contents.");
		}
		if (location == null) {
			throw new RepositoryException("No content available", null);
		}
	}

	/**
	 * @return a list of children of this entry. If this entry is not a
	 *         directory, this method returns an empty list.
	 * @throws RepositoryException
	 */
	public List<RepositoryEntry> getEntries() throws RepositoryException {
		if (!entriesValid) {
			entries = assembleEntries();
			Collections.sort(entries);
			entriesValid = true;
		}
		return entries;
	}

	@SuppressWarnings("unchecked")
	private List<RepositoryEntry> assembleEntries() throws RepositoryException {
		String path = location.getPath();
		if (!isDirectory()) {
			return Collections.emptyList();
		}

		return location.getRepository().getEntries(path);
	}

	/**
	 * @return name, optionally appended by "/", if this entry's content type is
	 *         {@linkplain ContentType#DIRECTORY}.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return isDirectory() ? name + "/" : name;
	}

	/**
	 * @return <code>true</code>, if this entry denotes a directory,
	 *         <code>false</code> otherwise.
	 */
	public boolean isDirectory() {
		return contentType == ContentType.DIRECTORY;
	}

	// getters and setters

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the lastChangeDate
	 */
	public Date getLastChangeDate() {
		return lastChangeDate;
	}

	/**
	 * @param lastChangeDate
	 *            the lastChangeDate to set
	 */
	public void setLastChangeDate(Date lastChangeDate) {
		this.lastChangeDate = lastChangeDate;
	}

	/**
	 * @return the contentType
	 */
	public ContentType getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the location
	 */
	public RepositoryLocation getLocation() {
		return location;
	}

}
