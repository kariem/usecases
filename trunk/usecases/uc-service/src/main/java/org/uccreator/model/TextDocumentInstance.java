// $Id$
package org.uccreator.model;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * {@link DocumentInstance} with text contents.
 * 
 * @author Kariem Hussein
 */
public class TextDocumentInstance extends DocumentInstance {

	String contents;

	/**
	 * @param id
	 *            the id of the instance
	 * @param contents
	 *            the contents
	 */
	public TextDocumentInstance(String id, String contents) {
		super(id);
		this.contents = contents;
	}

	@Override
	public String getTextContents() throws UnsupportedEncodingException {
		return contents;
	}

	@Override
	public ByteArrayOutputStream getOutputStream() {
		return null;
	}

	@Override
	public byte[] getBinaryContents() {
		return contents.getBytes();
	}

	@Override
	public boolean isText() {
		return true;
	}
}
