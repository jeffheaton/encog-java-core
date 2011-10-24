package org.encog.ml.bayesian;

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
		System.out.println( network.calculateParameterCount());
		System.out.println( network.toString());
	}
}
