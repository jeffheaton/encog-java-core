package org.encog.neural.networks.training.propagation;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.strategy.SmartLearningRate;
import org.encog.neural.networks.training.strategy.SmartMomentum;
import org.encog.util.ErrorCalculation;
import org.encog.util.logging.DumpMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Propagation  extends BasicTraining implements LearningRate,
Momentum {
	/**
	 * The learning rate. This is the degree to which the deltas will affect the
	 * current network.
	 */
	private double learnRate;

	/**
	 * The momentum, this is the degree to which the previous training cycle
	 * affects the current one.
	 */
	private double momentum;

	/**
	 * THe network that is being trained.
	 */
	private final BasicNetwork network;

	private NeuralData fire;
	
	private final PropagationMethod method;
	
	private final List<PropagationLevel> levels = new ArrayList<PropagationLevel>();

	/**
	 * The logger to use.
	 */
	final Logger logger = LoggerFactory.getLogger(BasicNetwork.class);

	private final NeuralOutputHolder outputHolder = new NeuralOutputHolder();

	/**
	 * Construct a backpropagation trainer.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param learnRate
	 *            The rate at which the weight matrix will be adjusted based on
	 *            learning.
	 * @param momentum
	 *            The influence that previous iteration's training deltas will
	 *            have on the current iteration.
	 */
	public Propagation(final BasicNetwork network, final PropagationMethod method,
			final NeuralDataSet training, final double learnRate,
			final double momentum) {
		this.network = network;
		this.learnRate = learnRate;
		this.momentum = momentum;
		this.method = method;
		setTraining(training);
		construct();
	}



	/**
	 * Calculate the error for the recognition just done.
	 * 
	 * @param ideal
	 *            What the output neurons should have yielded.
	 */
	public void backwardPass(final NeuralData ideal) {

		// make sure that the input is of the correct size
		if (ideal.size() != this.network.getOutputLayer().getNeuronCount()) {
			throw new NeuralNetworkError(
					"Size mismatch: Can't calcError for ideal input size="
							+ ideal.size() + " for output layer size="
							+ this.network.getOutputLayer().getNeuronCount());
		}

		// log that we are performing a backward pass
		if (logger.isDebugEnabled()) {
			logger.debug("Backpropagation backward pass");
		}
		
		// calculate the initial deltas from the output layer
		calculateInitialDeltas(this.fire, ideal);
		
		// now work these errors backward through the neural network
		for(int i=0;i<this.levels.size()-1;i++)
		{
			PropagationLevel fromLevel = this.levels.get(i+1);
			PropagationLevel toLevel = this.levels.get(i);
			this.method.calculateError(this.outputHolder,fromLevel, toLevel);
		}
	}
	
	private void construct()
	{
		// get the output layer
		Layer outputLayer = this.network.getOutputLayer();
		
		// construct the level
		PropagationLevel level = new PropagationLevel(this,outputLayer);
		this.levels.add(level);
		
		// construct the other levels
		construct(level);
	}
	
	private void construct(final PropagationLevel current)
	{
		List<Synapse> previousSynapses = current.determinePreviousSynapses();
		
		// are there more levels to move onto?
		if( previousSynapses.size()>0 )
		{
			PropagationLevel prevLevel = new PropagationLevel(this,previousSynapses);
			this.levels.add(prevLevel);
		

			// continue backward
			construct(prevLevel);
		}
	}

	/**
	 * Get the current best neural network.
	 * 
	 * @return The current best neural network.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	private NeuralData forwardPass(NeuralData input) {
		if (logger.isDebugEnabled()) {
			logger.debug("Backpropagation forward pass");
		}
		this.outputHolder.getResult().clear();
		this.fire = network.compute(input, this.outputHolder);
		return this.fire;
	}

	/**
	 * Perform one iteration of training.
	 * 
	 * Note: if you get a StackOverflowError while training, then you have
	 * endless recurrant loops. Try inserting no trainable synapses on one side
	 * of the loop.
	 */
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
			backwardPass(pair.getIdeal());
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
			logger.debug("Backpropagation learning pass");
		}
		
		for(PropagationLevel level: this.levels)
		{
			level.learn();
		}
	}
	

	/**
	 * Calculate the error for the given ideal values.
	 * 
	 * @param ideal
	 *            Ideal output values.
	 */
	private PropagationLevel calculateInitialDeltas(final NeuralData actual,
			final NeuralData ideal) {
		
		// get the output layer
		Layer outputLayer = this.network.getOutputLayer();
		
		// construct the level
		PropagationLevel level = this.levels.get(0);
		
		// obtain the output for each output layer neuron
		for (int i = 0; i < outputLayer.getNeuronCount(); i++) {
			level.setDelta(i, actual.getData(i) );
		}

		// take the derivative of these outputs
		outputLayer.getActivationFunction().derivativeFunction(level.getDeltas());

		// multiply by the difference between the actual and idea
		for (int i = 0; i < outputLayer.getNeuronCount(); i++) {
			level.setDelta(i, level.getDelta(i)*(ideal.getData(i) - actual.getData(i)) );
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Initial deltas: " + DumpMatrix.dumpArray(level.getDeltas()));
		}
		
		return level;
	}

	public double getLearningRate() {
		return this.learnRate;
	}

	public void setLearningRate(double rate) {
		this.learnRate = rate;
	}

	public double getMomentum() {
		return this.momentum;
	}

	public void setMomentum(double m) {
		this.momentum = m;
	}

	public NeuralOutputHolder getOutputHolder() {
		return outputHolder;
	}
}
