package org.encog.ml.ea.opp;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;

public interface EvolutionaryOperator {
	/**
	 * Perform the evolutionary operation.  
	 * @param rnd A random number generator.
	 * @param parents The parents.
	 * @param parentIndex The index into the parents array.
	 * @param offspring The offspring.
	 * @param offspringIndex An index into the offspring array.
	 */
	void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex);
	
	/**
	 * @return The number of offspring produced by this type of crossover.
	 */
	int offspringProduced();
	
	/**
	 * @return The number of parents needed.
	 */
	int parentsNeeded();
}
