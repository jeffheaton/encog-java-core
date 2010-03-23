/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks;

import junit.framework.Assert;

import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;

public class NetworkUtil {
	
	public static BasicNetwork createXORNetworkUntrained()
	{
		// random matrix data.  However, it provides a constant starting point 
		// for the unit tests.
		
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
		//network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,1));
		network.getStructure().finalizeStructure();
		
		(new ConsistentRandomizer(-1,1)).randomize(network);
		
		return network;
	}
	public static BasicNetwork createXORNetworkRangeRandomizedUntrained()
    {
        // random matrix data.  However, it provides a constant starting point 
        // for the unit tests.
        
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,1));
        network.getStructure().finalizeStructure();
        (new RangeRandomizer(-1,1)).randomize( network);
        
        return network;
    }
	
	public static BasicNetwork createXORNetworknNguyenWidrowUntrained()
    {
        // random matrix data.  However, it provides a constant starting point 
        // for the unit tests.
        
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,1));
        network.getStructure().finalizeStructure();
        (new NguyenWidrowRandomizer(-1,1)).randomize( network );
        
        return network;
    }
	
	public static void testTraining(Train train, double requiredImprove)
	{
		train.iteration();
		double error1 = train.getError();
		
		for(int i=0;i<10;i++)
			train.iteration();
		
		double error2 = train.getError();
		
		double improve = (error1-error2)/error1;
		Assert.assertTrue("Improve rate too low for " + train.getClass().getSimpleName() + 
				",Improve="+improve+",Needed="+requiredImprove, improve>=requiredImprove);
	}
}


