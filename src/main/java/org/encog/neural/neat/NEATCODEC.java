package org.encog.neural.neat;

import java.io.Serializable;
import java.util.Arrays;
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

	@Override
	public MLMethod decode(Genome genome) {
		NEATGenome neatGenome = (NEATGenome)genome;
		NEATPopulation pop = (NEATPopulation)neatGenome.getPopulation();
		List<NEATNeuronGene> neuronsChromosome = neatGenome.getNeuronsChromosome();
		List<NEATLinkGene> linksChromosome = neatGenome.getLinksChromosome();
		
		if( ((NEATNeuronGene)neuronsChromosome.get(0)).getNeuronType() != NEATNeuronType.Bias ) {
			throw new NeuralNetworkError("The first neuron must be the bias neuron, this genome is invalid.");
		}
		
		NEATLink[] links = new NEATLink[linksChromosome.size()];
		ActivationFunction[] afs = new ActivationFunction[neuronsChromosome.size()];
		
		for(int i=0;i<afs.length;i++) {
			afs[i] = neuronsChromosome.get(i).getActivationFunction();
		}
		
		Map<Long,Integer> lookup = new HashMap<Long,Integer>();
		for(int i=0;i<neuronsChromosome.size();i++) {
			NEATNeuronGene neuronGene = (NEATNeuronGene)neuronsChromosome.get(i);
			lookup.put(neuronGene.getId(), i);
		}
		
		// loop over connections
		for(int i=0;i<linksChromosome.size();i++) {
			NEATLinkGene linkGene = (NEATLinkGene)linksChromosome.get(i);
			links[i] = new NEATLink(
					lookup.get(linkGene.getFromNeuronID()),
					lookup.get(linkGene.getToNeuronID()),
					linkGene.getWeight());
			
		}
		
		Arrays.sort(links);
		
	    NEATNetwork network = new NEATNetwork(
	    		neatGenome.getInputCount(),
                neatGenome.getOutputCount(),
	    		links,	    		
                afs);
		
		
		network.setActivationCycles(pop.getActivationCycles());		
		return network;	
	}

	@Override
	public Genome encode(MLMethod phenotype) {
		throw new GeneticError("Encoding of a HyperNEAT network is not supported.");
	}

}
