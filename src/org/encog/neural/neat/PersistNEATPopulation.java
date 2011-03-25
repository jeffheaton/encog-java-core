package org.encog.neural.neat;

import java.io.InputStream;
import java.io.OutputStream;

import org.encog.ml.genetic.genes.Gene;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.innovation.Innovation;
import org.encog.ml.genetic.species.Species;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATInnovationType;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.thermal.HopfieldNetwork;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.map.PersistConst;

public class PersistNEATPopulation implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return NEATPopulation.class.getSimpleName();
	}

	@Override
	public Object read(InputStream is) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		NEATPopulation pop = (NEATPopulation) obj;
		out.addSection("NEAT-POPULATION");
		out.addSubSection("CONFIG");
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
		out.writeProperty(NEATPopulation.PROPERTY_NEXT_GENE_ID, pop
				.getGeneIDGenerate().getCurrentID());
		out.writeProperty(NEATPopulation.PROPERTY_NEXT_INNOVATION_ID, pop
				.getInnovationIDGenerate().getCurrentID());
		out.writeProperty(NEATPopulation.PROPERTY_NEXT_GENE_ID, pop
				.getGeneIDGenerate().getCurrentID());
		out.writeProperty(NEATPopulation.PROPERTY_NEXT_SPECIES_ID, pop
				.getSpeciesIDGenerate().getCurrentID());
		out.addSubSection("INNOVATIONS");
		for (Innovation innovation : pop.getInnovations().getInnovations()) {
			NEATInnovation neatInnovation = (NEATInnovation) innovation;
			out.addColumn(neatInnovation.getInnovationID());
			out.addColumn(PersistNEATPopulation.innovationTypeToString(neatInnovation.getInnovationType()));
			out.addColumn(PersistNEATPopulation.neuronTypeToString(neatInnovation.getNeuronType()));
			out.addColumn(neatInnovation.getSplitX());
			out.addColumn(neatInnovation.getSplitY());
			out.addColumn(neatInnovation.getFromNeuronID());
			out.addColumn(neatInnovation.getToNeuronID());
			out.writeLine();
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
				out.addColumn(PersistNEATPopulation.neuronTypeToString(neatNeuronGene.getNeuronType()));
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
		for(Species species : pop.getSpecies()) {
			out.addColumn(species.getSpeciesID());
			out.addColumn(species.getAge());
			out.addColumn(species.getBestScore());
			out.addColumn(species.getGensNoImprovement());
			out.addColumn(species.getNumToSpawn());
			out.addColumn(species.getSpawnsRequired());
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
			return "n";
		case NewNeuron:
			return "l";
		default:
			return null;
		}
	}

}
