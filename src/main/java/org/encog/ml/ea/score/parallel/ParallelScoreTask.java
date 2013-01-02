package org.encog.ml.ea.score.parallel;

import java.util.List;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.score.CalculateGenomeScore;
import org.encog.ml.ea.train.basic.BasicEA;

public class ParallelScoreTask implements Runnable {

	private final Genome genome;
	private final CalculateGenomeScore scoreFunction;
	private final List<AdjustScore> adjusters; 
	
	public ParallelScoreTask(Genome genome, CalculateGenomeScore scoreFunction,
			List<AdjustScore> adjusters) {
		super();
		this.genome = genome;
		this.scoreFunction = scoreFunction;
		this.adjusters = adjusters;
	}

	@Override
	public void run() {
		double score = this.scoreFunction.calculateScore(genome);
		genome.setScore(score);
		BasicEA.calculateScoreAdjustment(genome, adjusters);
	}

}
