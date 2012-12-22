package org.encog.ml.prg.train.crossover;

import java.util.Random;

import org.encog.ml.genetic.crossover.Crossover;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.prg.EncogProgram;

public class SubtreeCrossover implements Crossover {

	@Override
	public void performCrossover(Random rnd, Genome theParent1, Genome theParent2,
			Genome[] theOffspring, int index) {
		
		EncogProgram parent1 = (EncogProgram)theParent1;
		EncogProgram parent2 = (EncogProgram)theParent2;
		EncogProgram offspring = (EncogProgram)theOffspring[0];
		
		
		// find the position for the two cut-points, this is simply a node
		// position based on the
		// node count, it does not take int account node-sizes.
		int p1Position = rnd.nextInt(parent1.size());
		int p2Position = rnd.nextInt(parent2.size());

		// now convert these two positions into actual indexes into the code.
		int p1Index = parent1.findFrame(p1Position);
		int p2Index = parent2.findFrame(p2Position);

		// write to offspring
		offspring.copy(parent1);
		offspring.replaceNode(parent2, p2Index, p1Index);
		offspring.evaluate();
		
	}

	@Override
	public int offspringProduced() {
		return 1;
	}

}
