package org.encog.ml.prg.train;

import java.util.Comparator;
import java.util.List;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.train.crossover.PrgCrossover;
import org.encog.ml.prg.train.crossover.SubtreeCrossover;
import org.encog.ml.prg.train.mutate.PrgMutate;
import org.encog.ml.prg.train.mutate.SubtreeMutation;
import org.encog.ml.prg.train.selection.PrgSelection;
import org.encog.ml.prg.train.selection.TournamentSelection;
import org.encog.ml.prg.train.sort.MaximizeScoreComp;
import org.encog.ml.prg.train.sort.MinimizeScoreComp;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public class PrgGenetic implements MLTrain {
	private final EncogProgramContext context;
	private final PrgPopulation population;
	private final CalculateScore scoreFunction;
	private PrgSelection selection;
	private PrgMutate mutation;
	private PrgCrossover crossover;
	private EncogProgram bestGenome = null;
	private Comparator<EncogProgram> compareScore;
	
	public PrgGenetic(PrgPopulation thePopulation, CalculateScore theScoreFunction) {
		this.population = thePopulation;
		this.context = population.getContext();
		this.scoreFunction = theScoreFunction;
		this.selection = new TournamentSelection(this,4);
		this.crossover = new SubtreeCrossover();
		this.mutation = new SubtreeMutation(thePopulation.getContext(),4);
		if( theScoreFunction.shouldMinimize()) {
			this.compareScore = new MinimizeScoreComp();
		} else {
			this.compareScore = new MaximizeScoreComp();
		}
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
	
	public PrgSelection getSelection() {
		return selection;
	}

	public void setSelection(PrgSelection selection) {
		this.selection = selection;
	}

	public PrgMutate getMutation() {
		return mutation;
	}

	public void setMutation(PrgMutate mutation) {
		this.mutation = mutation;
	}

	public PrgCrossover getCrossover() {
		return crossover;
	}

	public void setCrossover(PrgCrossover crossover) {
		this.crossover = crossover;
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
		for(int i=0;i<this.population.size();i++) {
			EncogProgram newPrg = null;
			EncogProgram parent1 = this.population.getMembers()[this.selection.performSelection()];
			
			if( Math.random()<0.9 ) {
				
				EncogProgram parent2 = this.population.getMembers()[this.selection.performSelection()];
				newPrg = this.crossover.crossover(parent1, parent2);
			} else {
				newPrg = this.mutation.mutate(parent1);
			}
			
			double score = this.scoreFunction.calculateScore(newPrg);
			if( !Double.isInfinite(score) && !Double.isNaN(score) ) {
				this.population.rewrite(newPrg);
				newPrg.setScore(score);
				int replaceIndex = this.selection.performAntiSelection();
				this.population.getMembers()[replaceIndex] = newPrg;
				evaluateBestGenome(newPrg);
			}
		}
		
	}

	@Override
	public double getError() {
		return this.bestGenome.getScore();
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

	public Comparator<EncogProgram> getCompareScore() {
		return compareScore;
	}

	public void setCompareScore(Comparator<EncogProgram> compareScore) {
		this.compareScore = compareScore;
	}
	
	public void createRandomPopulation(int maxDepth) {
		CreateRandom rnd = new CreateRandom(this.context,maxDepth);
		
		for(int i=0;i<this.population.getMaxPopulation();i++) {
			
			boolean done = false;
			EncogProgram prg = null;
			do {
				prg = rnd.generate();
				double score = this.scoreFunction.calculateScore(prg);
				if( !Double.isInfinite(score) && !Double.isNaN(score) ) {
					prg.setScore(score);
					done = true;
				}
			} while(!done);
			
			evaluateBestGenome(prg);
			this.population.rewrite(prg);
			this.population.getMembers()[i] = prg;
		}
	}
	
	private void evaluateBestGenome(EncogProgram prg) {
		if( this.bestGenome==null || isGenomeBetter(prg,this.bestGenome) ) {
			this.bestGenome = prg;
		}
	}
	
	public boolean isGenomeBetter(EncogProgram genome, EncogProgram betterThan) {
		return this.compareScore.compare(genome, betterThan)>0;
	}

	public EncogProgram getBestGenome() {
		return this.bestGenome;
	}
	
	
}
