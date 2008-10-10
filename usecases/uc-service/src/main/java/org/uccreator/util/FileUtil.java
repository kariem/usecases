// $Id$
package org.uccreator.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Kariem Hussein
 * 
 */
public class FileUtil {

	/**
	 * @param is
	 *            input stream
	 * @return string representation of the underlying contents
	 * @throws IOException
	 *             if an error occurred reading the contents of the stream
	 */
	public static String readAsString(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer(1024);
		String line;
		while ((line = in.readLine()) != null) {
			buffer.append(line);
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
