package org.encog.ml.prg.train.selection;

import org.encog.ml.prg.train.PrgGenetic;

public interface PrgSelection {
	int performSelection();
	int performAntiSelection();
	PrgGenetic getTrainer();
}
