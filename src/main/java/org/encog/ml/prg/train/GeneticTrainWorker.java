package org.encog.ml.prg.train;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.ml.genetic.crossover.Crossover;
import org.encog.ml.genetic.mutate.Mutate;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.exception.EPLTooBig;
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
		for (int i = 0; i < 1; i++) {
			this.tempProgram[i] = this.owner.getPopulation().createProgram();
		}

	}

	private void handleNewGenomes() {
		CalculateScore scoreFunction = this.owner.getScoreFunction();
		double score = scoreFunction.calculateScore(this.tempProgram[0]);
		if (!Double.isInfinite(score) && !Double.isNaN(score)) {
			// population.rewrite(this.tempProgram[0]);
			this.tempProgram[0].setScore(score);
			this.owner.addGenome(this.tempProgram, 0, 1);
		}
	}

	public void run() {

		try {
			GeneticTrainingParams params = this.owner.getContext().getParams();
			PrgPopulation population = this.owner.getPopulation();
			EncogProgram[] members = this.owner.getPopulation().getMembers();
			PrgSelection selection = this.owner.getSelection();
			Crossover crossover = this.owner.getCrossover();
			Mutate mutation = this.owner.getMutation();
			CalculateScore scoreFunction = this.owner.getScoreFunction();
			this.done.set(false);
			EncogProgram parent1 = null;
			EncogProgram parent2 = null;

			for (;;) {
		
				try {
					parent1 = this.owner.getSelector().selectGenome();
					
					if (this.rnd.nextDouble() < params
							.getCrossoverProbability()) {
						parent2 = this.owner.getSelector().selectGenome();
						
						crossover.performCrossover(this.rnd, parent1, parent2,
							this.tempProgram, 0);

						scoreFunction.calculateScore(this.tempProgram[0]);
						handleNewGenomes();
					}

					if (this.rnd.nextDouble() < params.getMutationProbability()) {
						mutation.performMutation(this.rnd, parent1, this.tempProgram, 0);
						scoreFunction.calculateScore(this.tempProgram[0]);
						handleNewGenomes();
					}
				} catch (EPLTooBig ex) {
					// This is fine, we discard this Genome. Ideally this won't
					// happen too often,
					// as we will use a scoring method that favors smaller
					// genomes.
				} catch (Throwable t) {
					if (!this.owner.getContext().getParams()
							.isIgnoreExceptions()) {
						this.owner.reportError(t);
						return;
					}
				}
				finally {
					if( parent1!=null ) {
						this.owner.getSelector().releaseGenome(parent1);
					}
					if( parent2!=null ) {
						this.owner.getSelector().releaseGenome(parent2);
					}
				}

				this.owner.notifyProgress();
				if (this.done.get()) {
					break;
				}
			}
		} catch (Throwable t) {
			this.owner.reportError(t);
		} finally {
			this.owner.signalDone();
		}
	}

	public void requestTerminate() {
		this.done.set(true);
		this.interrupt();
	}
}
