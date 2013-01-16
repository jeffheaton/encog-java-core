package org.encog.ml.prg.score;

import java.io.Serializable;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.prg.EncogProgram;

public class ZeroEvalScoreFunction implements CalculateScore, Serializable {

	@Override
	public double calculateScore(MLMethod genome) {
		EncogProgram prg = (EncogProgram)genome;
		for(int i=0;i<prg.getVariables().size();i++) {
			prg.getVariables().getVariable(i).setValue(0);
		}
		prg.evaluate();
		
		return 0;
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

	@Override
	public boolean requireSingleThreaded() {
		return false;
	}

}
