// $Id$
package org.uccreator.content;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.uccreator.model.DocumentInstance;


/**
 * Representation of {@link DocumentInstance} that allows for direct reading
 * from a resource.
 * 
 * @author Kariem Hussein
 */
public class TestableDocumentInstance extends DocumentInstance {

	private static final Log log = LogFactory
			.getLog(TestableDocumentInstance.class);

	final InputStream is;
	byte[] binaryContents;
	String textContents;

	/**
	 * @param resource
	 *            the resource to load
	 */
	public TestableDocumentInstance(String resource) {
		this(TestableDocumentInstance.class.getResourceAsStream(resource));
	}

	/**
	 * @param is
	 *            the input stream to use
	 */
	public TestableDocumentInstance(InputStream is) {
		super("teststream");
		if (is == null) {
			throw new IllegalArgumentException(
					"Input stream should not be null");
		}
		this.is = is;
		setText(true);
	}

	@Override
	public byte[] getBinaryContents() {
		checkInputRead();
		return super.getBinaryContents();
	}

	@Override
	public String getTextContents() throws UnsupportedEncodingException {
		checkInputRead();
		return super.getTextContents();
	}

	private void checkInputRead() {
		if (getOutputStream() != null) {
			return;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(bos);
		BufferedInputStream in = new BufferedInputStream(is);

		byte[] buffer = new byte[1024];

		try {
			while (true) {
				synchronized (buffer) {
					int amountRead = in.read(buffer);
					if (amountRead == -1) {
						break;
					}
					out.write(buffer, 0, amountRead);
				}
			}
		} catch (IOException e) {
			log.error("Could not copy from input stream to output stream", e);
			throw new RuntimeException(e);
		} finally {
			close(in);
			close(out);
		}
		setOutputStream(bos);
	}

	private void close(Closeable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (IOException e) {
			log.error("Could not close stream", e);
		}
	}

}
