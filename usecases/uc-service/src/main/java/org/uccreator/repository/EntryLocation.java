// $Id$
package org.uccreator.repository;

import org.uccreator.model.Revision;

/**
 * Location of the entry in the repository
 * 
 * @author Kariem Hussein
 */
public class EntryLocation {
	/** The path */
	public final String path;
	/** The revision */
	public final long revision;

	/**
	 * @param path
	 *            the path
	 * @param revision
	 *            the revision
	 */
	public EntryLocation(String path, long revision) {
		this.path = path;
		this.revision = revision;
	}

	/**
	 * @param reposLocation
	 *            the repository location
	 * @param revision
	 *            the revision
	 */
	public EntryLocation(RepositoryLocation reposLocation, long revision) {
		this.path = reposLocation.getPath();
		this.revision = revision;
	}

	/**
	 * @param pathAndRevision
	 * path and revision separated by <tt>@</tt>
	 */
	public EntryLocation(String pathAndRevision) {
		String[] split = pathAndRevision.split("@");
		switch (split.length) {
		case 1:
			path = split[0];
			revision = Revision.LATEST.revision();
			break;
		case 2:
			path = split[0];
			try {
				revision = Long.parseLong(split[1]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"The revision parameter should be of type long.", e);
			}
			break;
		default:
			throw new IllegalArgumentException(
					"Path and revision should be separated with the character @.");
		}
	}

	@Override
	public String toString() {
		return path + "@" + revision;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + (int) (revision ^ (revision >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final EntryLocation other = (EntryLocation) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (revision != other.revision)
			return false;
		return true;
	}

}