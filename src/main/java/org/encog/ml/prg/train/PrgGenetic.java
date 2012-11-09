package org.encog.ml.prg.train;

import java.util.List;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public class PrgGenetic implements MLTrain {
	
	private final PrgPopulation population;
	private final CalculateScore scoreFunction;
	private double error = 0;
	
	public PrgGenetic(PrgPopulation thePopulation, CalculateScore theScoreFunction) {
		this.population = thePopulation;
		this.scoreFunction = theScoreFunction;
	}
	
	public PrgGenetic(PrgPopulation thePopulation, MLDataSet theTrainingSet) {
		this(thePopulation,new TrainingSetScore(theTrainingSet));
	}
	
	public PrgPopulation getPopulation() {
		return population;
	}

	public CalculateScore getScoreFunction() {
		return scoreFunction;
	}

	public void scorePopulation() {
		double totalScore = 0;
		int count = 0;
		for(EncogProgram prg: this.population.getMembers() ) {
			double score = this.scoreFunction.calculateScore(prg);
			count++;
			if( !Double.isNaN(score) && !Double.isInfinite(score) ) {
				totalScore+=score;
			}
		}
		this.error = totalScore / count;
	}

	@Override
	public TrainingImplementationType getImplementationType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTrainingDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MLDataSet getTraining() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void iteration() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getError() {
		return this.error;
	}

	@Override
	public void finishTraining() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void iteration(int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIteration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canContinue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addStrategy(Strategy strategy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MLMethod getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Strategy> getStrategies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setError(double error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIteration(int iteration) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
