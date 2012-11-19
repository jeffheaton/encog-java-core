package org.encog.ml.prg.train.crossover;

import org.encog.ml.prg.EncogProgram;

public interface PrgCrossover {
	
	EncogProgram crossover(EncogProgram parent1, EncogProgram parent2);
}
