package org.encog.ml.ea.opp.selection;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public interface PrgSelection {
	int performSelection();
	int performAntiSelection();
	EvolutionaryAlgorithm getTrainer();
}
