package org.encog.util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestEngineArray extends TestCase {
	public void testReplace() {
		Assert.assertEquals("aa01bb", EngineArray.replace("aa##bb", "##", "01"));
		Assert.assertEquals("aabbcc", EngineArray.replace("aabbcc", "##", "01"));
	}
}
