package org.encog.ml.genetic.sort;

import junit.framework.Assert;

import org.encog.Encog;
import org.encog.ml.ea.genome.BasicGenome;
import org.encog.ml.ea.sort.MaximizeScoreComp;
import org.encog.ml.genetic.genome.IntegerArrayGenome;
import org.junit.Test;

public class TestMaximizeScoreComp {
	@Test
	public void testCompare() {
		
		BasicGenome genome1 = new IntegerArrayGenome(1);
		genome1.setAdjustedScore(10);
		genome1.setScore(4);
		
		BasicGenome genome2 = new IntegerArrayGenome(1);
		genome2.setAdjustedScore(4);
		genome2.setScore(10);

		MaximizeScoreComp comp = new MaximizeScoreComp();
		
		Assert.assertTrue(comp.compare(genome1, genome2)>0);
	}
	
	@Test
	public void testIsBetterThan() {
		MaximizeScoreComp comp = new MaximizeScoreComp();
		Assert.assertFalse(comp.isBetterThan(10, 20));
	}
	
	@Test
	public void testShouldMinimize() {
		MaximizeScoreComp comp = new MaximizeScoreComp();
		Assert.assertFalse(comp.shouldMinimize());
	}
	
	@Test
	public void testApplyBonus() {
		MaximizeScoreComp comp = new MaximizeScoreComp();
		Assert.assertEquals(11, comp.applyBonus(10, 0.1), Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	@Test
	public void testApplyPenalty() {
		MaximizeScoreComp comp = new MaximizeScoreComp();
		Assert.assertEquals(9, comp.applyPenalty(10, 0.1), Encog.DEFAULT_DOUBLE_EQUAL);
	}
}
