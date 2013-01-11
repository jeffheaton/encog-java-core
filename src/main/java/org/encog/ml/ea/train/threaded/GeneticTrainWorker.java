package org.encog.ml.ea.train.threaded;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.score.CalculateGenomeScore;
import org.encog.ml.prg.exception.EPLTooBig;

public class GeneticTrainWorker extends Thread implements Serializable {
	private final MultiThreadedEA owner;
	private AtomicBoolean done = new AtomicBoolean();
	private Genome[] tempProgram;
	private Random rnd;

	public GeneticTrainWorker(MultiThreadedEA theOwner) {
		this.owner = theOwner;
		this.rnd = this.owner.getRandomNumberFactory().factor();

		this.tempProgram = new Genome[this.owner.getOperators()
				.maxOffspring()];
		for (int i = 0; i < this.tempProgram.length; i++) {
			this.tempProgram[i] = this.owner.getPopulation().getGenomeFactory().factor();
		}

	}

	private void handleNewGenomes(int offspringCount) {
		for (int i = 0; i < offspringCount; i++) {
			CalculateGenomeScore scoreFunction = this.owner.getScoreFunction();
			this.tempProgram[i].decode();
			double score = scoreFunction.calculateScore(this.tempProgram[i]);
			if (!Double.isInfinite(score) && !Double.isNaN(score)) {
				// population.rewrite(this.tempProgram[0]);
				this.tempProgram[i].setScore(score);
				this.owner.addGenome(this.tempProgram, i, 1);
			}
		}
	}

	public void run() {
		Genome[] parents = new Genome[this.owner.getOperators()
				.maxParents()];

		try {
			this.done.set(false);

			for (;;) {
				EvolutionaryOperator opp = null;

				try {
					// choose an operator to use
					opp = this.owner.getOperators().pick(this.rnd);

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
					if (!this.owner.getParams()
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
