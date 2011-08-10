package org.encog.util.arrayutil;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestNorm extends TestCase {
	
	public void testRoundTrip1() {
		NormalizedField field = new NormalizedField(NormalizationAction.Normalize, null, 10, 0, -1, 1);
		double d = 5;
		double d2= field.normalize(d);
		double d3 = field.deNormalize(d2);
		
		Assert.assertTrue( ((int)d) == ((int)d3) );

	}
	
	public void testRoundTrip2() {
		NormalizedField field = new NormalizedField(NormalizationAction.Normalize, null, 10, -10, -1, 1);
		double d = 5;
		double d2= field.normalize(d);
		double d3 = field.deNormalize(d2);

		Assert.assertTrue( ((int)d) == ((int)d3) );
	}
}
