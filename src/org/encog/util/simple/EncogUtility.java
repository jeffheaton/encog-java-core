/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.encog.util.simple;

import java.io.File;

import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.buffer.BufferedNeuralDataSet;
import org.encog.neural.data.csv.CSVNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.scg.ScaledConjugateGradient;
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
			NeuralDataSet trainingSet, int minutes) {
		final Propagation train = new ScaledConjugateGradient(network, trainingSet );
		train.setNumThreads(0);
		trainConsole(train,network,trainingSet,minutes);
	}

	public static void trainToError(BasicNetwork network,
			NeuralDataSet trainingSet, double error) {
		final Propagation train = new ScaledConjugateGradient(network, trainingSet );
		train.setNumThreads(0);
		trainToError(train,network,trainingSet,error);
	}
	
	public static void trainConsole(Train train, BasicNetwork network,
			NeuralDataSet trainingSet, int minutes) {
	
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
	
	public static void trainToError(Train train, BasicNetwork network,
			NeuralDataSet trainingSet, double error) {
	
		int epoch = 1;

		System.out.println("Beginning training...");

		do {
			train.iteration();
			
			System.out
					.println("Iteration #" + Format.formatInteger(epoch) 
							+ " Error:" + Format.formatPercent(train.getError()) 
							+ " Target Error: " + Format.formatPercent(error));
			epoch++;
		} while(train.getError()>error);	
	}
	
	public static void trainDialog(BasicNetwork network,
			NeuralDataSet trainingSet) {
		final Propagation train = new ScaledConjugateGradient(network, trainingSet );
		train.setNumThreads(0);
		trainDialog(train, network,trainingSet);
	}
	
	
	public static void trainDialog(Train train, BasicNetwork network,
			NeuralDataSet trainingSet) {
		
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
	
	private static String formatNeuralData(NeuralData data)
	{
		StringBuilder result = new StringBuilder();
		for(int i=0;i<data.size();i++) {
			if( i!=0 )
				result.append(',');
			result.append(Format.formatDouble(data.getData(i), 4));
		}
		return result.toString();
	}
	
	public static void evaluate(BasicNetwork network, NeuralDataSet training) {
		for (final NeuralDataPair pair : training) {
			final NeuralData output = network.compute(pair.getInput());
			System.out.println("Input=" + formatNeuralData(pair.getInput()) 
					+", Actual=" + formatNeuralData(output) 
					+", Ideal=" + formatNeuralData(pair.getIdeal()));
					
		}
	}
}
