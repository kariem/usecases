// $Id$
package org.uccreator.util;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.uccreator.util.XmlUtil;

/**
 * Tests {@link XmlUtil}
 * @author Kariem Hussein
 */
public class XmlUtilTest {

	/**
	 * Tests {@link XmlUtil#getCommonXpath(String, String)}
	 */
	@Test
	public void testCommonXpath() {
		String x1 = "/use-cases[1]/section[2]/uc[2]/post[1]/ul[1]/li[1]";
		String x2 = "/use-cases[1]/section[2]/uc[2]/";
		assertEquals("/use-cases[1]/section[2]/uc[2]", XmlUtil.getCommonXpath(x1, x2));
		
		x1 = "/use-cases[1]/section[2]/uc[2]/post[1]/ul[1]/li[1]";
		x2 = "/use-cases[1]/section[2]/uc[2]/something[1]/else";
		assertEquals("/use-cases[1]/section[2]/uc[2]", XmlUtil.getCommonXpath(x1, x2));
	}
}
