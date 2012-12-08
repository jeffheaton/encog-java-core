package org.encog.ml.prg.train.crossover;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;

public class SubtreeCrossover implements PrgCrossover {
	
	/**
	 * 
	 */
	@Override
	public void crossover(Random rnd, EncogProgram parent1, EncogProgram parent2, EncogProgram[] offspring,int index, int offspringCount) {
		int p1Index = parent1.findFrame(rnd.nextInt(parent1.size()));
		int p2Index = parent2.findFrame(rnd.nextInt(parent2.size()));
		
		for(int i=0;i<offspringCount;i++) {			
			try {
				offspring[i].copy(parent1);			
				offspring[i].replaceNode(parent2, p2Index, p1Index);
				offspring[i].evaluate();
			} catch(Throwable t) {
				System.out.println("Stop");
				for(;;) {
					try {
					offspring[i].copy(parent1);			
					offspring[i].replaceNode(parent2, p2Index, p1Index);
					} catch(Throwable t2) {
						t2.printStackTrace();
					}
				}
			}
		}
	}



}
