package org.encog.solve.genetic.mutate;

import org.encog.solve.genetic.genome.Chromosome;

public interface Mutate {
	
	void performMutation(Chromosome chromosome);

}
