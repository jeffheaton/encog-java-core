package org.encog.ml.prg.train;

import java.io.Serializable;
import java.util.List;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.train.species.TrainEA;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.concurrency.MultiThreadable;

public class PrgGenetic extends TrainEA implements MLTrain, MultiThreadable, Serializable {
	private final EncogProgramContext context;

	public PrgGenetic(PrgPopulation thePopulation,
			CalculateScore theScoreFunction) {
		super( thePopulation, theScoreFunction);
		this.context = thePopulation.getContext();	
		this.setParams(thePopulation.getContext().getParams());
	}


	public PrgGenetic(PrgPopulation thePopulation, MLDataSet theTrainingSet) {
		this(thePopulation, new TrainingSetScore(theTrainingSet));
	}

	public PrgPopulation getPrgPopulation() {
		return (PrgPopulation)getPopulation();
	}

	/**
	 * @return the context
	 */
	public EncogProgramContext getContext() {
		return context;
	}
}
