package org.encog.app.quant;

import org.encog.app.quant.normalize.NormalizeArray;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestNormalizeArray extends TestCase {

    public void testNormalize()
    {
        NormalizeArray norm = new NormalizeArray();
        double[] input = { 1,5,10 };
        double[] output = norm.process(input);
        Assert.assertEquals(3, output.length);
        Assert.assertEquals(-1.0, output[0]);
        Assert.assertEquals(1.0, output[2]);
        Assert.assertEquals(1.0, norm.getStats().getActualLow());
        Assert.assertEquals(10.0, norm.getStats().getActualHigh());
    }
	
}
