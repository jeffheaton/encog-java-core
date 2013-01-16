package org.encog.ml.ea.score.parallel;

import java.util.List;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.train.basic.BasicEA;

public class ParallelScoreTask implements Runnable {

	private final Genome genome;
	private final CalculateScore scoreFunction;
	private final List<AdjustScore> adjusters; 
	private final ParallelScore owner;
	
	public ParallelScoreTask(Genome genome, ParallelScore theOwner) {
		super();
		this.owner = theOwner;
		this.genome = genome;
		this.scoreFunction = theOwner.getScoreFunction();
		this.adjusters = theOwner.getAdjusters();
	}

	@Override
	public void run() {
		MLMethod phenotype = this.owner.getCodec().decode(this.genome);
		double score = this.scoreFunction.calculateScore(phenotype);
		genome.setScore(score);
		BasicEA.calculateScoreAdjustment(genome, adjusters);
	}

}
