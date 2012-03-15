/*
 * Encog(tm) Core v3.1 - Java Version
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

import org.encog.ml.genetic.genes.Gene;
import org.encog.ml.genetic.genome.Chromosome;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.innovation.Innovation;
import org.encog.ml.genetic.species.BasicSpecies;
import org.encog.ml.genetic.species.Species;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATInnovationList;
import org.encog.neural.neat.training.NEATInnovationType;
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

	@Override
	public String getPersistClassString() {
		return NEATPopulation.class.getSimpleName();
	}

	@Override
	public Object read(InputStream is) {
		NEATPopulation result = new NEATPopulation();
		NEATInnovationList innovationList = new NEATInnovationList();
		innovationList.setPopulation(result);
		result.setInnovations(innovationList);
		EncogReadHelper in = new EncogReadHelper(is);
		Map<Integer, Species> speciesMap = new HashMap<Integer, Species>();
		Map<Species, Integer> leaderMap = new HashMap<Species, Integer>();
		Map<Integer, Genome> genomeMap = new HashMap<Integer, Genome>();
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("NEAT-POPULATION")
					&& section.getSubSectionName().equals("INNOVATIONS")) {
				for (String line : section.getLines()) {
					List<String> cols = EncogFileSection.splitColumns(line);
					NEATInnovation innovation = new NEATInnovation();
					innovation.setInnovationID(Integer.parseInt(cols.get(0)));
					innovation.setInnovationType(PersistNEATPopulation
							.stringToInnovationType(cols.get(1)));
					innovation.setNeuronType(PersistNEATPopulation.stringToNeuronType(cols.get(2)));
					innovation.setSplitX(CSVFormat.EG_FORMAT.parse(cols.get(3)));
					innovation.setSplitY(CSVFormat.EG_FORMAT.parse(cols.get(4)));
					innovation.setNeuronID(Integer.parseInt(cols.get(5)));
					innovation.setFromNeuronID(Integer.parseInt(cols.get(6)));
					innovation.setToNeuronID(Integer.parseInt(cols.get(7)));
					result.getInnovations().add(innovation);
				}
			} else if (section.getSectionName().equals("NEAT-POPULATION")
					&& section.getSubSectionName().equals("SPECIES")) {
				for (String line : section.getLines()) {
					String[] cols = line.split(",");
					BasicSpecies species = new BasicSpecies();

					species.setSpeciesID(Integer.parseInt(cols[0]));
					species.setAge(Integer.parseInt(cols[1]));
					species.setBestScore(CSVFormat.EG_FORMAT.parse(cols[2]));
					species.setGensNoImprovement(Integer.parseInt(cols[3]));
					species.setSpawnsRequired(CSVFormat.EG_FORMAT
							.parse(cols[4]));
					species.setSpawnsRequired(CSVFormat.EG_FORMAT
							.parse(cols[5]));
					leaderMap.put(species, Integer.parseInt(cols[6]));
					result.getSpecies().add(species);
					speciesMap.put((int) species.getSpeciesID(), species);
				}
			} else if (section.getSectionName().equals("NEAT-POPULATION")
					&& section.getSubSectionName().equals("GENOMES")) {
				NEATGenome lastGenome = null;
				for (String line : section.getLines()) {
					List<String> cols = EncogFileSection.splitColumns(line);
					if (cols.get(0).equalsIgnoreCase("g") ) {
						lastGenome = new NEATGenome();
						lastGenome.setNeuronsChromosome(new Chromosome());
						lastGenome.setLinksChromosome(new Chromosome());
						lastGenome.getChromosomes().add(
								lastGenome.getNeuronsChromosome());
						lastGenome.getChromosomes().add(
								lastGenome.getLinksChromosome());
						lastGenome.setGenomeID(Integer.parseInt(cols.get(1)));
						lastGenome.setSpeciesID(Integer.parseInt(cols.get(2)));
						lastGenome.setAdjustedScore(CSVFormat.EG_FORMAT
								.parse(cols.get(3)));
						lastGenome.setAmountToSpawn(CSVFormat.EG_FORMAT
								.parse(cols.get(4)));
						lastGenome.setNetworkDepth(Integer.parseInt(cols.get(5)));
						lastGenome.setScore(CSVFormat.EG_FORMAT.parse(cols.get(6)));
						result.add(lastGenome);
						genomeMap.put((int) lastGenome.getGenomeID(),
								lastGenome);
					} else if (cols.get(0).equalsIgnoreCase("n") ) {
						NEATNeuronGene neuronGene = new NEATNeuronGene();
						neuronGene.setId(Integer.parseInt(cols.get(1)));
						neuronGene.setNeuronType(PersistNEATPopulation
								.stringToNeuronType(cols.get(2)));
						neuronGene.setEnabled(Integer.parseInt(cols.get(3))>0);
						neuronGene.setInnovationId(Integer.parseInt(cols.get(4)));
						neuronGene.setActivationResponse(CSVFormat.EG_FORMAT
								.parse(cols.get(5)));
						neuronGene
								.setSplitX(CSVFormat.EG_FORMAT.parse(cols.get(6)));
						neuronGene
								.setSplitY(CSVFormat.EG_FORMAT.parse(cols.get(7)));
						lastGenome.getNeurons().add(neuronGene);
					} else if (cols.get(0).equalsIgnoreCase("l")) {
						NEATLinkGene linkGene = new NEATLinkGene();
						linkGene.setId(Integer.parseInt(cols.get(1)));
						linkGene.setEnabled(Integer.parseInt(cols.get(2))>0);
						linkGene.setRecurrent(Integer.parseInt(cols.get(3))>0);
						linkGene.setFromNeuronID(Integer.parseInt(cols.get(4)));
						linkGene.setToNeuronID(Integer.parseInt(cols.get(5)));
						linkGene.setWeight(CSVFormat.EG_FORMAT.parse(cols.get(6)));
						linkGene.setInnovationId(Integer.parseInt(cols.get(7)));
						lastGenome.getLinks().add(linkGene);
					}
				}
			} else if (section.getSectionName().equals("NEAT-POPULATION")
					&& section.getSubSectionName().equals("CONFIG")) {
				Map<String, String> params = section.parseParams();
				
				result.setNeatActivationFunction(EncogFileSection.parseActivationFunction(params,NEATPopulation.PROPERTY_NEAT_ACTIVATION));
				result.setActivationCycles(EncogFileSection.parseInt(params, PersistConst.ACTIVATION_CYCLES));
				result.setInputCount(EncogFileSection.parseInt(params,
						PersistConst.INPUT_COUNT));
				result.setOutputCount(EncogFileSection.parseInt(params,
						PersistConst.OUTPUT_COUNT));
				result.setOldAgePenalty(EncogFileSection.parseDouble(params,
						NEATPopulation.PROPERTY_OLD_AGE_PENALTY));
				result.setOldAgeThreshold(EncogFileSection.parseInt(params,
						NEATPopulation.PROPERTY_OLD_AGE_THRESHOLD));
				result.setPopulationSize(EncogFileSection.parseInt(params,
						NEATPopulation.PROPERTY_POPULATION_SIZE));
				result.setSurvivalRate(EncogFileSection.parseDouble(params,
						NEATPopulation.PROPERTY_SURVIVAL_RATE));
				result.setYoungBonusAgeThreshhold(EncogFileSection.parseInt(
						params, NEATPopulation.PROPERTY_YOUNG_AGE_THRESHOLD));
				result.setYoungScoreBonus(EncogFileSection.parseDouble(params,
						NEATPopulation.PROPERTY_YOUNG_AGE_BONUS));
				result.getGenomeIDGenerate().setCurrentID(
						EncogFileSection.parseInt(params,
								NEATPopulation.PROPERTY_NEXT_GENOME_ID));
				result.getInnovationIDGenerate().setCurrentID(
						EncogFileSection.parseInt(params,
								NEATPopulation.PROPERTY_NEXT_INNOVATION_ID));
				result.getGeneIDGenerate().setCurrentID(
						EncogFileSection.parseInt(params,
								NEATPopulation.PROPERTY_NEXT_GENE_ID));
				result.getSpeciesIDGenerate().setCurrentID(
						EncogFileSection.parseInt(params,
								NEATPopulation.PROPERTY_NEXT_SPECIES_ID));
			}
		}

		// now link everything up

		// first put all the genomes into correct species
		for (Genome genome : result.getGenomes()) {
			NEATGenome neatGenome = (NEATGenome) genome;
			int speciesId = (int) neatGenome.getSpeciesID();
			Species species = speciesMap.get(speciesId);
			if (species != null) {
				species.getMembers().add(neatGenome);
			}
			neatGenome.setInputCount(result.getInputCount());
			neatGenome.setOutputCount(result.getOutputCount());
		}

		// set the species leader links
		for (Species species : leaderMap.keySet()) {
			int leaderID = leaderMap.get(species);
			Genome leader = genomeMap.get(leaderID);
			if( leader==null) {
				throw new PersistError("Unknown leader: genome #" + leader);
			}
			species.setLeader(leader);
			((BasicSpecies)species).setPopulation(result);
		}
		
		((NEATInnovationList)result.getInnovations()).init();

		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		NEATPopulation pop = (NEATPopulation) obj;
		out.addSection("NEAT-POPULATION");
		out.addSubSection("CONFIG");
		out.writeProperty(PersistConst.ACTIVATION_CYCLES, pop.getActivationCycles());
		out.writeProperty(NEATPopulation.PROPERTY_NEAT_ACTIVATION, pop.getNeatActivationFunction());
		out.writeProperty(PersistConst.INPUT_COUNT, pop.getInputCount());
		out.writeProperty(PersistConst.OUTPUT_COUNT, pop.getOutputCount());
		out.writeProperty(NEATPopulation.PROPERTY_OLD_AGE_PENALTY,
				pop.getOldAgePenalty());
		out.writeProperty(NEATPopulation.PROPERTY_OLD_AGE_THRESHOLD,
				pop.getOldAgeThreshold());
		out.writeProperty(NEATPopulation.PROPERTY_POPULATION_SIZE,
				pop.getPopulationSize());
		out.writeProperty(NEATPopulation.PROPERTY_SURVIVAL_RATE,
				pop.getSurvivalRate());
		out.writeProperty(NEATPopulation.PROPERTY_YOUNG_AGE_THRESHOLD,
				pop.getYoungBonusAgeThreshold());
		out.writeProperty(NEATPopulation.PROPERTY_YOUNG_AGE_BONUS,
				pop.getYoungScoreBonus());
		out.writeProperty(NEATPopulation.PROPERTY_NEXT_GENOME_ID, pop
				.getGenomeIDGenerate().getCurrentID());
		out.writeProperty(NEATPopulation.PROPERTY_NEXT_INNOVATION_ID, pop
				.getInnovationIDGenerate().getCurrentID());
		out.writeProperty(NEATPopulation.PROPERTY_NEXT_GENE_ID, pop
				.getGeneIDGenerate().getCurrentID());
		out.writeProperty(NEATPopulation.PROPERTY_NEXT_SPECIES_ID, pop
				.getSpeciesIDGenerate().getCurrentID());
		out.addSubSection("INNOVATIONS");
		if (pop.getInnovations() != null) {
			for (Innovation innovation : pop.getInnovations().getInnovations()) {
				NEATInnovation neatInnovation = (NEATInnovation) innovation;
				out.addColumn(neatInnovation.getInnovationID());
				out.addColumn(PersistNEATPopulation
						.innovationTypeToString(neatInnovation
								.getInnovationType()));
				out.addColumn(PersistNEATPopulation
						.neuronTypeToString(neatInnovation.getNeuronType()));
				out.addColumn(neatInnovation.getSplitX());
				out.addColumn(neatInnovation.getSplitY());
				out.addColumn(neatInnovation.getNeuronID());
				out.addColumn(neatInnovation.getFromNeuronID());
				out.addColumn(neatInnovation.getToNeuronID());
				out.writeLine();
			}
		}
		out.addSubSection("GENOMES");
		for (Genome genome : pop.getGenomes()) {
			NEATGenome neatGenome = (NEATGenome) genome;
			out.addColumn("g");
			out.addColumn(neatGenome.getGenomeID());
			out.addColumn(neatGenome.getSpeciesID());
			out.addColumn(neatGenome.getAdjustedScore());
			out.addColumn(neatGenome.getAmountToSpawn());
			out.addColumn(neatGenome.getNetworkDepth());
			out.addColumn(neatGenome.getScore());
			out.writeLine();

			for (Gene neuronGene : neatGenome.getNeurons().getGenes()) {
				NEATNeuronGene neatNeuronGene = (NEATNeuronGene) neuronGene;
				out.addColumn("n");
				out.addColumn(neatNeuronGene.getId());
				out.addColumn(PersistNEATPopulation
						.neuronTypeToString(neatNeuronGene.getNeuronType()));
				out.addColumn(neatNeuronGene.isEnabled());
				out.addColumn(neatNeuronGene.getInnovationId());
				out.addColumn(neatNeuronGene.getActivationResponse());
				out.addColumn(neatNeuronGene.getSplitX());
				out.addColumn(neatNeuronGene.getSplitY());
				out.writeLine();
			}
			for (Gene linkGene : neatGenome.getLinks().getGenes()) {
				NEATLinkGene neatLinkGene = (NEATLinkGene) linkGene;
				out.addColumn("l");
				out.addColumn(neatLinkGene.getId());
				out.addColumn(neatLinkGene.isEnabled());
				out.addColumn(neatLinkGene.isRecurrent());
				out.addColumn(neatLinkGene.getFromNeuronID());
				out.addColumn(neatLinkGene.getToNeuronID());
				out.addColumn(neatLinkGene.getWeight());
				out.addColumn(neatLinkGene.getInnovationId());
				out.writeLine();
			}
		}
		out.addSubSection("SPECIES");
		for (Species species : pop.getSpecies()) {
			out.addColumn(species.getSpeciesID());
			out.addColumn(species.getAge());
			out.addColumn(species.getBestScore());
			out.addColumn(species.getGensNoImprovement());
			out.addColumn(species.getNumToSpawn());
			out.addColumn(species.getSpawnsRequired());
			if( species.getLeader()==null ) {
				out.addColumn(-1);
			} else {
				if( !pop.getGenomes().contains(species.getLeader())) {
					throw new PersistError("Genome #" + species.getLeader().getGenomeID() + " is a leader, but not in the general population.");
				}
				out.addColumn(species.getLeader().getGenomeID());
			}
			out.writeLine();
		}
		out.flush();

	}

	@Override
	public int getFileVersion() {
		return 1;
	}

	public static String neuronTypeToString(NEATNeuronType t) {
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

	public static String innovationTypeToString(NEATInnovationType t) {
		switch (t) {
		case NewLink:
			return "l";
		case NewNeuron:
			return "n";
		default:
			return null;
		}
	}

	public static NEATInnovationType stringToInnovationType(String t) {
		if (t.equalsIgnoreCase("l")) {
			return NEATInnovationType.NewLink;
		} else if (t.equalsIgnoreCase("n")) {
			return NEATInnovationType.NewNeuron;
		} else {
			return null;
		}
	}

	public static NEATNeuronType stringToNeuronType(String t) {
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
}
