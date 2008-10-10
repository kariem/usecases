// $Id$
package org.uccreator.content.transform;

import static org.junit.Assert.*;

import org.jboss.seam.log.Logging;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.uccreator.BaseTest;
import org.uccreator.content.TestableDocumentInstance;
import org.uccreator.content.transform.TransformerBasedProcessor;
import org.uccreator.model.DocumentInstance;


/**
 * Tests {@link TransformerBasedProcessor}
 * 
 * @author Kariem Hussein
 */
public class TransformerBasedProcessorTest extends BaseTest {

	@Autowired
	TransformerBasedProcessor processor;

	/** Initializes logging for error handling */
	@Before
	public void initializeLogging() {
		processor.setLog(Logging.getLog(TransformerBasedProcessor.class));
	}

	/**
	 * Tests {@link TransformerBasedProcessor#process(DocumentInstance)}
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@Test
	public void testRenderOutput() throws Exception {
		DocumentInstance instance = new TestableDocumentInstance(
				"/uc/uc-viewer.xml");
		instance.setText(true);

		String output = processor.process(instance);
		assertNotNull(output);
		System.out.println(output);
		assertTrue("Output does not start with '<div>' element", output
				.indexOf("<div") == 0);
		assertTrue("Output does not end with '<div>' element",
				output.length() - 10 < output.lastIndexOf("</div>"));
	}
}
