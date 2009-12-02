package org.encog.util.simple;

import java.io.File;

import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.buffer.BufferedNeuralDataSet;
import org.encog.neural.data.csv.CSVNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.multi.MultiPropagation;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.util.Format;

public class EncogUtility {
	
	public static void convertCSV2Binary(File csvFile,File binFile,int inputCount,int outputCount,boolean headers)
	{
		binFile.delete();
		CSVNeuralDataSet csv = new CSVNeuralDataSet(csvFile.toString(),inputCount,outputCount,false);
		BufferedNeuralDataSet buffer = new BufferedNeuralDataSet(binFile);
		buffer.beginLoad(50, 6);
		for(NeuralDataPair pair: csv)
		{
			buffer.add(pair);
		}
		buffer.endLoad();
	}
	
	public static BasicNetwork simpleFeedForward(int input,int hidden1,int hidden2,int output,boolean tanh)
	{
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(input);
		pattern.setOutputNeurons(output);
		if( tanh)
			pattern.setActivationFunction(new ActivationTANH());
		else
			pattern.setActivationFunction(new ActivationSigmoid());
		
		if( hidden1>0 )
			pattern.addHiddenLayer(hidden1);
		if( hidden2>0 )
			pattern.addHiddenLayer(hidden2);
		
		BasicNetwork network = pattern.generate();
		network.reset();
		return network;
	}
	public static void trainConsole(BasicNetwork network,
			BufferedNeuralDataSet trainingSet, int minutes) {
		final Train train = new MultiPropagation(network, trainingSet );
	}
	
	public static void trainConsole(Train train, BasicNetwork network,
			BufferedNeuralDataSet trainingSet, int minutes) {
	
		int epoch = 1;
		long remaining;

		System.out.println("Beginning training...");
		long start = System.currentTimeMillis();
		do {
			train.iteration();
			
			long current = System.currentTimeMillis();
			long elapsed = (current-start)/1000;// seconds
			remaining = minutes - elapsed/60;
			
			System.out
					.println("Iteration #" + Format.formatInteger(epoch) 
							+ " Error:" + Format.formatPercent(train.getError()) 
							+ " elapsed time = " + Format.formatTimeSpan((int)elapsed)
							+ " time left = " + Format.formatTimeSpan((int)remaining*60));
			epoch++;
		} while(remaining>0);	
	}
	
	public static void trainDialog(BasicNetwork network,
			BufferedNeuralDataSet trainingSet) {
		final Train train = new MultiPropagation(network, trainingSet );
		trainDialog(train, network,trainingSet);
	}
	
	
	public static void trainDialog(Train train, BasicNetwork network,
			BufferedNeuralDataSet trainingSet) {
		
		int epoch = 1;
		TrainingDialog dialog = new TrainingDialog();
		dialog.setVisible(true);

		long start = System.currentTimeMillis();
		do {
			train.iteration();
			
			long current = System.currentTimeMillis();
			long elapsed = (current-start)/1000;// seconds
			dialog.setIterations(epoch);
			dialog.setError(train.getError());
			dialog.setTime((int)elapsed);
			epoch++;
		} while(!dialog.shouldStop());	
		dialog.dispose();
	}
}
