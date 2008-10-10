// $Id$
package org.uccreator.repository.backend;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.uccreator.BaseComponent;
import org.uccreator.content.ProcessingException;
import org.uccreator.model.ReposAccess;
import org.uccreator.repository.BackendHandler;
import org.uccreator.repository.RepositoryConnectionException;
import org.uccreator.repository.UseCaseRepository;


/**
 * Simple implementation of {@link BackendHandler}
 * 
 * @author Kariem Hussein
 */
public abstract class AbstractBackendHandler extends BaseComponent implements
		BackendHandler {

	public UseCaseRepository process(String input) throws ProcessingException {
		ReposAccess access = new ReposAccess();
		access.setUrl(input);
		try {
			return connect(access);
		} catch (RepositoryConnectionException e) {
			error("Could not connect to repository", e);
			throw new ProcessingException("Could not connect to repository", e);
		}
	}

	public boolean canProcess(String url) {
		try {
			URL u = new URL(url);
			return checkUrl(u);
		} catch (MalformedURLException e) {
			try {
				String encoded = URLEncoder.encode(url.toString(), "UTF-8");
				try {
					URL u = new URL(encoded);
					return checkUrl(u);
				} catch (MalformedURLException e1) {
					// encoding not possible
				}
			} catch (UnsupportedEncodingException e1) {
				debug("Could not encode #0 as URL. Error: #1", url, e
						.getMessage());
			}
			debug("Not a valid URL: #0. Error: #1", url, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks the given url
	 * 
	 * @param url
	 *            the URL
	 * @return <code>true</code> if the URL can be handled, false otherwise
	 */
	protected abstract boolean checkUrl(URL url);

}