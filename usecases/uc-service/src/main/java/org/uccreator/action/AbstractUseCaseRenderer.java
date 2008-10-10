// $Id$
package org.uccreator.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.uccreator.content.ContentProcessor;
import org.uccreator.content.ProcessingException;
import org.uccreator.model.DocumentInstance;


/**
 * Abstract base class for actions that render use cases.
 * @author Kariem Hussein
 */
public abstract class AbstractUseCaseRenderer extends AbstractRepoAction implements UseCaseRenderer {

	/** The instance being rendered. */
	@Out(required = false)
	protected DocumentInstance instance;
	
	@In
	ContentProcessor contentProcessor;

	public String getView() {
		prepareInstance();
		if (instance == null) {
			addFacesMessageError("No information available");
			return "No information available";
		}
		try {
			return contentProcessor.process(instance);
		} catch (ProcessingException e) {
			String name = instance.getId();
			String msg = "Error while trying to process #0: #1";
	
			error(msg, e, name, e.getMessage());
			addFacesMessageError(msg, name, e.getMessage());
			return "Could not process " + name;
		}
	}

}