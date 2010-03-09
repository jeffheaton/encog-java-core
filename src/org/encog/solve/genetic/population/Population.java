/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */
package org.encog.solve.genetic.population;

import java.util.List;

import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.innovation.InnovationList;
import org.encog.solve.genetic.species.Species;

/**
 * Defines a population of genomes.
 */
public interface Population {
	void add(Genome genome);

	void addAll(List<? extends Genome> newPop);

	long assignGeneID();

	long assignGenomeID();

	long assignInnovationID();

	long assignSpeciesID();

	void clear();

	Genome get(int i);

	Genome getBest();

	List<Genome> getGenomes();

	public InnovationList getInnovations();

	double getOldAgePenalty();

	int getOldAgeThreshold();

	int getPopulationSize();

	public List<Species> getSpecies();

	double getSurvivalRate();

	int getYoungBonusAgeThreshhold();

	double getYoungFitnessBonus();

	public void setInnovations(InnovationList innovations);

	void setOldAgePenalty(double oldAgePenalty);

	void setOldAgeThreshold(int oldAgeThreshold);

	void setPopulationSize(final int populationSize);

	void setSurvivalRate(double survivalRate);

	void setYoungBonusAgeThreshhold(int youngBonusAgeThreshhold);

	void setYoungFitnessBonus(double youngFitnessBonus);

	int size();

	void sort();

}
