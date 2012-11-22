package org.encog.ml.prg.train;

import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.crossover.PrgCrossover;
import org.encog.ml.prg.train.mutate.PrgMutate;
import org.encog.ml.prg.train.selection.PrgSelection;
import org.encog.neural.networks.training.CalculateScore;

public class GeneticTrainWorker extends Thread {
	private final PrgGenetic owner;
	private AtomicBoolean done = new AtomicBoolean();
	
	public GeneticTrainWorker(PrgGenetic theOwner) {
		this.owner = theOwner;
	}
	
	public void run() {
		
		PrgPopulation population = this.owner.getPopulation();
		EncogProgram[] members = this.owner.getPopulation().getMembers();
		PrgSelection selection = this.owner.getSelection();
		PrgCrossover crossover = this.owner.getCrossover();
		PrgMutate mutation = this.owner.getMutation();
		CalculateScore scoreFunction = this.owner.getScoreFunction();
		this.done.set(false);
		
		for(;;) {
			EncogProgram newPrg = null;
			EncogProgram parent1 = members[selection.performSelection()];
			
			if( Math.random()<0.9 ) {
				
				EncogProgram parent2 = members[selection.performSelection()];
				newPrg = crossover.crossover(parent1, parent2);
			} else {
				newPrg = mutation.mutate(parent1);
			}
			
			double score = scoreFunction.calculateScore(newPrg);
			if( !Double.isInfinite(score) && !Double.isNaN(score) ) {
				population.rewrite(newPrg);
				newPrg.setScore(score);
				this.owner.addGenome(newPrg);
				
				if( this.done.get() ) {
					break;
				}
			}
		}
	}
	
	public void requestTerminate() {
		this.done.set(true);
	}
}
