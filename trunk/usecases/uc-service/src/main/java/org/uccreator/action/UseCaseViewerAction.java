// $Id$
package org.uccreator.action;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.uccreator.model.Document;
import org.uccreator.model.Revision;
import org.uccreator.repository.RepositoryEntry;
import org.uccreator.repository.RepositoryException;


/**
 * @author Kariem Hussein
 */
@Stateful
@Name("ucViewer")
@Scope(ScopeType.CONVERSATION)
public class UseCaseViewerAction extends AbstractUseCaseRenderer implements UseCaseViewer {

	@In(scope = ScopeType.CONVERSATION, required = false)
	RepositoryEntry reposEntry;

	@In(required = false)
	String stream;
	@In(required = false)
	@Out(required = false)
	Long revision;

	@In(required = false)
	@Out(required = false)
	Document document;

	private void getDocument() {
		if (document != null) {
			return;
		}
		if (reposEntry.isDirectory()) {
			return;
		}
		document = new Document(reposEntry);
	}
	
	private void getInstance() {
		long rev = revision == null ? Revision.LATEST.revision() : revision
				.longValue();

		try {
			instance = document.getInstance(stream, rev);
			revision = new Long(instance.getRevision());
		} catch (UnsupportedOperationException e) {
			String msg = "Cannot view #0, operation not supported: #1";
			warn(msg, e, reposEntry.getName(), e.getMessage());
			addFacesMessageError(msg, reposEntry.getName(), e.getMessage());
		} catch (RepositoryException e) {
			String msg = "An error occurred while trying to view #0: #1";
			warn(msg, e, reposEntry.getName(), e.getMessage());
			addFacesMessageError(msg, reposEntry.getName(), e.getMessage());
		}
	}

	public void prepareDocument() {
		getDocument();
	}
	public void prepareInstance() {
		getDocument();
		getInstance();
	}
	
	public void showHistory() {
		try {
			document.init();
		} catch (UnsupportedOperationException e) {
			String name = reposEntry.getName();
			String msg = "Document history for #0 not available: #1";

			error(msg, e, name, e.getMessage());
			addFacesMessageError(msg, name, e.getMessage());
		} catch (RepositoryException e) {
			String name = reposEntry.getName();
			String msg = "Error while trying to access document history for #0: #1";

			error(msg, e, name, e.getMessage());
			addFacesMessageError(msg, name, e.getMessage());
		}
	}

	public boolean isHistoryAvailable() {
		return document == null ? false : !document.getRevisionStreams()
				.isEmpty();
	}

	@Destroy
	@Remove
	public void destroy() {
		// nothing to do
	}
}
