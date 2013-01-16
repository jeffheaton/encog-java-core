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
import org.encog.neural.neat.training.NEATInnovationType;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.neat.training.innovation.Innovation;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.persist.PersistError;
import org.encog.util.csv.CSVFormat;

public class PersistNEATPopulation implements EncogPersistor {

	public static final String TYPE_CPPN = "cppn";
	
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
		Map<Integer, NEATSpecies> speciesMap = new HashMap<Integer, NEATSpecies>();
		Map<NEATSpecies, Integer> leaderMap = new HashMap<NEATSpecies, Integer>();
		Map<Integer, NEATGenome> genomeMap = new HashMap<Integer, NEATGenome>();
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
					ActivationFunction af = null;
					
					if( !cols.get(8).equalsIgnoreCase("null") ) {
						af = EncogFileSection.parseActivationFunction(cols.get(8));
					}
					
					innovation.setActivationFunction(af);
					result.getInnovations().add(innovation);
				}
			} else if (section.getSectionName().equals("NEAT-POPULATION")
					&& section.getSubSectionName().equals("SPECIES")) {
				for (String line : section.getLines()) {
					String[] cols = line.split(",");
					NEATSpecies species = new NEATSpecies();

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
						
						ActivationFunction af = EncogFileSection.parseActivationFunction(cols.get(2));
						neuronGene.setActivationFunction(af);
						
						neuronGene.setNeuronType(PersistNEATPopulation
								.stringToNeuronType(cols.get(3)));
						neuronGene.setEnabled(Integer.parseInt(cols.get(4))>0);
						neuronGene.setInnovationId(Integer.parseInt(cols.get(5)));
						neuronGene
								.setSplitX(CSVFormat.EG_FORMAT.parse(cols.get(6)));
						neuronGene
								.setSplitY(CSVFormat.EG_FORMAT.parse(cols.get(7)));
						lastGenome.getNeuronsChromosome().add(neuronGene);
					} else if (cols.get(0).equalsIgnoreCase("l")) {
						NEATLinkGene linkGene = new NEATLinkGene();
						linkGene.setId(Integer.parseInt(cols.get(1)));
						linkGene.setEnabled(Integer.parseInt(cols.get(2))>0);
						linkGene.setRecurrent(Integer.parseInt(cols.get(3))>0);
						linkGene.setFromNeuronID(Integer.parseInt(cols.get(4)));
						linkGene.setToNeuronID(Integer.parseInt(cols.get(5)));
						linkGene.setWeight(CSVFormat.EG_FORMAT.parse(cols.get(6)));
						linkGene.setInnovationId(Integer.parseInt(cols.get(7)));
						lastGenome.getLinksChromosome().add(linkGene);
					}
				}
			} else if (section.getSectionName().equals("NEAT-POPULATION")
					&& section.getSubSectionName().equals("CONFIG")) {
				Map<String, String> params = section.parseParams();
				
				String afStr = params.get(NEATPopulation.PROPERTY_NEAT_ACTIVATION);
				
				if( afStr.equalsIgnoreCase(TYPE_CPPN)) {
					HyperNEATGenome.buildCPPNActivationFunctions(result.getActivationFunctions());
				} else {
					result.setNEATActivationFunction(
							EncogFileSection.parseActivationFunction(params,NEATPopulation.PROPERTY_NEAT_ACTIVATION));
				}
				
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
				result.setActivationCycles(EncogFileSection.parseInt(params,
						NEATPopulation.PROPERTY_CYCLES));
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
			NEATSpecies species = speciesMap.get(speciesId);
			if (species != null) {
				species.getMembers().add(neatGenome);
			}
			neatGenome.setInputCount(result.getInputCount());
			neatGenome.setOutputCount(result.getOutputCount());
		}

		// set the species leader links
		for (NEATSpecies species : leaderMap.keySet()) {
			int leaderID = leaderMap.get(species);
			NEATGenome leader = genomeMap.get(leaderID);
			if( leader==null) {
				throw new PersistError("Unknown leader: genome #" + leader);
			}
			species.setLeader(leader);
			species.setPopulation(result);
		}
		
		// set factories
		if( result.isHyperNEAT() ) {
			result.setGenomeFactory(new FactorHyperNEATGenome());
			result.setCodec(new HyperNEATCODEC());
		} else {
			result.setGenomeFactory(new FactorNEATGenome());
			result.setCodec(new NEATCODEC());
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
		
		if( pop.isHyperNEAT() ) {
			out.writeProperty(NEATPopulation.PROPERTY_NEAT_ACTIVATION, TYPE_CPPN);
		} else {
			ActivationFunction af = pop.getActivationFunctions().getList().get(0).getObj();
			out.writeProperty(NEATPopulation.PROPERTY_NEAT_ACTIVATION, af);	
		}
		
		
		out.writeProperty(PersistConst.INPUT_COUNT, pop.getInputCount());
		out.writeProperty(PersistConst.OUTPUT_COUNT, pop.getOutputCount());
		out.writeProperty(NEATPopulation.PROPERTY_CYCLES, pop.getActivationCycles());
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
				if( neatInnovation.getActivationFunction()==null) {
					out.addColumn("null");
				} else {
					out.addColumn(neatInnovation.getActivationFunction());
				}
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

			for (NEATNeuronGene neatNeuronGene : neatGenome.getNeuronsChromosome()) {
				out.addColumn("n");
				out.addColumn(neatNeuronGene.getId());
				out.addColumn(neatNeuronGene.getActivationFunction());
				out.addColumn(PersistNEATPopulation
						.neuronTypeToString(neatNeuronGene.getNeuronType()));
				out.addColumn(neatNeuronGene.isEnabled());
				out.addColumn(neatNeuronGene.getInnovationId());
				out.addColumn(neatNeuronGene.getSplitX());
				out.addColumn(neatNeuronGene.getSplitY());
				out.writeLine();
			}
			for (NEATLinkGene neatLinkGene : neatGenome.getLinksChromosome()) {
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
		for (NEATSpecies species : pop.getSpecies()) {
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
