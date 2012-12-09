package org.encog.ml.prg.train;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.crossover.PrgCrossover;
import org.encog.ml.prg.train.mutate.PrgMutate;
import org.encog.ml.prg.train.selection.PrgSelection;
import org.encog.neural.networks.training.CalculateScore;

public class GeneticTrainWorker extends Thread {
	private final PrgGenetic owner;
	private AtomicBoolean done = new AtomicBoolean();
	private EncogProgram[] tempProgram;
	private Random rnd;
	
	public GeneticTrainWorker(PrgGenetic theOwner) {
		this.owner = theOwner;
		this.rnd = this.owner.getRandomNumberFactory().factor();
		
		this.tempProgram = new EncogProgram[1];
		for(int i=0;i<1;i++) {
			this.tempProgram[i] = this.owner.getPopulation().createProgram();
		}
		
	}
	
	public void run() {
		
		try {
		PrgPopulation population = this.owner.getPopulation();
		EncogProgram[] members = this.owner.getPopulation().getMembers();
		PrgSelection selection = this.owner.getSelection();
		PrgCrossover crossover = this.owner.getCrossover();
		PrgMutate mutation = this.owner.getMutation();
		CalculateScore scoreFunction = this.owner.getScoreFunction();
		this.done.set(false);
		
		for(;;) {
			EncogProgram parent1 = members[selection.performSelection()];
			parent1.validate();
			
			if( this.rnd.nextDouble()<0.9 ) {
				
				EncogProgram parent2 = members[selection.performSelection()];
				parent2.validate();
				//crossover.crossover(this.rnd, parent1, parent2, this.tempProgram,0,1);
				//scoreFunction.calculateScore(this.tempProgram[0]);
			} else {
				mutation.mutate(this.rnd, parent1, this.tempProgram, 0, 1);
				scoreFunction.calculateScore(this.tempProgram[0]);
			}
			
			/*double score = scoreFunction.calculateScore(this.tempProgram[0]);
			if( !Double.isInfinite(score) && !Double.isNaN(score) ) {
				//population.rewrite(this.tempProgram[0]);
				//this.tempProgram[0].setScore(score);
				//tempProgram[0].validate();
				//this.owner.addGenome(this.tempProgram,0,1);
				
				if( this.done.get() ) {
					break;
				}
			}*/
		}} catch(Throwable t) {
			this.owner.reportError(t);
		}
	}
	
	public void requestTerminate() {
		this.done.set(true);
	}
}
