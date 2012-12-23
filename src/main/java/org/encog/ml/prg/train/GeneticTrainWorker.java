package org.encog.ml.prg.train;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.ml.genetic.evolutionary.EvolutionaryOperator;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.exception.EPLTooBig;
import org.encog.neural.networks.training.CalculateScore;

public class GeneticTrainWorker extends Thread {
	private final PrgGenetic owner;
	private AtomicBoolean done = new AtomicBoolean();
	private EncogProgram[] tempProgram;
	private Random rnd;

	public GeneticTrainWorker(PrgGenetic theOwner) {
		this.owner = theOwner;
		this.rnd = this.owner.getRandomNumberFactory().factor();

		this.tempProgram = new EncogProgram[this.owner.getOperators()
				.maxOffspring()];
		for (int i = 0; i < this.tempProgram.length; i++) {
			this.tempProgram[i] = this.owner.getPopulation().createProgram();
		}

	}

	private void handleNewGenomes(int offspringCount) {
		for (int i = 0; i < offspringCount; i++) {
			CalculateScore scoreFunction = this.owner.getScoreFunction();
			double score = scoreFunction.calculateScore(this.tempProgram[0]);
			if (!Double.isInfinite(score) && !Double.isNaN(score)) {
				// population.rewrite(this.tempProgram[0]);
				this.tempProgram[0].setScore(score);
				this.owner.addGenome(this.tempProgram, 0, 1);
			}
		}
	}

	public void run() {
		EncogProgram[] parents = new EncogProgram[this.owner.getOperators()
				.maxParents()];

		try {
			this.done.set(false);

			for (;;) {
				EvolutionaryOperator opp = null;

				try {
					// choose an operator to use
					opp = this.owner.getOperators().pickOperator(this.rnd);

					// select and lock parents
					for (int i = 0; i < opp.parentsNeeded(); i++) {
						parents[i] = this.owner.getSelector().selectGenome();
					}

					// perform the operation
					opp.performOperation(this.rnd, parents, 0,
							this.tempProgram, 0);

					// process of the offspring
					handleNewGenomes(opp.offspringProduced());
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
				} finally {
					// release parents
					if (opp != null) {
						for (int i = 0; i < opp.parentsNeeded(); i++) {
							this.owner.getSelector().releaseGenome(parents[i]);
						}
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
