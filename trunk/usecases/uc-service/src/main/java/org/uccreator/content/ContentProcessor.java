// $Id$
package org.uccreator.content;

import org.uccreator.model.DocumentInstance;

/**
 * @author Kariem Hussein
 */
public interface ContentProcessor extends Processor<DocumentInstance, String> {

	/**
	 * @param instance
	 *            the instance
	 * @return an XHTML rendered representation of {@code instance}
	 * @throws ProcessingException
	 *             if an error occurred while trying to render the output.
	 * @see Processor#process(Object)
	 */
	String process(DocumentInstance instance) throws ProcessingException;

}
