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
package org.encog.neural.neat.training;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.CompoundOperator;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.ml.ea.sort.MinimizeAdjustedScoreComp;
import org.encog.ml.ea.sort.MinimizeScoreComp;
import org.encog.ml.ea.species.BasicSpecies;
import org.encog.ml.ea.train.species.SpeciesEA;
import org.encog.ml.genetic.GeneticError;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.opp.NEATCrossover;
import org.encog.neural.neat.training.opp.NEATMutateAddLink;
import org.encog.neural.neat.training.opp.NEATMutateAddNode;
import org.encog.neural.neat.training.opp.NEATMutateRemoveLink;
import org.encog.neural.neat.training.opp.NEATMutateWeights;
import org.encog.neural.neat.training.opp.links.MutatePerturbLinkWeight;
import org.encog.neural.neat.training.opp.links.MutateResetLinkWeight;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import org.encog.neural.networks.training.TrainingError;

/**
 * Implements NEAT genetic training.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATTraining extends SpeciesEA {

	/**
	 * The number of inputs.
	 */
	private final int inputCount;

	/**
	 * The number of output neurons.
	 */
	private final int outputCount;

	private int maxTries = 5;


	/**
	 * Construct a neat trainer with a new population. The new population is
	 * created from the specified parameters.
	 * 
	 * @param calculateScore
	 *            The score calculation object.
	 * @param inputCount
	 *            The input neuron count.
	 * @param outputCount
	 *            The output neuron count.
	 * @param populationSize
	 *            The population size.
	 */
	public NEATTraining(final CalculateScore calculateScore,
			final int inputCount, final int outputCount,
			final int populationSize) {
		super(new NEATPopulation(inputCount, outputCount, populationSize),
				calculateScore);

		getNEATPopulation().reset();
		this.inputCount = inputCount;
		this.outputCount = outputCount;

		setBestComparator(new MinimizeScoreComp());
		setSelectionComparator(new MinimizeAdjustedScoreComp());

		init();

	}

	/**
	 * Construct neat training with an existing population.
	 * 
	 * @param calculateScore
	 *            The score object to use.
	 * @param population
	 *            The population to use.
	 */
	public NEATTraining(final CalculateScore calculateScore,
			final NEATPopulation population) {
		super(population, calculateScore);

		if (population.getSpecies().size() < 1) {
			throw new TrainingError("Population has no species.");
		}
		
		BasicSpecies species = population.getSpecies().get(0);
		
		if ( species.getMembers().size() < 1) {
			throw new TrainingError("First NEAT species is empty");
		}

		final NEATGenome genome = (NEATGenome) species.getMembers().get(0);
		setPopulation(population);
		this.inputCount = genome.getInputCount();
		this.outputCount = genome.getOutputCount();
		init();
	}

	/**
	 * @return The innovations.
	 */
	public NEATInnovationList getInnovations() {
		return (NEATInnovationList) ((NEATPopulation) getPopulation())
				.getInnovations();
	}

	/**
	 * @return The input count.
	 */
	public int getInputCount() {
		return this.inputCount;
	}
	

	/**
	 * @return The number of output neurons.
	 */
	public int getOutputCount() {
		return this.outputCount;
	}

	/**
	 * setup for training.
	 */
	private void init() {
		this.setSpeciation( new OriginalNEATSpeciation());

		this.setSelection(new TruncationSelection(this,0.3));		
		CompoundOperator weightMutation = new CompoundOperator();
		weightMutation.getComponents().add(0.1125,new NEATMutateWeights(new SelectFixed(1),new MutatePerturbLinkWeight(0.02)));
		weightMutation.getComponents().add(0.1125,new NEATMutateWeights(new SelectFixed(2),new MutatePerturbLinkWeight(0.02)));
		weightMutation.getComponents().add(0.1125,new NEATMutateWeights(new SelectFixed(3),new MutatePerturbLinkWeight(0.02)));
		weightMutation.getComponents().add(0.1125,new NEATMutateWeights(new SelectProportion(0.02),new MutatePerturbLinkWeight(0.02)));
		weightMutation.getComponents().add(0.1125,new NEATMutateWeights(new SelectFixed(1),new MutatePerturbLinkWeight(1)));
		weightMutation.getComponents().add(0.1125,new NEATMutateWeights(new SelectFixed(2),new MutatePerturbLinkWeight(1)));
		weightMutation.getComponents().add(0.1125,new NEATMutateWeights(new SelectFixed(3),new MutatePerturbLinkWeight(1)));
		weightMutation.getComponents().add(0.1125,new NEATMutateWeights(new SelectProportion(0.02),new MutatePerturbLinkWeight(1)));
		weightMutation.getComponents().add(0.03,new NEATMutateWeights(new SelectFixed(1),new MutateResetLinkWeight()));
		weightMutation.getComponents().add(0.03,new NEATMutateWeights(new SelectFixed(2),new MutateResetLinkWeight()));
		weightMutation.getComponents().add(0.03,new NEATMutateWeights(new SelectFixed(3),new MutateResetLinkWeight()));		
		weightMutation.getComponents().add(0.01,new NEATMutateWeights(new SelectProportion(0.02),new MutateResetLinkWeight()));
		weightMutation.getComponents().finalizeStructure();
		
		this.setChampMutation(weightMutation);
		addOperation(0.5, new NEATCrossover());
		addOperation(0.494, weightMutation);
		addOperation(0.0005, new NEATMutateAddNode());
		addOperation(0.005, new NEATMutateAddLink());
		addOperation(0.0005, new NEATMutateRemoveLink());
		this.getOperators().finalizeStructure();

		if (this.getNEATPopulation().isHyperNEAT()) {
			setCODEC(new HyperNEATCODEC());
		} else {
			setCODEC(new NEATCODEC());
		}

		// check the population
		for (final Genome obj : getPopulation().getGenomes()) {
			if (!(obj instanceof NEATGenome)) {
				throw new TrainingError(
						"Population can only contain objects of NEATGenome.");
			}

			final NEATGenome neat = (NEATGenome) obj;

			if ((neat.getInputCount() != this.inputCount)
					|| (neat.getOutputCount() != this.outputCount)) {
				throw new TrainingError(
						"All NEATGenome's must have the same input and output sizes as the base network.");
			}
		}
	}





	public NEATPopulation getNEATPopulation() {
		return (NEATPopulation) getPopulation();
	}

	/**
	 * @return the maxTries
	 */
	public int getMaxTries() {
		return maxTries;
	}

	/**
	 * @param maxTries
	 *            the maxTries to set
	 */
	public void setMaxTries(int maxTries) {
		this.maxTries = maxTries;
	}
	
	public void dump(File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			dump(fos);
			fos.close();
		} catch(IOException ex) {
			throw new GeneticError(ex);
		}
	}
	
	public void dump(OutputStream os) {
		this.sortPopulation();
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(os));
				
		if( this.getBestGenome()!=null ) {
			out.println("Best genome: " + this.getBestGenome().toString());
		}
		
		
		out.println("Species");
		for(int i=0;i<this.getNEATPopulation().getSpecies().size();i++) {
			BasicSpecies species = this.getNEATPopulation().getSpecies().get(i);
			out.println("Species #" + i + ":" + species.toString());
		}
		
		out.println("Species Detail");
		for(int i=0;i<this.getNEATPopulation().getSpecies().size();i++) {
			BasicSpecies species = this.getNEATPopulation().getSpecies().get(i);
			out.println("Species #" + i + ":" + species.toString());
			out.println("Leader:" + species.getLeader()); 
			for(int j=0;j<species.getMembers().size();j++) {
				out.println("Species Member #" + j + ":" + species.getMembers().get(j));
			}
		}
		
		out.println("Population Dump");
		for(int i=0;i<this.getNEATPopulation().getGenomes().size();i++) {
			out.println("Genome #" + i + ":" + this.getNEATPopulation().getGenomes().get(i));
		}
		
		out.flush();
				
	}
}
