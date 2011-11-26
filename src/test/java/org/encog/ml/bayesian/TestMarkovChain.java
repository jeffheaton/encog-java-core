package org.encog.ml.bayesian;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.mathutil.probability.vars.RandomVariable;
import org.encog.ml.markov.MarkovChain;
import org.encog.ml.markov.training.FindTransitionProbabilities;


public class TestMarkovChain extends TestCase {
	
	public static void testMarkovTransition1() {
		MarkovChain chain = new MarkovChain();
		RandomVariable r = chain.addState("R");
		RandomVariable s = chain.addState("S");
		chain.finalizeStructure();
		FindTransitionProbabilities train = new FindTransitionProbabilities(chain);
		train.fromSingleLetterString("RSSSRSR");
				
		Assert.assertEquals(1.0, chain.getInitialState(r),4);
		Assert.assertEquals(0.0, chain.getInitialState(s),4);
		Assert.assertEquals(0.0, chain.getStateProbability(r, r),4);
		Assert.assertEquals(0.5, chain.getStateProbability(r, s),4);
		Assert.assertEquals(1.0, chain.getStateProbability(s, r),4);
		Assert.assertEquals(0.5, chain.getStateProbability(s, s),4);			
	}
	
	public static void testMarkovTransition2() {
		MarkovChain chain = new MarkovChain();
		RandomVariable r = chain.addState("R");
		RandomVariable s = chain.addState("S");
		chain.finalizeStructure();
		FindTransitionProbabilities train = new FindTransitionProbabilities(chain);
		train.fromSingleLetterString("SSSSSRSSSRR");
		
		Assert.assertEquals(0.0, chain.getInitialState(r),4);
		Assert.assertEquals(1.0, chain.getInitialState(s),4);
		Assert.assertEquals(0.5, chain.getStateProbability(r, r),4);
		Assert.assertEquals(0.25, chain.getStateProbability(r, s),4);
		Assert.assertEquals(0.5, chain.getStateProbability(s, r),4);
		Assert.assertEquals(0.75, chain.getStateProbability(s, s),4);
	}
	
	public static void testMarkovTransitionLaplace() {
		MarkovChain chain = new MarkovChain();
		RandomVariable r = chain.addState("R");
		RandomVariable s = chain.addState("S");
		chain.finalizeStructure();
		FindTransitionProbabilities train = new FindTransitionProbabilities(chain,1);
		train.fromSingleLetterString("RSSSS");
		
		Assert.assertEquals(0.6666, chain.getInitialState(r),4);
		Assert.assertEquals(0.3333, chain.getInitialState(s),4);
		Assert.assertEquals(0.3333, chain.getStateProbability(r, r),4);
		Assert.assertEquals(0.2, chain.getStateProbability(r, s),4);
		Assert.assertEquals(0.6667, chain.getStateProbability(s, r),4);
		Assert.assertEquals(0.8000, chain.getStateProbability(s, s),4);
	}
	
	public static void testProbability1() {
		MarkovChain chain = new MarkovChain();
		RandomVariable rState = chain.addState("R");
		RandomVariable sState = chain.addState("S");
		chain.finalizeStructure();
		chain.setStateProbability(rState,rState,0.6);
		chain.setStateProbability(sState, sState,0.8);
		chain.setStateProbability(sState, rState,0.2);
		chain.setStateProbability(rState, sState,0.4);
		chain.setInitialState(rState,1.0);
		chain.setInitialState(sState,0.0);
		Assert.assertEquals(1.0, chain.calculateProbability(rState,0),2 );
		Assert.assertEquals(0.0, chain.calculateProbability(sState,0), 2);
		Assert.assertEquals(0.6, chain.calculateProbability(rState,1), 2);
		Assert.assertEquals(0.4, chain.calculateProbability(sState,1), 2);
		Assert.assertEquals(0.44 ,chain.calculateProbability(rState,2), 2);
		Assert.assertEquals(0.376, chain.calculateProbability(rState,3), 2);
		Assert.assertEquals(0.3504, chain.calculateProbability(rState,4), 2);
	}
	
	public static void testProbability2() {
		MarkovChain chain = new MarkovChain();
		RandomVariable aState = chain.addState("A");
		RandomVariable bState = chain.addState("B");
		chain.finalizeStructure();
		chain.setStateProbability(aState,aState,0.5);
		chain.setStateProbability(bState, bState,0.0);
		chain.setStateProbability(aState, bState,0.5);
		chain.setStateProbability(bState, aState,1.0);
		chain.setInitialState(aState,1.0);
		chain.setInitialState(bState,0.0);

		Assert.assertEquals(1.0, chain.calculateProbability(aState,0),2 );
		Assert.assertEquals(0.5, chain.calculateProbability(aState,1), 2);
		Assert.assertEquals(0.75, chain.calculateProbability(aState,2), 2);

	}
	
}
