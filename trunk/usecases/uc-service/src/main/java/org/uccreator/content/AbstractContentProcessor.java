// $Id$
package org.uccreator.content;

import org.uccreator.model.DocumentInstance;

/**
 * @author Kariem Hussein
 */
public abstract class AbstractContentProcessor extends AbstractXmlComponent implements
		ContentProcessor {

	public boolean canProcess(DocumentInstance instance) {
		return instance.isText();
	}

}
