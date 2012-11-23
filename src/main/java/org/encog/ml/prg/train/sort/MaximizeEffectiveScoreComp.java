package org.encog.ml.prg.train.sort;

import java.util.Comparator;

import org.encog.ml.prg.EncogProgram;

public class MaximizeEffectiveScoreComp implements Comparator<EncogProgram> {

	@Override
	public int compare(EncogProgram p1, EncogProgram p2) {
		return Double.compare(p2.getEffectiveScore(), p1.getEffectiveScore());
	}

}
