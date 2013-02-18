package org.encog.neural.neat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.genetic.GeneticError;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

public class NEATCODEC implements GeneticCODEC, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public MLMethod decode(final Genome genome) {
		final NEATGenome neatGenome = (NEATGenome) genome;
		final NEATPopulation pop = (NEATPopulation) neatGenome.getPopulation();
		final List<NEATNeuronGene> neuronsChromosome = neatGenome
				.getNeuronsChromosome();
		final List<NEATLinkGene> linksChromosome = neatGenome
				.getLinksChromosome();

		if (neuronsChromosome.get(0).getNeuronType() != NEATNeuronType.Bias) {
			throw new NeuralNetworkError(
					"The first neuron must be the bias neuron, this genome is invalid.");
		}

		final List<NEATLink> links = new ArrayList<NEATLink>();
		final ActivationFunction[] afs = new ActivationFunction[neuronsChromosome
				.size()];

		for (int i = 0; i < afs.length; i++) {
			afs[i] = neuronsChromosome.get(i).getActivationFunction();
		}

		final Map<Long, Integer> lookup = new HashMap<Long, Integer>();
		for (int i = 0; i < neuronsChromosome.size(); i++) {
			final NEATNeuronGene neuronGene = neuronsChromosome.get(i);
			lookup.put(neuronGene.getId(), i);
		}

		// loop over connections
		for (int i = 0; i < linksChromosome.size(); i++) {
			final NEATLinkGene linkGene = linksChromosome.get(i);
			if (linkGene.isEnabled()) {
				links.add(new NEATLink(lookup.get(linkGene.getFromNeuronID()),
						lookup.get(linkGene.getToNeuronID()), linkGene
								.getWeight()));
			}

		}

		Collections.sort(links);

		final NEATNetwork network = new NEATNetwork(neatGenome.getInputCount(),
				neatGenome.getOutputCount(), links, afs);

		network.setActivationCycles(pop.getActivationCycles());
		return network;
	}

	@Override
	public Genome encode(final MLMethod phenotype) {
		throw new GeneticError(
				"Encoding of a HyperNEAT network is not supported.");
	}

}
