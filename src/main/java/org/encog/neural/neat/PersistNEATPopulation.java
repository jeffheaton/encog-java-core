/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.neural.neat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.BasicSpecies;
import org.encog.ml.ea.species.Species;
import org.encog.neural.hyperneat.FactorHyperNEATGenome;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.hyperneat.HyperNEATGenome;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATInnovationList;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.util.csv.CSVFormat;

/**
 * Persist a NEAT or HyperNEAT network.
 * 
 * -----------------------------------------------------------------------------
 * http://www.cs.ucf.edu/~kstanley/ Encog's NEAT implementation was drawn from
 * the following three Journal Articles. For more complete BibTeX sources, see
 * NEATNetwork.java.
 * 
 * Evolving Neural Networks Through Augmenting Topologies
 * 
 * Generating Large-Scale Neural Networks Through Discovering Geometric
 * Regularities
 * 
 * Automatic feature selection in neuroevolution
 */
public class PersistNEATPopulation implements EncogPersistor {

	/**
	 * Type for the Compositional pattern-producing networks used by HyperNEAT.
	 */
	public static final String TYPE_CPPN = "cppn";

	/**
	 * Convert a NEATNeuronType enum to a string.
	 * @param t The type.
	 * @return The string type.
	 */
	public static String neuronTypeToString(final NEATNeuronType t) {
		switch (t) {
		case Bias:
			return "b";
		case Hidden:
			return "h";
		case Input:
			return "i";
		case None:
			return "n";
		case Output:
			return "o";
		default:
			return null;
		}
	}

	public static NEATNeuronType stringToNeuronType(final String t) {
		if (t.equals("b")) {
			return NEATNeuronType.Bias;
		} else if (t.equals("h")) {
			return NEATNeuronType.Hidden;
		} else if (t.equals("i")) {
			return NEATNeuronType.Input;
		} else if (t.equals("n")) {
			return NEATNeuronType.None;
		} else if (t.equals("o")) {
			return NEATNeuronType.Output;
		} else {
			return null;
		}
	}

	@Override
	public int getFileVersion() {
		return 1;
	}

	@Override
	public String getPersistClassString() {
		return NEATPopulation.class.getSimpleName();
	}

	@Override
	public Object read(final InputStream is) {
		long nextInnovationID = 0;
		long nextGeneID = 0;

		final NEATPopulation result = new NEATPopulation();
		final NEATInnovationList innovationList = new NEATInnovationList();
		innovationList.setPopulation(result);
		result.setInnovations(innovationList);
		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("NEAT-POPULATION")
					&& section.getSubSectionName().equals("INNOVATIONS")) {
				for (final String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					final NEATInnovation innovation = new NEATInnovation();
					final int innovationID = Integer.parseInt(cols.get(1));
					innovation.setInnovationID(innovationID);
					innovation.setNeuronID(Integer.parseInt(cols.get(2)));
					result.getInnovations().getInnovations()
							.put(cols.get(0), innovation);
					nextInnovationID = Math.max(nextInnovationID,
							innovationID + 1);
				}
			} else if (section.getSectionName().equals("NEAT-POPULATION")
					&& section.getSubSectionName().equals("SPECIES")) {
				NEATGenome lastGenome = null;
				BasicSpecies lastSpecies = null;

				for (final String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);

					if (cols.get(0).equalsIgnoreCase("s")) {
						lastSpecies = new BasicSpecies();
						lastSpecies.setPopulation(result);
						lastSpecies.setAge(Integer.parseInt(cols.get(1)));
						lastSpecies.setBestScore(CSVFormat.EG_FORMAT.parse(cols
								.get(2)));
						lastSpecies.setGensNoImprovement(Integer.parseInt(cols
								.get(3)));
						result.getSpecies().add(lastSpecies);
					} else if (cols.get(0).equalsIgnoreCase("g")) {
						final boolean isLeader = lastGenome == null;
						lastGenome = new NEATGenome();
						lastGenome.setInputCount(result.getInputCount());
						lastGenome.setOutputCount(result.getOutputCount());
						lastGenome.setSpecies(lastSpecies);
						lastGenome.setAdjustedScore(CSVFormat.EG_FORMAT
								.parse(cols.get(1)));
						lastGenome.setScore(CSVFormat.EG_FORMAT.parse(cols
								.get(2)));
						lastGenome.setBirthGeneration(Integer.parseInt(cols
								.get(3)));
						lastSpecies.add(lastGenome);
						if (isLeader) {
							lastSpecies.setLeader(lastGenome);
						}
					} else if (cols.get(0).equalsIgnoreCase("n")) {
						final NEATNeuronGene neuronGene = new NEATNeuronGene();
						final int geneID = Integer.parseInt(cols.get(1));
						neuronGene.setId(geneID);

						final ActivationFunction af = EncogFileSection
								.parseActivationFunction(cols.get(2));
						neuronGene.setActivationFunction(af);

						neuronGene.setNeuronType(PersistNEATPopulation
								.stringToNeuronType(cols.get(3)));
						neuronGene
								.setInnovationId(Integer.parseInt(cols.get(4)));
						lastGenome.getNeuronsChromosome().add(neuronGene);
						nextGeneID = Math.max(geneID + 1, nextGeneID);
					} else if (cols.get(0).equalsIgnoreCase("l")) {
						final NEATLinkGene linkGene = new NEATLinkGene();
						linkGene.setId(Integer.parseInt(cols.get(1)));
						linkGene.setEnabled(Integer.parseInt(cols.get(2)) > 0);
						linkGene.setFromNeuronID(Integer.parseInt(cols.get(3)));
						linkGene.setToNeuronID(Integer.parseInt(cols.get(4)));
						linkGene.setWeight(CSVFormat.EG_FORMAT.parse(cols
								.get(5)));
						linkGene.setInnovationId(Integer.parseInt(cols.get(6)));
						lastGenome.getLinksChromosome().add(linkGene);
					}
				}

			} else if (section.getSectionName().equals("NEAT-POPULATION")
					&& section.getSubSectionName().equals("CONFIG")) {
				final Map<String, String> params = section.parseParams();

				final String afStr = params
						.get(NEATPopulation.PROPERTY_NEAT_ACTIVATION);

				if (afStr.equalsIgnoreCase(PersistNEATPopulation.TYPE_CPPN)) {
					HyperNEATGenome.buildCPPNActivationFunctions(result
							.getActivationFunctions());
				} else {
					result.setNEATActivationFunction(EncogFileSection
							.parseActivationFunction(params,
									NEATPopulation.PROPERTY_NEAT_ACTIVATION));
				}

				result.setActivationCycles(EncogFileSection.parseInt(params,
						PersistConst.ACTIVATION_CYCLES));
				result.setInputCount(EncogFileSection.parseInt(params,
						PersistConst.INPUT_COUNT));
				result.setOutputCount(EncogFileSection.parseInt(params,
						PersistConst.OUTPUT_COUNT));
				result.setPopulationSize(EncogFileSection.parseInt(params,
						NEATPopulation.PROPERTY_POPULATION_SIZE));
				result.setSurvivalRate(EncogFileSection.parseDouble(params,
						NEATPopulation.PROPERTY_SURVIVAL_RATE));
				result.setActivationCycles(EncogFileSection.parseInt(params,
						NEATPopulation.PROPERTY_CYCLES));
			}
		}

		// set factories
		if (result.isHyperNEAT()) {
			result.setGenomeFactory(new FactorHyperNEATGenome());
			result.setCODEC(new HyperNEATCODEC());
		} else {
			result.setGenomeFactory(new FactorNEATGenome());
			result.setCODEC(new NEATCODEC());
		}

		// set the next ID's
		result.getInnovationIDGenerate().setCurrentID(nextInnovationID);
		result.getGeneIDGenerate().setCurrentID(nextGeneID);

		// find first genome, which should be the best genome
		if (result.getSpecies().size() > 0) {
			final Species species = result.getSpecies().get(0);
			if (species.getMembers().size() > 0) {
				result.setBestGenome(species.getMembers().get(0));
			}
		}

		return result;
	}

	@Override
	public void save(final OutputStream os, final Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final NEATPopulation pop = (NEATPopulation) obj;
		out.addSection("NEAT-POPULATION");
		out.addSubSection("CONFIG");
		out.writeProperty(PersistConst.ACTIVATION_CYCLES,
				pop.getActivationCycles());

		if (pop.isHyperNEAT()) {
			out.writeProperty(NEATPopulation.PROPERTY_NEAT_ACTIVATION,
					PersistNEATPopulation.TYPE_CPPN);
		} else {
			final ActivationFunction af = pop.getActivationFunctions()
					.getList().get(0).getObj();
			out.writeProperty(NEATPopulation.PROPERTY_NEAT_ACTIVATION, af);
		}

		out.writeProperty(PersistConst.INPUT_COUNT, pop.getInputCount());
		out.writeProperty(PersistConst.OUTPUT_COUNT, pop.getOutputCount());
		out.writeProperty(NEATPopulation.PROPERTY_CYCLES,
				pop.getActivationCycles());
		out.writeProperty(NEATPopulation.PROPERTY_POPULATION_SIZE,
				pop.getPopulationSize());
		out.writeProperty(NEATPopulation.PROPERTY_SURVIVAL_RATE,
				pop.getSurvivalRate());
		out.addSubSection("INNOVATIONS");
		if (pop.getInnovations() != null) {
			for (final String key : pop.getInnovations().getInnovations()
					.keySet()) {
				final NEATInnovation innovation = pop.getInnovations()
						.getInnovations().get(key);
				out.addColumn(key);
				out.addColumn(innovation.getInnovationID());
				out.addColumn(innovation.getNeuronID());
				out.writeLine();
			}
		}

		out.addSubSection("SPECIES");

		// make sure the best species goes first
		final Species bestSpecies = pop.determineBestSpecies();
		if (bestSpecies != null) {
			saveSpecies(out, bestSpecies);
		}

		// now write the other species, other than the best one
		for (final Species species : pop.getSpecies()) {
			if (species != bestSpecies) {
				saveSpecies(out, species);
			}
		}
		out.flush();
	}

	private void saveSpecies(final EncogWriteHelper out, final Species species) {
		out.addColumn("s");
		out.addColumn(species.getAge());
		out.addColumn(species.getBestScore());
		out.addColumn(species.getGensNoImprovement());
		out.writeLine();

		for (final Genome genome : species.getMembers()) {
			final NEATGenome neatGenome = (NEATGenome) genome;
			out.addColumn("g");
			out.addColumn(neatGenome.getAdjustedScore());
			out.addColumn(neatGenome.getScore());
			out.addColumn(neatGenome.getBirthGeneration());
			out.writeLine();

			for (final NEATNeuronGene neatNeuronGene : neatGenome
					.getNeuronsChromosome()) {
				out.addColumn("n");
				out.addColumn(neatNeuronGene.getId());
				out.addColumn(neatNeuronGene.getActivationFunction());
				out.addColumn(PersistNEATPopulation
						.neuronTypeToString(neatNeuronGene.getNeuronType()));
				out.addColumn(neatNeuronGene.getInnovationId());
				out.writeLine();
			}
			for (final NEATLinkGene neatLinkGene : neatGenome
					.getLinksChromosome()) {
				out.addColumn("l");
				out.addColumn(neatLinkGene.getId());
				out.addColumn(neatLinkGene.isEnabled());
				out.addColumn(neatLinkGene.getFromNeuronID());
				out.addColumn(neatLinkGene.getToNeuronID());
				out.addColumn(neatLinkGene.getWeight());
				out.addColumn(neatLinkGene.getInnovationId());
				out.writeLine();
			}

		}

	}
}
