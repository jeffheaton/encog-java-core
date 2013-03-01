package org.encog.ml.prg.train;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public class ThreadedGenomeSelector implements Serializable {
	private EvolutionaryAlgorithm owner;
	private Set<Genome> used = new HashSet<Genome>();
	private Random rnd;

	public ThreadedGenomeSelector(EvolutionaryAlgorithm theOwner, Random theRandom) {
		this.owner = theOwner;
		this.rnd = theRandom;
	}

	public Genome selectGenome(Species species) {
		Genome result = null;

		synchronized (this) {
			while (result == null) {
				int selectedID = owner.getSelection().performSelection(this.rnd, species);
				Genome potentialSelection = (Genome)species.getMembers().get(selectedID);
				if (!used.contains(potentialSelection)) {
					used.add(potentialSelection);
					result = potentialSelection;
				}
			}
			return result;
		}
	}
	
	public Genome antiSelectGenome(Species species) {
		Genome result = null;

		synchronized (this) {
			while (result == null) {
				int selectedID = owner.getSelection().performAntiSelection(this.rnd, species);
				Genome potentialSelection = (Genome)species.getMembers().get(selectedID);
				if (!used.contains(potentialSelection)) {
					used.add(potentialSelection);
					result = potentialSelection;
				}
			}
			return result;
		}
	}
	
	public void releaseGenome(Genome genome) {
		synchronized(this) {
			this.used.remove(genome);
		}
	}
	
	public void clear() {
		synchronized(this) {
			this.used.clear();
		}
	}
}
