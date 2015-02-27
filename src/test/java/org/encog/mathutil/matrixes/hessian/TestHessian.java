/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.mathutil.matrixes.hessian;

import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.matrices.hessian.ComputeHessian;
import org.encog.mathutil.matrices.hessian.HessianCR;
import org.encog.mathutil.matrices.hessian.HessianFD;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.layers.BasicLayer;

public class TestHessian extends TestCase {
	
	private void dump(ComputeHessian hess, String name) {
		System.out.println(name);
		double[][] h = hess.getHessian();
		System.out.println("Gradients: " + Arrays.toString(hess.getGradients()));
		for(int i=0;i<h.length;i++) {
			System.out.println(Arrays.toString(h[i]));
		}
	}
	
	public void testSingleOutput() {
		
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
		network.getStructure().finalizeStructure();
		
		(new ConsistentRandomizer(-1,1)).randomize(network);
		
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);		
		
		HessianFD testFD = new HessianFD(); 
		testFD.init(network, trainingData);
		testFD.compute();
				
		HessianCR testCR = new HessianCR(); 
		testCR.init(network, trainingData);
		testCR.compute();
		
		//dump(testFD, "FD");
		//dump(testCR, "CR");
		Assert.assertTrue(testCR.getHessianMatrix().equals(testFD.getHessianMatrix(), 4));
	}
	
	public void testDualOutput() {
		
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,2));
		network.getStructure().finalizeStructure();
		
		(new ConsistentRandomizer(-1,1)).randomize(network);
		
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL2);		
		
		HessianFD testFD = new HessianFD(); 
		testFD.init(network, trainingData);
		testFD.compute();
		
		//dump(testFD, "FD");
				
		HessianCR testCR = new HessianCR(); 
		testCR.init(network, trainingData);
		testCR.compute();
		
		
		//dump(testCR, "CR");
		Assert.assertTrue(testCR.getHessianMatrix().equals(testFD.getHessianMatrix(), 4));
	}
}
