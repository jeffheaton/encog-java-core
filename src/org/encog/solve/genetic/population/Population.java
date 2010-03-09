package org.encog.solve.genetic.population;

import java.util.List;

import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.innovation.InnovationList;
import org.encog.solve.genetic.species.Species;

public interface Population {
	int getPopulationSize();
	void setPopulationSize(final int populationSize);
	void sort();
	List<Genome> getGenomes();
	Genome getBest();
	void add(Genome genome);
	long assignGenomeID();
	long assignSpeciesID();
	long assignInnovationID();
	long assignGeneID();
	int size();
	void clear();
	void addAll(List<? extends Genome> newPop);
	Genome get(int i);
	int getYoungBonusAgeThreshhold();
	void setYoungBonusAgeThreshhold(int youngBonusAgeThreshhold);
	double getYoungFitnessBonus();
	void setYoungFitnessBonus(double youngFitnessBonus);
	int getOldAgeThreshold();
	void setOldAgeThreshold(int oldAgeThreshold);
	double getOldAgePenalty();
	void setOldAgePenalty(double oldAgePenalty);
	double getSurvivalRate();
	void setSurvivalRate(double survivalRate);
	public List<Species> getSpecies();
	public InnovationList getInnovations();
	public void setInnovations(InnovationList innovations);
	
}
