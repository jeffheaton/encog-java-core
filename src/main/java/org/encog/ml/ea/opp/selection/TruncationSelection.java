package org.encog.ml.ea.opp.selection;

import java.util.Random;

import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public class TruncationSelection implements PrgSelection {

	private EvolutionaryAlgorithm trainer;
	private double percent;
	
	public TruncationSelection(EvolutionaryAlgorithm theTrainer, double thePercent) {
		this.trainer = theTrainer;
		this.percent = thePercent;
	}
	
	@Override
	public int performSelection(Random rnd, Species species) {
		int top = Math.max(species.getMembers().size(), 1);
		int result = rnd.nextInt(top);
		return result;
	}

	@Override
	public int performAntiSelection(Random rnd, Species species) {
		return species.getMembers().size() - performSelection(rnd,species);
	}
	
	@Override
	public EvolutionaryAlgorithm getTrainer() {
		return this.trainer;
	}

}
