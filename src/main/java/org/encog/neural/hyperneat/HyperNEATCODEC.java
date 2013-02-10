package org.encog.neural.hyperneat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSteepenedSigmoid;
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

	private double minWeight = 0.2;
	private double maxWeight = 5.0;
	
	public MLMethod decode(NEATPopulation pop, Substrate substrate, Genome genome) {
		// obtain the CPPN
		NEATCODEC neatCodec = new NEATCODEC();
		NEATNetwork cppn = (NEATNetwork) neatCodec.decode(genome);

		List<NEATLink> linkList = new ArrayList<NEATLink>();
		
		ActivationFunction[] afs = new ActivationFunction[substrate
				.getNodeCount()];

		ActivationFunction af = new ActivationSteepenedSigmoid();
		// all activation functions are the same
		for (int i = 0; i < afs.length; i++) {
			afs[i] = af;
		}
		
		double c = this.maxWeight / (1.0 - this.minWeight);

		// now create the links
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
			
			double weight = output.getData(0);
			if(Math.abs(weight)>this.minWeight) {
				weight = (Math.abs(weight) - this.minWeight) * c * Math.signum(weight);
				linkList.add(new NEATLink(source.getId(), target.getId(),weight));
			}
			
			double biasWeight = output.getData(1);
			if( Math.abs(biasWeight)>this.minWeight) {
				biasWeight = (Math.abs(biasWeight) - this.minWeight) * c * Math.signum(biasWeight);
				linkList.add( new NEATLink(0, target.getId(), biasWeight));
			}
		}
		
		// check for invalid neural network
		if( linkList.size()==0 ) {
			return null;
		}
	
		Collections.sort(linkList);

		NEATNetwork network = new NEATNetwork(substrate.getInputCount(),
				substrate.getOutputCount(), linkList, afs);

		network.setActivationCycles(pop.getActivationCycles());
		return network;
		
	}
	
	@Override
	public MLMethod decode(Genome genome) {
		NEATPopulation pop = (NEATPopulation) genome.getPopulation();
		Substrate substrate = pop.getSubstrate();
		return decode(pop, substrate, genome);
	}

	@Override
	public Genome encode(MLMethod phenotype) {
		throw new GeneticError(
				"Encoding of a HyperNEAT network is not supported.");
	}

	/**
	 * @return the minWeight
	 */
	public double getMinWeight() {
		return minWeight;
	}

	/**
	 * @param minWeight the minWeight to set
	 */
	public void setMinWeight(double minWeight) {
		this.minWeight = minWeight;
	}

	/**
	 * @return the maxWeight
	 */
	public double getMaxWeight() {
		return maxWeight;
	}

	/**
	 * @param maxWeight the maxWeight to set
	 */
	public void setMaxWeight(double maxWeight) {
		this.maxWeight = maxWeight;
	}
	
	

}
