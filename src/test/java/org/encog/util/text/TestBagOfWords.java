package org.encog.util.text;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestBagOfWords extends TestCase {
	public static final String SAMPLE1 = "Now is the time for all good men to come to the aid of their country.";	
	
	public void testCount() {
		BagOfWords bag = new BagOfWords();
		bag.process(SAMPLE1);
		Assert.assertEquals(14,bag.size());
	}
}
