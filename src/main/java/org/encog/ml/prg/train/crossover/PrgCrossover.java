package org.encog.ml.prg.train.crossover;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;

public interface PrgCrossover {
	
	EncogProgram crossover(Random rnd, EncogProgram parent1, EncogProgram parent2);
}
