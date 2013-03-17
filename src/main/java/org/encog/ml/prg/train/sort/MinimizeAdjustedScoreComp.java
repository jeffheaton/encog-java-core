package org.encog.ml.prg.train.sort;

import java.util.Comparator;

import org.encog.ml.prg.EncogProgram;

public class MinimizeAdjustedScoreComp implements Comparator<EncogProgram> {

	@Override
	public int compare(EncogProgram p1, EncogProgram p2) {
		return Double.compare(p1.getAdjustedScore(), p2.getAdjustedScore());
	}

}
