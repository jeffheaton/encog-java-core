package org.encog.neural.freeform.training;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNetwork;
import org.encog.neural.freeform.FreeformNeuron;
import org.encog.neural.freeform.task.ConnectionTask;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public abstract class FreeformPropagationTraining extends BasicTraining implements Serializable {
	
	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	public static final double FLAT_SPOT_CONST = 0.1;
	private FreeformNetwork network;
	private MLDataSet training;
	private int iterationCount;
	private double error;
	private final Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>(); 
	private boolean fixFlatSopt = true;
	
	public FreeformPropagationTraining(final FreeformNetwork theNetwork, final MLDataSet theTraining) {
		super(TrainingImplementationType.Iterative);
		this.network = theNetwork;
		this.training = theTraining;		
	}
	
	@Override
	public TrainingImplementationType getImplementationType() {
		return TrainingImplementationType.Iterative;
	}

	@Override
	public boolean isTrainingDone() {
		return false;
	}

	@Override
	public MLDataSet getTraining() {
		return this.training;
	}
	
	private void calculateGradients() {
		ErrorCalculation errorCalc = new ErrorCalculation();
		this.visited.clear();
		
		for(MLDataPair pair: this.training) {
			MLData input = pair.getInput();
			MLData ideal = pair.getIdeal();
			MLData actual = this.network.compute(input);
			double sig = pair.getSignificance();
			
			errorCalc.updateError(actual.getData(), ideal.getData(),sig);
			
			for(int i=0;i<this.network.getOutputCount();i++) {
				double diff = (ideal.getData(i) - actual.getData(i))*sig;
				FreeformNeuron neuron = network.getOutputLayer().getNeurons().get(i);
				calculateOutputDelta(neuron, diff);
				calculateNeuronGradient(neuron);
			}
		}
		
		setError(errorCalc.calculate());
	}
	
	private void calculateNeuronGradient(FreeformNeuron toNeuron) {
						
		// Only calculate if layer has inputs, because we've already handled the output
		// neurons, this means a hidden layer.
		if( toNeuron.getInputSummation()!=null ) {					
			
			// between the layer deltas between toNeuron and the neurons that feed toNeuron.
			// also calculate all inbound gradeints to toNeuron
			for(FreeformConnection connection: toNeuron.getInputSummation().list()) {
				
				// calculate the gradient
				double gradient = connection.getSource().getActivation() * toNeuron.getTempTraining(0);
				connection.addTempTraining(0, gradient);

				// calculate the next layer delta
				FreeformNeuron fromNeuron = connection.getSource();
				double sum = 0;
				for(FreeformConnection toConnection : fromNeuron.getOutputs() ) {					
					sum+=toConnection.getTarget().getTempTraining(0) * toConnection.getWeight();					
				}
				double neuronOutput = fromNeuron.getActivation();
				double neuronSum = fromNeuron.getSum();
				double deriv = toNeuron.getInputSummation().getActivationFunction().derivativeFunction(neuronSum,neuronOutput);
				
				if( this.fixFlatSopt && toNeuron.getInputSummation().getActivationFunction() instanceof ActivationSigmoid ) {
					deriv+=FreeformPropagationTraining.FLAT_SPOT_CONST;
				}
				
				double layerDelta = sum * deriv;
				fromNeuron.setTempTraining(0, layerDelta);								
			}
			
			// recurse to the next level
			for(FreeformConnection connection: toNeuron.getInputSummation().list()) {
				FreeformNeuron fromNeuron = connection.getSource();
				calculateNeuronGradient(fromNeuron);
			}
			
			
			
		}
		
		
		
	}
	
	private void calculateOutputDelta(FreeformNeuron neuron, double diff) {
		double neuronOutput = neuron.getActivation();
		double neuronSum = neuron.getInputSummation().getSum();
		double deriv = neuron.getInputSummation().getActivationFunction().derivativeFunction(neuronSum,neuronOutput);
		if (this.fixFlatSopt
				&& neuron.getInputSummation().getActivationFunction() instanceof ActivationSigmoid) {
			deriv += FreeformPropagationTraining.FLAT_SPOT_CONST;
		}
		double layerDelta = deriv * diff;
		neuron.setTempTraining(0, layerDelta);
	}

	@Override
	public void iteration() {
		this.iterationCount++;
		calculateGradients();
		
		network.performConnectionTask(new ConnectionTask(){
			@Override
			public void task(FreeformConnection connection) {
				learnConnection(connection);
				connection.setTempTraining(0, 0);
			}
		});
	}

	@Override
	public double getError() {
		return this.error;
	}

	@Override
	public void finishTraining() {
		this.network.tempTrainingClear();		
	}

	@Override
	public void iteration(int count) {
		for(int i=0;i<count;i++) {
			this.iteration();
		}
		
	}

	@Override
	public int getIteration() {
		return this.iterationCount;
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public MLMethod getMethod() {
		return this.network;
	}

	@Override
	public void setError(double theError) {
		this.error = theError;
		
	}

	@Override
	public void setIteration(int iteration) {
		this.iterationCount = iteration;		
	}
	
	
	
	public boolean isFixFlatSopt() {
		return fixFlatSopt;
	}

	public void setFixFlatSopt(boolean fixFlatSopt) {
		this.fixFlatSopt = fixFlatSopt;
	}

	protected abstract void learnConnection(FreeformConnection connection);

}
