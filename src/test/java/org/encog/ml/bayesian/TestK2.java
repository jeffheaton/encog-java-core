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
package org.encog.ml.bayesian;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.bayesian.training.BayesianInit;
import org.encog.ml.bayesian.training.TrainBayesian;
import org.encog.ml.bayesian.training.search.k2.SearchK2;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

public class TestK2 extends TestCase {
	
	public static final double DATA[][] = {
		{ 1, 0, 0 }, // case 1
		{ 1, 1, 1 }, // case 2
		{ 0, 0, 1 }, // case 3
		{ 1, 1, 1 }, // case 4
		{ 0, 0, 0 }, // case 5
		{ 0, 1, 1 }, // case 6
		{ 1, 1, 1 }, // case 7
		{ 0, 0, 0 }, // case 8
		{ 1, 1, 1 }, // case 9
		{ 0, 0, 0 }, // case 10		
	};
	
	public void testK2Structure() {
		String[] labels = { "available", "not" };
		
		MLDataSet data = new BasicMLDataSet(DATA,null);
		BayesianNetwork network = new BayesianNetwork();
		BayesianEvent x1 = network.createEvent("x1", labels);
		BayesianEvent x2 = network.createEvent("x2", labels);
		BayesianEvent x3 = network.createEvent("x3", labels);
		network.finalizeStructure();
		TrainBayesian train = new TrainBayesian(network,data,10);
		train.setInitNetwork(BayesianInit.InitEmpty);
		while(!train.isTrainingDone()) {
			train.iteration();
		}
		train.iteration();
		Assert.assertTrue(x1.getParents().size()==0);
		Assert.assertTrue(x2.getParents().size()==1);
		Assert.assertTrue(x3.getParents().size()==1);
		Assert.assertTrue(x2.getParents().contains(x1));
		Assert.assertTrue(x3.getParents().contains(x2));
		Assert.assertEquals(0.714, network.getEvent("x2").getTable().findLine(1, new int[] {1}).getProbability(),0.001);
		
	}
	
	public void testK2Calc() {
		String[] labels = { "available", "not" };
		
		MLDataSet data = new BasicMLDataSet(DATA,null);
		BayesianNetwork network = new BayesianNetwork();
		BayesianEvent x1 = network.createEvent("x1", labels);
		BayesianEvent x2 = network.createEvent("x2", labels);
		BayesianEvent x3 = network.createEvent("x3", labels);
		network.finalizeStructure();
		TrainBayesian train = new TrainBayesian(network,data,10);
		SearchK2 search = (SearchK2)train.getSearch();
		
		double p = search.calculateG(network, x1, x1.getParents());
		Assert.assertEquals(3.607503E-4, p, 0.0001);
		
		network.createDependency(x1, x2);
		p = search.calculateG(network, x2, x2.getParents());
		Assert.assertEquals(0.0011111, p, 0.0001);	
		
		network.createDependency(x2, x3);
		p = search.calculateG(network, x3, x3.getParents());
		Assert.assertEquals(0.0011111, p, 0.00555555);			
	}
}
