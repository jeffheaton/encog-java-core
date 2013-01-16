package org.encog.neural.hyperneat;

import java.util.Arrays;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.genetic.GeneticError;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateLink;
import org.encog.neural.hyperneat.substrate.SubstrateNode;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;

public class HyperNEATCODEC implements GeneticCODEC {

	@Override
	public MLMethod decode(Genome genome) {
		// obtain the CPPN
		NEATCODEC neatCodec = new NEATCODEC();
		NEATNetwork cppn = (NEATNetwork) neatCodec.decode(genome);

		// create the phenotype
		NEATPopulation pop = (NEATPopulation) genome.getPopulation();
		Substrate substrate = pop.getSubstrate();

		NEATLink[] links = new NEATLink[substrate.getLinkCount() * 2];
		ActivationFunction[] afs = new ActivationFunction[substrate
				.getNodeCount()];

		// all activation functions are the same
		for (int i = 0; i < afs.length; i++) {
			afs[i] = pop.getActivationFunctions().pickFirst();
		}

		// now create the links
		int linkIndex = 0;
		for (SubstrateLink link : substrate.getLinks()) {
			SubstrateNode source = link.getSource();
			SubstrateNode target = link.getTarget();
			MLData input = new BasicMLData(cppn.getInputCount());
			int index = 0;
			for (double d : source.getLocation()) {
				input.setData(index++, d);
			}
			for (double d : target.getLocation()) {
				input.setData(index++, d);
			}
			MLData output = cppn.compute(input);
			links[linkIndex++] = new NEATLink(source.getId(), target.getId(),
					output.getData(0));
			links[linkIndex++] = new NEATLink(0, target.getId(),
					output.getData(1));
		}

		Arrays.sort(links);

		NEATNetwork network = new NEATNetwork(substrate.getInputCount(),
				substrate.getOutputCount(), links, afs);

		network.setActivationCycles(pop.getActivationCycles());
		return network;

	}

	@Override
	public Genome encode(MLMethod phenotype) {
		throw new GeneticError(
				"Encoding of a HyperNEAT network is not supported.");
	}

}
