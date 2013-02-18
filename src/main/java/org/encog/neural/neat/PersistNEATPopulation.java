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
package org.encog.neural.neat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.ea.genome.Genome;
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
import org.encog.persist.PersistError;
import org.encog.util.csv.CSVFormat;

public class PersistNEATPopulation implements EncogPersistor {

	public static final String TYPE_CPPN = "cppn";

	public static String neuronTypeToString(final NEATNeuronType t) {
		switch (t) {
		case Bias:
			return ("b");
		case Hidden:
			return ("h");
		case Input:
			return ("i");
		case None:
			return ("n");
		case Output:
			return ("o");
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
		long nextGenomeID = 0;
		long nextInnovationID = 0;
		long nextGeneID = 0;
		long nextSpeciesID = 0;

		final NEATPopulation result = new NEATPopulation();
		final NEATInnovationList innovationList = new NEATInnovationList();
		innovationList.setPopulation(result);
		result.setInnovations(innovationList);
		final EncogReadHelper in = new EncogReadHelper(is);
		final Map<Integer, NEATSpecies> speciesMap = new HashMap<Integer, NEATSpecies>();
		final Map<NEATSpecies, Integer> leaderMap = new HashMap<NEATSpecies, Integer>();
		final Map<Integer, NEATGenome> genomeMap = new HashMap<Integer, NEATGenome>();
		final Map<NEATGenome, Integer> genomeSpeciesMap = new HashMap<NEATGenome, Integer>();
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
				for (final String line : section.getLines()) {
					final String[] cols = line.split(",");
					final NEATSpecies species = new NEATSpecies();
					final int speciesID = Integer.parseInt(cols[0]);
					species.setSpeciesID(speciesID);
					species.setAge(Integer.parseInt(cols[1]));
					species.setBestScore(CSVFormat.EG_FORMAT.parse(cols[2]));
					species.setGensNoImprovement(Integer.parseInt(cols[3]));
					leaderMap.put(species, Integer.parseInt(cols[4]));
					result.getSpecies().add(species);
					speciesMap.put((int) species.getSpeciesID(), species);
					nextSpeciesID = Math.max(nextSpeciesID, speciesID + 1);
				}
			} else if (section.getSectionName().equals("NEAT-POPULATION")
					&& section.getSubSectionName().equals("GENOMES")) {
				NEATGenome lastGenome = null;
				for (final String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					if (cols.get(0).equalsIgnoreCase("g")) {
						lastGenome = new NEATGenome();
						final long genomeID = Integer.parseInt(cols.get(1));
						lastGenome.setGenomeID(genomeID);
						genomeSpeciesMap.put(lastGenome,
								Integer.parseInt(cols.get(2)));
						lastGenome.setAdjustedScore(CSVFormat.EG_FORMAT
								.parse(cols.get(3)));
						lastGenome.setScore(CSVFormat.EG_FORMAT.parse(cols
								.get(4)));
						lastGenome.setBirthGeneration(Integer.parseInt(cols
								.get(5)));
						result.add(lastGenome);
						genomeMap.put((int) lastGenome.getGenomeID(),
								lastGenome);
						nextGenomeID = Math.max(nextGenomeID, genomeID + 1);
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

		// now link everything up

		// first put all the genomes into correct species
		for (final Genome genome : result.getGenomes()) {
			final NEATGenome neatGenome = (NEATGenome) genome;
			final int speciesId = genomeSpeciesMap.get(neatGenome);
			final NEATSpecies species = speciesMap.get(speciesId);
			if (species != null) {
				species.getMembers().add(neatGenome);
			}
			neatGenome.setInputCount(result.getInputCount());
			neatGenome.setOutputCount(result.getOutputCount());
		}

		// set the species leader links
		for (final NEATSpecies species : leaderMap.keySet()) {
			final int leaderID = leaderMap.get(species);
			final NEATGenome leader = genomeMap.get(leaderID);
			if (leader == null) {
				throw new PersistError("Unknown leader: genome #" + leader);
			}
			species.setLeader(leader);
			species.setPopulation(result);
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
		result.getGenomeIDGenerate().setCurrentID(nextGenomeID);
		result.getInnovationIDGenerate().setCurrentID(nextInnovationID);
		result.getGeneIDGenerate().setCurrentID(nextGeneID);
		result.getSpeciesIDGenerate().setCurrentID(nextSpeciesID);

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
		out.addSubSection("GENOMES");
		for (final Genome genome : pop.getGenomes()) {
			final NEATGenome neatGenome = (NEATGenome) genome;
			out.addColumn("g");
			out.addColumn(neatGenome.getGenomeID());
			out.addColumn((neatGenome.getSpecies() == null) ? 0 : neatGenome
					.getSpecies().getSpeciesID());
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
		out.addSubSection("SPECIES");
		for (final NEATSpecies species : pop.getSpecies()) {
			out.addColumn(species.getSpeciesID());
			out.addColumn(species.getAge());
			out.addColumn(species.getBestScore());
			out.addColumn(species.getGensNoImprovement());
			if (species.getLeader() == null) {
				out.addColumn(-1);
			} else {
				if (!pop.getGenomes().contains(species.getLeader())) {
					throw new PersistError(
							"Genome #"
									+ species.getLeader().getGenomeID()
									+ " is a leader, but not in the general population.");
				}
				out.addColumn(species.getLeader().getGenomeID());
			}
			out.writeLine();
		}
		out.flush();

	}
}
