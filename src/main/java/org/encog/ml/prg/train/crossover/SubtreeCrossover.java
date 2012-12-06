package org.encog.ml.prg.train.crossover;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;

public class SubtreeCrossover implements PrgCrossover {

	/**
	 * 
	 */
	@Override
	public void crossover(Random rnd, EncogProgram parent1, EncogProgram parent2, EncogProgram[] offspring,int index, int offspringCount) {
		int p1Index = rnd.nextInt(parent1.size());
		int p2Index = rnd.nextInt(parent2.size());

		for(int i=0;i<offspringCount;i++) {
			offspring[i].copy(parent1);			
			offspring[i].replaceNode(parent2, p2Index, p1Index);
		}
	}



}
