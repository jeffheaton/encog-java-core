/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.persist;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.RadialBasisPattern;

public class TestPersist extends TestCase {
	
	private BasicNetwork getRBF()
	{
		RadialBasisPattern pattern = new RadialBasisPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		BasicNetwork net = (BasicNetwork)pattern.generate();
		return net;
	}
	
	private BasicNetwork getElman()
	{
		ElmanPattern pattern = new ElmanPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		pattern.setActivationFunction(new ActivationSigmoid());
		BasicNetwork net = (BasicNetwork)pattern.generate();
		return net;
	}
	
	public void testPersist()
	{
		EncogPersistedCollection encog = 
			new EncogPersistedCollection("encogtest.eg");
		encog.create();
		BasicNetwork net1 = getRBF();
		BasicNetwork net2 = getElman();
		encog.add("rbf", net1);
		encog.add("elman", net2);
		
		net1 = (BasicNetwork)encog.find("rbf");
		net2 = (BasicNetwork)encog.find("elman");
		
		Assert.assertNotNull(net1);
		Assert.assertNotNull(net2);
		
	}
}
