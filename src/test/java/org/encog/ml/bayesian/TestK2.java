package org.encog.ml.bayesian;

import org.encog.ml.bayesian.training.k2.TrainK2;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

import junit.framework.Assert;
import junit.framework.TestCase;

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
		TrainK2 train = new TrainK2(network,data,10);
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
		TrainK2 train = new TrainK2(network,data,10);
		
		double p = train.calculateG(network, x1, x1.getParents());
		Assert.assertEquals(3.607503E-4, p, 0.0001);
		
		network.createDependancy(x1, x2);
		p = train.calculateG(network, x2, x2.getParents());
		Assert.assertEquals(0.0011111, p, 0.0001);	
		
		network.createDependancy(x2, x3);
		p = train.calculateG(network, x3, x3.getParents());
		Assert.assertEquals(0.0011111, p, 0.00555555);			
	}
}
