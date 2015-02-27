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
package org.encog.ml.genetic.sort;

import junit.framework.Assert;

import org.encog.Encog;
import org.encog.ml.ea.genome.BasicGenome;
import org.encog.ml.ea.sort.MinimizeAdjustedScoreComp;
import org.encog.ml.genetic.genome.IntegerArrayGenome;
import org.junit.Test;

public class TestMinimizeAdjustedScoreComp {
	@Test
	public void testCompare() {
		
		BasicGenome genome1 = new IntegerArrayGenome(1);
		genome1.setAdjustedScore(10);
		genome1.setScore(4);
		
		BasicGenome genome2 = new IntegerArrayGenome(1);
		genome2.setAdjustedScore(4);
		genome2.setScore(10);

		MinimizeAdjustedScoreComp comp = new MinimizeAdjustedScoreComp();
		
		Assert.assertTrue(comp.compare(genome1, genome2)>0);
	}
	
	@Test
	public void testIsBetterThan() {
		MinimizeAdjustedScoreComp comp = new MinimizeAdjustedScoreComp();
		Assert.assertTrue(comp.isBetterThan(10, 20));
	}
	
	@Test
	public void testShouldMinimize() {
		MinimizeAdjustedScoreComp comp = new MinimizeAdjustedScoreComp();
		Assert.assertTrue(comp.shouldMinimize());
	}
	
	@Test
	public void testApplyBonus() {
		MinimizeAdjustedScoreComp comp = new MinimizeAdjustedScoreComp();
		Assert.assertEquals(9, comp.applyBonus(10, 0.1), Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	@Test
	public void testApplyPenalty() {
		MinimizeAdjustedScoreComp comp = new MinimizeAdjustedScoreComp();
		Assert.assertEquals(11, comp.applyPenalty(10, 0.1), Encog.DEFAULT_DOUBLE_EQUAL);
	}
}
