/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.ea.train;

import java.util.List;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.selection.SelectionOperator;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.sort.GenomeComparator;
import org.encog.ml.ea.species.Speciation;
import org.encog.ml.prg.train.GeneticTrainingParams;

/**
 * This interface defines the basic functionality of an Evolutionary Algorithm.
 * An evolutionary algorithm is one that applies operations to a population of
 * potential "solutions".
 */
public interface EvolutionaryAlgorithm {
	
	void calculateScore(Genome g);

	void setPopulation(Population thePopulation);

	Population getPopulation();

	void iteration();

	GeneticTrainingParams getParams();

	int getMaxIndividualSize();

	public SelectionOperator getSelection();

	public void setSelection(SelectionOperator selection);

	GenomeComparator getSelectionComparator();

	void setSelectionComparator(GenomeComparator selectionComparator);

	GenomeComparator getBestComparator();

	void setBestComparator(GenomeComparator bestComparator);

	List<AdjustScore> getScoreAdjusters();

	void addScoreAdjuster(AdjustScore scoreAdjust);

	CalculateScore getScoreFunction();

	GeneticCODEC getCODEC();

	int getIteration();
	
	int getMaxTries();

	boolean isValidationMode();

	Genome getBestGenome();

	double getError();

	void setSpeciation(Speciation originalNEATSpeciation);

	void finishTraining();

}
