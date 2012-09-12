/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.neural.networks;

import junit.framework.Assert;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.ml.train.MLTrain;
import org.encog.neural.freeform.FreeformLayer;
import org.encog.neural.freeform.FreeformNetwork;
import org.encog.neural.networks.layers.BasicLayer;

public class NetworkUtil {
	
	public static BasicNetwork createXORNetworkUntrained()
	{
		// random matrix data.  However, it provides a constant starting point 
		// for the unit tests.		
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,4));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
		network.getStructure().finalizeStructure();
		
		(new ConsistentRandomizer(-1,1)).randomize(network);
		
		return network;
	}
	
	public static BasicNetwork createXORNetworknNguyenWidrowUntrained()
    {
        // random matrix data.  However, it provides a constant starting point 
        // for the unit tests.
        
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null,true,2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,3));
        network.addLayer(new BasicLayer(null,false,1));
        network.getStructure().finalizeStructure();
        (new NguyenWidrowRandomizer()).randomize( network );
        
        return network;
    }
	
	public static void testTraining(MLTrain train, double requiredImprove)
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

	public static FreeformNetwork createXORFreeformNetworkUntrained() {
		FreeformNetwork network = new FreeformNetwork();
		FreeformLayer inputLayer = network.createInputLayer(2);
		FreeformLayer hiddenLayer1 = network.createLayer(3);
		FreeformLayer outputLayer = network.createOutputLayer(1);
		
		network.connectLayers(inputLayer, hiddenLayer1, new ActivationSigmoid(), 1.0, false);
		network.connectLayers(hiddenLayer1, outputLayer, new ActivationSigmoid(), 1.0, false);
		
		network.reset(1000);
		return network;
	}
}


