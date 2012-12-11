package org.encog.ml.prg.train;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.mathutil.randomize.RandomChoice;
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
			PrgCrossover crossover = this.owner.getCrossover();
			PrgMutate mutation = this.owner.getMutation();
			CalculateScore scoreFunction = this.owner.getScoreFunction();
			this.done.set(false);

			for (;;) {
				EncogProgram parent1 = members[selection.performSelection()];

				if (this.rnd.nextDouble() < params.getCrossoverProbability()) {
					EncogProgram parent2 = members[selection.performSelection()];
					parent2.validate();
					crossover.crossover(this.rnd, parent1, parent2,
							this.tempProgram, 0, 1);
					scoreFunction.calculateScore(this.tempProgram[0]);
					handleNewGenomes();
				}

				if (this.rnd.nextDouble() < params.getMutationProbability()) {
					mutation.mutate(this.rnd, parent1, this.tempProgram, 0, 1);
					scoreFunction.calculateScore(this.tempProgram[0]);
					handleNewGenomes();
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
	}
}
