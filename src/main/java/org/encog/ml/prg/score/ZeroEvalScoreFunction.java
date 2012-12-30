package org.encog.ml.prg.score;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.score.CalculateGenomeScore;
import org.encog.ml.prg.EncogProgram;

public class ZeroEvalScoreFunction implements CalculateGenomeScore, Serializable {

	@Override
	public double calculateScore(Genome genome) {
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

}
