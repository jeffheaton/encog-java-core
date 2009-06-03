package org.encog.util.benchmark;

import org.encog.StatusReportable;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.logging.Logging;
import org.encog.util.randomize.RangeRandomizer;

public class EncogBenchmark {
	
	private StatusReportable report;
	
	public EncogBenchmark(StatusReportable report)
	{
		this.report = report;
	}
	
	private double benchmar0Hidden()
	{
		System.out.print("Evaluating 0 hidden layer network...");
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(20));
		network.addLayer(new BasicLayer(20));
		network.getStructure().finalizeStructure();
		network.reset();
		
		NeuralDataSet training = RandomTrainingFactory.generate(10000, 20, 20, -1, 1);
		
		double result = Evaluate.evaluateNetwork(network, training);
		this.report.report(7,2, "Evaluate 0 hidden layer result: " + result);
		return result;
	}
	
	private double benchmar1Hidden()
	{
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(20));
		network.addLayer(new BasicLayer(30));
		network.addLayer(new BasicLayer(20));
		network.getStructure().finalizeStructure();
		network.reset();
		
		NeuralDataSet training = RandomTrainingFactory.generate(10000, 20, 20, -1, 1);
		
		double result = Evaluate.evaluateNetwork(network, training);
		this.report.report(7,3, "Evaluate 1 hidden layer result: " + result);
		return result;
	}
	
	private double benchmar2Hidden()
	{
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(20));
		network.addLayer(new BasicLayer(30));
		network.addLayer(new BasicLayer(30));
		network.addLayer(new BasicLayer(20));
		network.getStructure().finalizeStructure();
		network.reset();
		
		NeuralDataSet training = RandomTrainingFactory.generate(10000, 20, 20, -1, 1);
		
		double result = Evaluate.evaluateNetwork(network, training);
		this.report.report(7,4, "Evaluate 2 hidden layer result: " + result);
		return result;
	}
	
	private double train0Hidden()
	{
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(20));
		network.addLayer(new BasicLayer(20));
		network.getStructure().finalizeStructure();
		network.reset();
		
		NeuralDataSet training = RandomTrainingFactory.generate(10000, 20, 20, -1, 1);
		
		double result = Evaluate.evaluateTrain(network, training);
		this.report.report(7,5, "Train 0 hidden layer result: " + result);
		return result;
	}
	
	private double train1Hidden()
	{
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(20));
		network.addLayer(new BasicLayer(30));
		network.addLayer(new BasicLayer(20));
		network.getStructure().finalizeStructure();
		network.reset();
		
		NeuralDataSet training = RandomTrainingFactory.generate(10000, 20, 20, -1, 1);
		
		double result = Evaluate.evaluateTrain(network, training);
		this.report.report(7,6, "Train 1 hidden layer result: " + result);
		return result;
	}
	
	private double train2Hidden()
	{
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(20));
		network.addLayer(new BasicLayer(30));
		network.addLayer(new BasicLayer(30));
		network.addLayer(new BasicLayer(20));
		network.getStructure().finalizeStructure();
		network.reset();
		
		NeuralDataSet training = RandomTrainingFactory.generate(10000, 20, 20, -1, 1);
		
		double result = Evaluate.evaluateTrain(network, training);
		this.report.report(7,7, "Train 2 hidden layer result: " + result);
		return result;
	}
	
	private double trainElman()
	{
		// construct an Elman type network
		Layer hidden;
		Layer context = new ContextLayer(30);
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(20));
		network.addLayer(hidden = new BasicLayer(30));
		hidden.addNext(context,SynapseType.OneToOne);
		context.addNext(hidden);
		network.addLayer(new BasicLayer(20));
		network.getStructure().finalizeStructure();
		network.reset();

		
		NeuralDataSet training = RandomTrainingFactory.generate(10000, 20, 20, -1, 1);
		
		double result = Evaluate.evaluateTrain(network, training);
		this.report.report(7,1, "Training Elman result: " + result);
		return result;
	}
	
	
	public double process()
	{
		Logging.stopConsoleLogging();
		report.report(7, 0, "Beginning benchmark");
		double total = 0;
		total+=trainElman();
		total+=benchmar0Hidden();
		total+=benchmar1Hidden();
		total+=benchmar2Hidden();
		total+=train0Hidden();
		total+=train1Hidden();
		total+=train2Hidden();
		return total;
	}
}
