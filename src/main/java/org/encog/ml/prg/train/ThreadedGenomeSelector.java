package org.encog.ml.prg.train;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public class ThreadedGenomeSelector implements Serializable {
	private final EvolutionaryAlgorithm owner;
	private final Set<Genome> used = new HashSet<Genome>();
	private final Random rnd;

	public ThreadedGenomeSelector(final EvolutionaryAlgorithm theOwner,
			final Random theRandom) {
		this.owner = theOwner;
		this.rnd = theRandom;
	}

	public Genome antiSelectGenome(final Species species) {
		Genome result = null;

		synchronized (this) {
			while (result == null) {
				final int selectedID = this.owner.getSelection()
						.performAntiSelection(this.rnd, species);
				final Genome potentialSelection = species.getMembers().get(
						selectedID);
				if (!this.used.contains(potentialSelection)) {
					this.used.add(potentialSelection);
					result = potentialSelection;
				}
			}
			return result;
		}
	}

	public void clear() {
		synchronized (this) {
			this.used.clear();
		}
	}

	public void releaseGenome(final Genome genome) {
		synchronized (this) {
			this.used.remove(genome);
		}
	}

	public Genome selectGenome(final Species species) {
		Genome result = null;

		synchronized (this) {
			while (result == null) {
				final int selectedID = this.owner.getSelection()
						.performSelection(this.rnd, species);
				final Genome potentialSelection = species.getMembers().get(
						selectedID);
				if (!this.used.contains(potentialSelection)) {
					this.used.add(potentialSelection);
					result = potentialSelection;
				}
			}
			return result;
		}
	}
}
