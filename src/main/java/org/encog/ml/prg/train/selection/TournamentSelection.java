package org.encog.ml.prg.train.selection;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.PrgGenetic;
import org.encog.ml.prg.train.PrgPopulation;

public class TournamentSelection implements PrgSelection {
	private PrgGenetic trainer;
	private int rounds;
	
	public TournamentSelection(PrgGenetic theTrainer, int theRounds) {
		this.trainer = theTrainer;
		this.rounds = theRounds;
	}

	


	public PrgGenetic getTrainer() {
		return trainer;
	}




	public void setTrainer(PrgGenetic trainer) {
		this.trainer = trainer;
	}




	public int getRounds() {
		return rounds;
	}



	public void setRounds(int rounds) {
		this.rounds = rounds;
	}



	@Override
	public int performSelection() {
		PrgPopulation population = trainer.getPopulation();
		int bestIndex = RangeRandomizer.randomInt(0, population.size()-1);
	    EncogProgram best = population.getMembers()[bestIndex];
	    
	    for ( int i = 0; i < this.rounds; i ++ ) {
	    	int competitorIndex = RangeRandomizer.randomInt(0, population.size()-1);
	      EncogProgram competitor = population.getMembers()[competitorIndex];
	      if ( this.trainer.isGenomeBetter(best, competitor) ) {
	        best = competitor;
	        bestIndex = competitorIndex;
	      }
	    }
	    return( bestIndex );
	}


	@Override
	public int performAntiSelection() {
		PrgPopulation population = trainer.getPopulation();
		int worstIndex = RangeRandomizer.randomInt(0, population.size()-1);
	    EncogProgram worst = population.getMembers()[worstIndex];
	    
	    for ( int i = 0; i < this.rounds; i ++ ) {
	    	int competitorIndex = RangeRandomizer.randomInt(0, population.size()-1);
	      EncogProgram competitor = population.getMembers()[competitorIndex];
	      if ( !this.trainer.isGenomeBetter(worst, competitor) ) {
	        worst = competitor;
	        worstIndex = competitorIndex;
	      }
	    }
	    return( worstIndex );
	}
	
	
}
