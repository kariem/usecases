// $Id$
package org.uccreator.model;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Single instance of a document, representing this document's state at a
 * certain revision.
 * 
 * @author Kariem Hussein
 */
public class DocumentInstance implements Comparable<DocumentInstance> {

	/** Property for diff results */
	public static final String PROP_COMPARE_RESULTS = "compare_results";

	final String id;
	Map<String, Object> properties;
	long revision;
	ByteArrayOutputStream outputStream;
	boolean text;

	/**
	 * @param id
	 *            the id of the instance
	 */
	public DocumentInstance(String id) {
		this.id = id;
	}

	/**
	 * @return the contents of this document instance as string.
	 * @throws UnsupportedEncodingException
	 *             if the contents could not be encoded as UTF-8
	 */
	public String getTextContents() throws UnsupportedEncodingException {
		if (!text) {
			return "Document could not be detected as text";
		}
		return outputStream.toString("UTF-8");
	}

	/**
	 * @return a binary representation of this document instance
	 */
	public byte[] getBinaryContents() {
		return outputStream.toByteArray();
	}

	public int compareTo(DocumentInstance o) {
		long revDif = revision - o.revision;
		return (int) (revDif > 0 ? Math.min(Integer.MAX_VALUE, revDif) : Math
				.max(Integer.MIN_VALUE, revDif));
	}

	// getters and setters
	
	/**
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		if (properties == null) {
			properties = new HashMap<String, Object>(1);
		}
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * @return the revision
	 */
	public long getRevision() {
		return revision;
	}

	/**
	 * @param revision
	 *            the revision to set
	 */
	public void setRevision(long revision) {
		this.revision = revision;
	}

	/**
	 * @return the outputStream
	 */
	public ByteArrayOutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * @param outputStream
	 *            the outputStream to set
	 */
	public void setOutputStream(ByteArrayOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * @return the text
	 */
	public boolean isText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(boolean text) {
		this.text = text;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

}