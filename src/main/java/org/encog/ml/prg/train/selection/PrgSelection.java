package org.encog.ml.prg.train.selection;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.PrgPopulation;

public interface PrgSelection {
	int performSelection();
	int performAntiSelection();
	PrgPopulation getPopulation();
}
