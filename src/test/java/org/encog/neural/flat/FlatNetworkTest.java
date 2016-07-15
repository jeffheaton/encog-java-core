package org.encog.neural.flat;

import org.encog.EncogError;
import org.junit.Assert;
import org.junit.Test;

public class FlatNetworkTest {

    @Test(expected = EncogError.class)
    public void testDecodeNetworkThrowExceptionWhenWeightsSizesDiffer() throws Exception {

        FlatNetwork flatNetwork = new FlatNetwork(1, 2, 3, 4, true);

        flatNetwork.decodeNetwork(new double[]{1, 2, 3});
    }

    @Test(expected = NullPointerException.class)
    public void testDecodeNetworkThrowExceptionWhenNetworkIsUninitialized() throws Exception {

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