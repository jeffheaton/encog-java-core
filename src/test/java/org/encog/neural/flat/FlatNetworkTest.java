package org.encog.neural.flat;

import org.encog.EncogError;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FlatNetworkTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testDecodeNetworkThrowExceptionWhenWeightsSizesDiffer() throws Exception {

        expectedEx.expect(EncogError.class);
        expectedEx.expectMessage("Incompatible weight sizes, can't assign length=3 to length=29");

        FlatNetwork flatNetwork = new FlatNetwork(1, 2, 3, 4, true);

        flatNetwork.decodeNetwork(new double[]{1, 2, 3});
    }

    @Test
    public void testDecodeNetworkThrowExceptionWhenNetworkIsUninitialized() throws Exception {

        expectedEx.expect(NullPointerException.class);

        FlatNetwork flatNetwork = new FlatNetwork();

        flatNetwork.decodeNetwork(new double[]{1, 2, 3});
    }

    @Test
    public void testDecodeNetwork() throws Exception {

        FlatNetwork flatNetwork = new FlatNetwork(1, 2, 3, 4, true);
        double[] weights = new double[29];
        for (int i = 0; i < 29; i++) {
            weights[i] = i;
        }

        flatNetwork.decodeNetwork(weights);

        Assert.assertArrayEquals(weights, flatNetwork.getWeights(), 0);
    }
}