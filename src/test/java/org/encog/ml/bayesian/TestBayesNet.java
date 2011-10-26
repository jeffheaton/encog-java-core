package org.encog.ml.bayesian;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestBayesNet extends TestCase {
	
	public void testCount() {
		BayesianNetwork network = new BayesianNetwork();
		BayesianEvent a = network.createEvent("a");
		BayesianEvent b = network.createEvent("b");
		BayesianEvent c = network.createEvent("c");
		BayesianEvent d = network.createEvent("d");
		BayesianEvent e = network.createEvent("e");
		network.createDependancy(a, b, d, e);
		network.createDependancy(c, d);
		network.createDependancy(b, e);
		network.createDependancy(d, e);
		network.finalizeStructure();
		Assert.assertEquals(16, network.calculateParameterCount());
	}
	
	public void testIndependant() {
		BayesianNetwork network = new BayesianNetwork();
		BayesianEvent a = network.createEvent("a");
		BayesianEvent b = network.createEvent("b");
		BayesianEvent c = network.createEvent("c");
		BayesianEvent d = network.createEvent("d");
		BayesianEvent e = network.createEvent("e");
		network.createDependancy(a, b, d, e);
		network.createDependancy(c, d);
		network.createDependancy(b, e);
		network.createDependancy(d, e);
		network.finalizeStructure();
		
		Assert.assertFalse( network.isCondIndependant(c,e,a) );
		Assert.assertFalse(  network.isCondIndependant(b,d,c,e) );
		Assert.assertFalse(  network.isCondIndependant(a,c,e) );
		Assert.assertTrue(  network.isCondIndependant(a,c,b) );
	}
	
	public void testIndependant2() {
		BayesianNetwork network = new BayesianNetwork();
		BayesianEvent a = network.createEvent("a");
		BayesianEvent b = network.createEvent("b");
		BayesianEvent c = network.createEvent("c");
		BayesianEvent d = network.createEvent("d");
		network.createDependancy(a, b, c);
		network.createDependancy(b, d);
		network.createDependancy(c, d);
		network.finalizeStructure();
		
		Assert.assertFalse( network.isCondIndependant(b,c) );
		Assert.assertFalse( network.isCondIndependant(b,c,d) );
		Assert.assertTrue( network.isCondIndependant(a,c,a) );
		Assert.assertFalse( network.isCondIndependant(a,c,a,d) );
	}
}
