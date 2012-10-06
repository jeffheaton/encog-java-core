package org.encog.neural.freeform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.BasicML;
import org.encog.ml.MLClassification;
import org.encog.ml.MLContext;
import org.encog.ml.MLEncodable;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.MLResettable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.freeform.basic.BasicActivationSummationFactory;
import org.encog.neural.freeform.basic.BasicFreeformConnectionFactory;
import org.encog.neural.freeform.basic.BasicFreeformLayerFactory;
import org.encog.neural.freeform.basic.BasicFreeformNeuronFactory;
import org.encog.neural.freeform.factory.FreeformConnectionFactory;
import org.encog.neural.freeform.factory.FreeformLayerFactory;
import org.encog.neural.freeform.factory.FreeformNeuronFactory;
import org.encog.neural.freeform.factory.InputSummationFactory;
import org.encog.neural.freeform.task.ConnectionTask;
import org.encog.neural.freeform.task.NeuronTask;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.EngineArray;
import org.encog.util.obj.ObjectCloner;
import org.encog.util.simple.EncogUtility;

public class FreeformNetwork extends BasicML implements MLContext, Cloneable,
MLRegression, MLEncodable, MLResettable, MLClassification, MLError {
	
	private FreeformLayer inputLayer;
	private FreeformLayer outputLayer;
	private FreeformConnectionFactory connectionFactory = new BasicFreeformConnectionFactory();
	private FreeformLayerFactory layerFactory = new BasicFreeformLayerFactory();
	private FreeformNeuronFactory neuronFactory = new BasicFreeformNeuronFactory();
	private InputSummationFactory summationFactory = new BasicActivationSummationFactory();
	
	public FreeformNetwork() {	
	}
	
	public FreeformLayer createInputLayer(final int neuronCount) {
		if( neuronCount<1 ) {
			throw new FreeformNetworkError("Input layer must have at least one neuron.");
		}
		this.inputLayer = createLayer(neuronCount);
		return this.inputLayer;
	}
	
	public FreeformLayer createOutputLayer(final int neuronCount) {
		if( neuronCount<1 ) {
			throw new FreeformNetworkError("Output layer must have at least one neuron.");
		}
		this.outputLayer = createLayer(neuronCount);
		return this.outputLayer;
	}
	
	public FreeformLayer createLayer(final int neuronCount)
	{
		if( neuronCount<1 ) {
			throw new FreeformNetworkError("Layer must have at least one neuron.");
		}
		
		FreeformLayer result = layerFactory.factor();
		
		// Add the neurons for this layer
		for(int i=0;i<neuronCount;i++) {
			result.add(this.neuronFactory.factorRegular(null));
		}
		
		return result;
	}
		
	public FreeformNetwork(BasicNetwork network) {
		
		if( network.getLayerCount()<2 ) {
			throw new FreeformNetworkError("The BasicNetwork must have at least two layers to be converted.");
		}
		
		// handle each layer
		FreeformLayer previousLayer = null;
		FreeformLayer currentLayer;
		
		for(int currentLayerIndex = 0; currentLayerIndex<network.getLayerCount();currentLayerIndex++) {
			// create the layer
			currentLayer = this.layerFactory.factor();
			
			// Is this the input layer?
			if( this.inputLayer == null) {
				this.inputLayer = currentLayer;
			}
			
			// Add the neurons for this layer
			for(int i=0;i<network.getLayerNeuronCount(currentLayerIndex);i++) {
				// obtain the summation object.
				InputSummation summation = null;
				
				if( previousLayer!=null ) {
					summation = this.summationFactory.factor(network.getActivation(currentLayerIndex));
				}
				
				// add the new neuron
				currentLayer.add(neuronFactory.factorRegular(summation));
			}
			
			// Fully connect this layer to previous
			if( previousLayer!=null ) {				
				connectLayersFromBasic(
						network, 
						currentLayerIndex-1,
						previousLayer,
						currentLayerIndex,
						currentLayer,
						currentLayerIndex,
						false);			
			}
			
			// Add the bias neuron
			// The bias is added after connections so it has no inputs
			if( network.isLayerBiased(currentLayerIndex) ) {
				FreeformNeuron biasNeuron = this.neuronFactory.factorRegular(null);
				biasNeuron.setBias(true);
				biasNeuron.setActivation(network.getLayerBiasActivation(currentLayerIndex));
				currentLayer.add(biasNeuron);
			}
						
			// update previous layer
			previousLayer = currentLayer;
			currentLayer = null;
		}
		
		// finally, set the output layer.
		this.outputLayer = previousLayer;
	}
	
	public FreeformLayer createContext(FreeformLayer source, FreeformLayer target) {
		double biasActivation = 0.0;
		ActivationFunction activatonFunction = null;
		
		if( source.getNeurons().get(0).getOutputs().size()<1 ) {
			throw new FreeformNetworkError("A layer cannot have a context layer connected if there are no other outbound connections from the source layer.  Please connect the source layer somewhere else first.");
		}
		
		activatonFunction = source.getNeurons().get(0).getInputSummation().getActivationFunction();
		
		// first create the context layer
		FreeformLayer result = this.layerFactory.factor();
		
		for(int i=0;i<source.size();i++) {
			FreeformNeuron neuron = source.getNeurons().get(i);
			if( neuron.isBias() ) {
				FreeformNeuron biasNeuron = this.neuronFactory.factorRegular(null);
				biasNeuron.setBias(true);
				biasNeuron.setActivation(neuron.getActivation());
				result.add(biasNeuron);
			} else {
				FreeformNeuron contextNeuron = this.neuronFactory.factorContext(neuron);
				result.add(contextNeuron);
			}
		}
		
		// now connect the context layer to the target layer
		
		connectLayers(result,target,activatonFunction,biasActivation,false);
		
		return result;
	}

	public void connectLayers(
			FreeformLayer source, 
			FreeformLayer target, 
			ActivationFunction theActivationFunction, 
			double biasActivation, 
			boolean isRecurrent) {
				
		// create bias, if requested
		if( biasActivation> Encog.DEFAULT_DOUBLE_EQUAL ) {
			// does the source already have a bias?
			if( source.hasBias() ) {
				throw new FreeformNetworkError("The source layer already has a bias neuron, you cannot create a second.");
			}
			FreeformNeuron biasNeuron = this.neuronFactory.factorRegular(null);
			biasNeuron.setActivation(biasActivation);
			biasNeuron.setBias(true);
			source.add(biasNeuron);
		}
		
		// create connections
		for(FreeformNeuron targetNeuron: target.getNeurons()) {
			// create the summation for the target
			InputSummation summation = targetNeuron.getInputSummation();
			
			// do not create a second input summation
			if( summation==null ) {
				summation = this.summationFactory.factor(theActivationFunction);
				targetNeuron.setInputSummation(summation);
			}
			
			// connect the source neurons to the target neuron
			for(FreeformNeuron sourceNeuron: source.getNeurons()) {				
				FreeformConnection connection = this.connectionFactory.factor(sourceNeuron,targetNeuron);
				sourceNeuron.addOutput(connection);
				targetNeuron.addInput(connection);
			}	
		}
	}
	
	public void connectLayers(FreeformLayer source, FreeformLayer target) {
		connectLayers(source,target,new ActivationTANH(),1.0,false);
	}
	
	public void ConnectLayers(FreeformLayer source, 
			FreeformLayer target, 
			ActivationFunction theActivationFunction) {
		connectLayers(source,target,theActivationFunction,1.0,false);
	}
	
	private void connectLayersFromBasic(BasicNetwork network, 
			int fromLayerIdx,
			FreeformLayer source,
			int sourceIdx,
			FreeformLayer target,
			int targetIdx,
			boolean isRecurrent) {
		
		for(int targetNeuronIdx = 0; targetNeuronIdx < target.size(); targetNeuronIdx++ ) {
			for(int sourceNeuronIdx = 0; sourceNeuronIdx < source.size(); sourceNeuronIdx++ ) {
				FreeformNeuron sourceNeuron = source.getNeurons().get(sourceNeuronIdx);
				FreeformNeuron targetNeuron = target.getNeurons().get(targetNeuronIdx);
				
				// neurons with no input (i.e. bias neurons)
				if( targetNeuron.getInputSummation()==null ) {
					continue;
				}
				
				FreeformConnection connection = this.connectionFactory.factor(sourceNeuron,targetNeuron);
				sourceNeuron.addOutput(connection);
				targetNeuron.addInput(connection);
				double weight = network.getWeight(fromLayerIdx, sourceNeuronIdx, targetNeuronIdx);
				connection.setWeight(weight);
			}	
		}		
	}

	@Override
	public int getInputCount() {
		return this.inputLayer.sizeNonBias();
	}

	@Override
	public int getOutputCount() {
		return this.outputLayer.sizeNonBias();
	}

	@Override
	public double calculateError(MLDataSet data) {
		return EncogUtility.calculateRegressionError(this, data);
	}

	@Override
	public int classify(MLData input) {
		final MLData output = compute(input);
		return EngineArray.maxIndex(output.getData());
	}

	@Override
	public void reset() {
		reset((int)(System.currentTimeMillis()%Integer.MAX_VALUE));		
	}

	@Override
	public void reset(int seed) {
		final ConsistentRandomizer randomizer = new ConsistentRandomizer(-1,1, seed);
		
		performConnectionTask(new ConnectionTask() {
			@Override
			public void task(FreeformConnection connection) {
				connection.setWeight(randomizer.nextDouble());
			}
		});
	}

	@Override
	public int encodedArrayLength() {
		int result = 0;
		Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>();
		List<FreeformNeuron> queue = new ArrayList<FreeformNeuron>();
		
		// first copy outputs to queue
		for(FreeformNeuron neuron: this.outputLayer.getNeurons()) {
			queue.add(neuron);
		}
		
		while( queue.size()>0 ) {
			// pop a neuron off the queue
			FreeformNeuron neuron = queue.get(0);
			queue.remove(0);
			visited.add(neuron);
			
			// find anymore neurons and add them to the queue.
			if( neuron.getInputSummation()!=null ) {
				for( FreeformConnection connection : neuron.getInputSummation().list()) {
					result++;
					FreeformNeuron nextNeuron = connection.getSource();
					if( !visited.contains(nextNeuron) ) {
						queue.add(nextNeuron);
					}
				}
			}
		}

		return result;
	}

	@Override
	public void encodeToArray(double[] encoded) {
		int index = 0;
		Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>();
		List<FreeformNeuron> queue = new ArrayList<FreeformNeuron>();
		
		// first copy outputs to queue
		for(FreeformNeuron neuron: this.outputLayer.getNeurons()) {
			queue.add(neuron);
		}
		
		while( queue.size()>0 ) {
			// pop a neuron off the queue
			FreeformNeuron neuron = queue.get(0);
			queue.remove(0);
			visited.add(neuron);
			
			// find anymore neurons and add them to the queue.
			if( neuron.getInputSummation()!=null ) {
				for( FreeformConnection connection : neuron.getInputSummation().list()) {
					encoded[index++] = connection.getWeight();
					FreeformNeuron nextNeuron = connection.getSource();
					if( !visited.contains(nextNeuron) ) {
						queue.add(nextNeuron);
					}
				}
			}
		}
		
	}

	@Override
	public void decodeFromArray(double[] encoded) {
		int index = 0;
		Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>();
		List<FreeformNeuron> queue = new ArrayList<FreeformNeuron>();
		
		// first copy outputs to queue
		for(FreeformNeuron neuron: this.outputLayer.getNeurons()) {
			queue.add(neuron);
		}
		
		while( queue.size()>0 ) {
			// pop a neuron off the queue
			FreeformNeuron neuron = queue.get(0);
			queue.remove(0);
			visited.add(neuron);
			
			// find anymore neurons and add them to the queue.
			if( neuron.getInputSummation()!=null ) {
				for( FreeformConnection connection : neuron.getInputSummation().list()) {
					connection.setWeight(encoded[index++]);
					FreeformNeuron nextNeuron = connection.getSource();
					if( !visited.contains(nextNeuron) ) {
						queue.add(nextNeuron);
					}
				}
			}
		}
	}

	@Override
	public MLData compute(MLData input) {
		
		// Allocate result
		MLData result = new BasicMLData(this.outputLayer.size());
		
		// Copy the input
		for(int i=0;i<input.size();i++) {
			this.inputLayer.setActivation(i,input.getData(i));
		}
		
		// Request calculation of outputs
		for(int i=0;i<this.outputLayer.size();i++) {
			FreeformNeuron outputNeuron = this.outputLayer.getNeurons().get(i);
			outputNeuron.performCalculation();
			result.setData(i,outputNeuron.getActivation());
		}
		
		updateContext();
		
		return result;
	}

	@Override
	public void clearContext() {
		performNeuronTask(new NeuronTask(){
			@Override
			public void task(FreeformNeuron neuron) {
				if( neuron instanceof FreeformContextNeuron ) {
					neuron.setActivation(0);
				}
			}});
	}
	
	public void updateContext() {
		performNeuronTask(new NeuronTask(){
			@Override
			public void task(FreeformNeuron neuron) {
				neuron.updateContext();
			}});
	}
	
	public void performNeuronTask(NeuronTask task) {
		Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>();
		
		for(FreeformNeuron neuron: this.outputLayer.getNeurons()) {
			performNeuronTask(visited,neuron,task);
		}
	}
	
	private void performNeuronTask(Set<FreeformNeuron> visited, FreeformNeuron parentNeuron, NeuronTask task) {
		visited.add(parentNeuron);
		task.task(parentNeuron);
		
		// does this neuron have any inputs?
		if( parentNeuron.getInputSummation()!=null ) { 
			// visit the inputs
			for(FreeformConnection connection : parentNeuron.getInputSummation().list() ) {
				FreeformNeuron neuron = connection.getSource();
				// have we already visited this neuron?
				if( !visited.contains(neuron) ) {
					performNeuronTask(visited,neuron,task);
				}
			}
		}
	}
	
	public void performConnectionTask(ConnectionTask task) {
		Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>();
		
		for(FreeformNeuron neuron: this.outputLayer.getNeurons()) {
			performConnectionTask(visited,neuron,task);
		}
	}
	
	private void performConnectionTask(Set<FreeformNeuron> visited, FreeformNeuron parentNeuron, ConnectionTask task) {
		visited.add(parentNeuron);		
		
		// does this neuron have any inputs?
		if( parentNeuron.getInputSummation()!=null ) { 
			// visit the inputs
			for(FreeformConnection connection : parentNeuron.getInputSummation().list() ) {
				task.task(connection);
				FreeformNeuron neuron = connection.getSource();
				// have we already visited this neuron?
				if( !visited.contains(neuron) ) {
					performConnectionTask(visited,neuron,task);
				}
			}
		}
	}
	
	public void tempTrainingAllocate(final int neuronSize, final int connectionSize) {
		performNeuronTask(new NeuronTask()
		{
			@Override
			public void task(FreeformNeuron neuron) {
				neuron.allocateTempTraining(neuronSize);
				if( neuron.getInputSummation()!=null ) {
					for(FreeformConnection connection: neuron.getInputSummation().list()) {
						connection.allocateTempTraining(connectionSize);
					}
				}
			}
		});
	}

	public void tempTrainingClear() {
		performNeuronTask(new NeuronTask()
		{
			@Override
			public void task(FreeformNeuron neuron) {
				neuron.clearTempTraining();
				if( neuron.getInputSummation()!=null ) {
					for(FreeformConnection connection: neuron.getInputSummation().list()) {
						connection.clearTempTraining();
					}
				}
			}
		});
	}

	public FreeformLayer getOutputLayer() {
		return this.outputLayer;
	}
	
	/**
	 * Return a clone of this neural network. Including structure, weights and
	 * bias values. This is a deep copy.
	 * 
	 * @return A cloned copy of the neural network.
	 */
	@Override
	public Object clone() {
		final BasicNetwork result = (BasicNetwork) ObjectCloner.deepCopy(this);
		return result;
	}

	@Override
	public void updateProperties() {
		// not needed
	}

	public static FreeformNetwork createElman(int input, int hidden1, int output,
			ActivationFunction af) {
		
		FreeformNetwork network = new FreeformNetwork();
		FreeformLayer inputLayer = network.createInputLayer(2);
		FreeformLayer hiddenLayer1 = network.createLayer(3);
		FreeformLayer outputLayer = network.createOutputLayer(1);
		
		network.connectLayers(inputLayer, hiddenLayer1, af, 1.0, false);
		network.connectLayers(hiddenLayer1, outputLayer, af, 1.0, false);
		network.createContext(hiddenLayer1, hiddenLayer1);
		network.reset();
		
		return network;
	}

	public static FreeformNetwork createFeedforward(int input, int hidden1, int hidden2, int output,
			ActivationFunction af) {
		FreeformNetwork network = new FreeformNetwork();
		FreeformLayer lastLayer = network.createInputLayer(input);
		FreeformLayer currentLayer;
		
		if( hidden1>0 ) {
			currentLayer = network.createLayer(hidden1);
			network.connectLayers(lastLayer, currentLayer, af, 1.0, false);
			lastLayer = currentLayer;
		}
		
		if( hidden2>0 ) {
			currentLayer = network.createLayer(hidden2);
			network.connectLayers(lastLayer, currentLayer, af, 1.0, false);
			lastLayer = currentLayer;
		}
		
		currentLayer = network.createOutputLayer(output);
		network.connectLayers(lastLayer, currentLayer, af, 1.0, false);
		
		network.reset();
		
		return network;
	}

}
