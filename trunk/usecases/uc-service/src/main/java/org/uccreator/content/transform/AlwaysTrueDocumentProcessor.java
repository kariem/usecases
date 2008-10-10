// $Id$
package org.uccreator.content.transform;

import org.uccreator.content.Processor;
import org.w3c.dom.Document;


/**
 * @author Kariem Hussein
 */
public class AlwaysTrueDocumentProcessor implements Processor<Document, Document>{

	public boolean canProcess(Document content) {
		return true;
	}

	public Document process(Document input) {
		return input;
	}
}
