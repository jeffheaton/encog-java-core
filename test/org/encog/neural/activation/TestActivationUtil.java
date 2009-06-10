package org.encog.neural.activation;

import org.junit.Assert;

import junit.framework.TestCase;

public class TestActivationUtil extends TestCase {
	
	public void testActivationUtil()
	{
		double[] d = ActivationUtil.toArray(1.0);
		double d2 = ActivationUtil.fromArray(d);
		Assert.assertEquals(1.0,d2,0.1);
	}
}
