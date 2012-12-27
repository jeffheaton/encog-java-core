package org.encog.ml.ea.opp.selection;

import org.encog.ml.ea.train.GeneticAlgorithm;

public interface PrgSelection {
	int performSelection();
	int performAntiSelection();
	GeneticAlgorithm getTrainer();
}
