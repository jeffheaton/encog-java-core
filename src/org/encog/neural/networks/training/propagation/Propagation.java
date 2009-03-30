package org.encog.neural.networks.training.propagation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.backpropagation.PropagationSynapse;
import org.encog.util.ErrorCalculation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Propagation extends BasicTraining {
	
	private PropagationMethod method;
	private double learningRate;
	private double momentum;
	private BasicNetwork network;
	private List<PropagationLayer> layers = new ArrayList<PropagationLayer>();
	
	private NeuralData fire;
	private final NeuralOutputHolder outputHolder = new NeuralOutputHolder();
	
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public Propagation(PropagationMethod method,BasicNetwork network, double learningRate, double momentum)
	{
		this.method = method;
		this.network = network;
		this.learningRate = learningRate;
		this.momentum = momentum;
		construct();
		this.method.init(this);
	}
	
	private void construct()
	{
		Layer layer = this.network.getOutputLayer();
		constructLayer(layer);
	}
	
	private Layer findTrunkBackward(Layer layer)
	{
		Collection<Synapse> synapses = this.network.getStructure().getPreviousSynapses(layer);
		
		for(Synapse synapse: synapses )
		{
			// only follow teachable backward links
			// if this is a truly recurrent neural network, this will cause an 
			// endless recurrsion loop.  Backprop can't be used for truly recurrent
			// networks.  It requires a non-teachable connection to break the loop.
			if(synapse.isTeachable())
				return synapse.getFromLayer();
		}
		
		return null;
	}
	
	private void constructLayer(Layer layer)
	{
		Collection<Synapse> prevSynapses = this.network.getStructure().getPreviousSynapses(layer);
		int previousNeuronCount = 0;
		
		// how many teachable neurons feed into here?
		for(Synapse synapse: prevSynapses)
		{
			if( synapse.isTeachable() )
			{
				previousNeuronCount+=synapse.getFromNeuronCount();
			}
		}
		
		// construct the layer
		PropagationLayer propagationLayer = new PropagationLayer(layer, previousNeuronCount, prevSynapses);
		this.layers.add(propagationLayer);
		if( logger.isDebugEnabled())
		{
			logger.debug("Adding layer to propagation: {}", layer);
		}
		
		// find next layer
		Layer prevLayer = findTrunkBackward(layer);
		
		// on to the next one
		if(prevLayer!=null)
		constructLayer(prevLayer);
	}
	
	private NeuralData forwardPass(NeuralData input) {
		if (logger.isDebugEnabled()) {
			logger.debug("Backpropagation forward pass");
		}
		this.outputHolder.getResult().clear();
		this.fire = network.compute(input, this.outputHolder);
		return this.fire;
	}
	
	public void iteration() {
		
		if (logger.isInfoEnabled()) {
			logger.info("Beginning backpropagation iteration");
		}

		preIteration();

		final ErrorCalculation errorCalculation = new ErrorCalculation();

		for (final NeuralDataPair pair : getTraining()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Backpropagation training on: input={},ideal={}",
						pair.getInput(), pair.getIdeal());
			}
			NeuralData actual = forwardPass(pair.getInput());
			errorCalculation.updateError(actual, pair.getIdeal());
			this.method.determineDeltas(pair.getIdeal());
		}
		learn();

		this.setError(errorCalculation.calculateRMS());

		postIteration();
		
	}
	
	/**
	 * Modify the weight matrix and thresholds based on the last call to
	 * calcError.
	 */
	public void learn() {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Propagation learning pass");
		}
		
	}

	public BasicNetwork getNetwork() {
		return this.network;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getMomentum() {
		return momentum;
	}

	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}

	public List<PropagationLayer> getLayers() {
		return layers;
	}

	public NeuralData getFire() {
		return fire;
	}
	
	
	
}
