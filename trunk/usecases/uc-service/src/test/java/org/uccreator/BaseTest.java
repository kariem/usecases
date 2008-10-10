// $Id$
package org.uccreator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.uccreator.util.FileUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


/**
 * Superclass for spring-based JUnit test cases. Copied context awareness from
 * {@link AbstractJUnit4SpringContextTests}.
 * 
 * @author Kariem Hussein
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContext-test.xml" })
public abstract class BaseTest implements ApplicationContextAware {

	/** Logger */
	protected final Log logger = LogFactory.getLog(getClass());
	/** Injected application context. */
	protected ApplicationContext applicationContext;
	private DocumentBuilderFactory dbf;

	/**
	 * Sets the {@link ApplicationContext} to be used by this test instance,
	 * provided via {@link ApplicationContextAware} semantics.
	 * 
	 * @param applicationContext
	 *            The applicationContext to set.
	 */
	public final void setApplicationContext(
			final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * @param resourceLocation
	 *            the resource location
	 * @return the string read from the resource
	 * @throws RuntimeException
	 *             if the resource could not be found, or could not be read as
	 *             string.
	 */
	protected String readFromResource(String resourceLocation)
			throws RuntimeException {
		InputStream is = getStream(resourceLocation);
		try {
			return FileUtil.readAsString(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param resourceLocation
	 * @return the document read from the resource
	 * @throws RuntimeException
	 *             if the resource could not be found, or could not be read as
	 *             document.
	 */
	protected Document readDocumentFromResource(String resourceLocation)
			throws RuntimeException {
		InputStream is = getStream(resourceLocation);
		try {
			return getDocumentBuilder().parse(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param text
	 * @return the document created from {@code text}
	 * @throws RuntimeException
	 *             if the text could not be parsed as document.
	 */
	protected Document readDocumentFromText(String text)
			throws RuntimeException {
		StringReader reader = new StringReader(text);
		try {
			return getDocumentBuilder().parse(new InputSource(reader));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private DocumentBuilder getDocumentBuilder()
			throws ParserConfigurationException {
		if (dbf == null) {
			dbf = DocumentBuilderFactory.newInstance();
		}
		return dbf.newDocumentBuilder();
	}

	private InputStream getStream(String resourceLocation) {
		return getClass().getResourceAsStream(resourceLocation);
	}

}