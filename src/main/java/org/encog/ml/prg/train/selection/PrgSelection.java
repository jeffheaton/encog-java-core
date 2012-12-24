package org.encog.ml.prg.train.selection;

import org.encog.ml.genetic.GeneticAlgorithm;

public interface PrgSelection {
	int performSelection();
	int performAntiSelection();
	GeneticAlgorithm getTrainer();
}
