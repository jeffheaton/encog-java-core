package org.encog.ml.prg.train.crossover;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;

public class SubtreeCrossover implements PrgCrossover {
	
	/**
	 * 
	 */
	@Override
	public void crossover(Random rnd, EncogProgram parent1, EncogProgram parent2, EncogProgram[] offspring,int index, int offspringCount) {
		// find the position for the two cut-points, this is simply a node position based on the
		// node count, it does not take int account node-sizes.
		int p1Position = rnd.nextInt(parent1.size());
		int p2Position = rnd.nextInt(parent2.size());
		
		// now convert these two positions into actual indexes into the code.
		int p1Index = parent1.findFrame(p1Position);
		int p2Index = parent2.findFrame(p2Position);
		
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
