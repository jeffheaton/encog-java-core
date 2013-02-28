package org.encog.ml.ea.opp.selection;

import java.util.Random;

import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public interface PrgSelection {
	int performSelection(Random rnd, Species species);
	int performAntiSelection(Random rnd, Species species);
	EvolutionaryAlgorithm getTrainer();
}
