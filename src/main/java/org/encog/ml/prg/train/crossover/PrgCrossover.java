package org.encog.ml.prg.train.crossover;

import org.encog.ml.prg.EncogProgram;

public interface PrgCrossover {
	
	EncogProgram crossover(EncogProgram mother, EncogProgram father);
}
