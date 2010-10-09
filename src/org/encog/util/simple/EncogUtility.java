/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */

package org.encog.util.simple;

import java.io.File;

import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.network.train.prop.OpenCLTrainingProfile;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.util.Format;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.buffer.BufferedNeuralDataSet;
import org.encog.neural.data.csv.CSVNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.svm.SVMNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.svm.SVMTrain;
import org.encog.neural.pattern.FeedForwardPattern;

/**
 * General utility class for Encog. Provides for some common Encog procedures.
 */
public final class EncogUtility {

	/**
	 * Convert a CSV file to a binary training file.
	 * 
	 * @param csvFile
	 *            The CSV file.
	 * @param binFile
	 *            The binary file.
	 * @param inputCount
	 *            The number of input values.
	 * @param outputCount
	 *            The number of output values.
	 * @param headers
	 *            True, if there are headers on the3 CSV.
	 */
	public static void convertCSV2Binary(final File csvFile,
			final File binFile, final int inputCount, final int outputCount,
			final boolean headers) {
		binFile.delete();
		final CSVNeuralDataSet csv = new CSVNeuralDataSet(csvFile.toString(),
				inputCount, outputCount, false);
		final BufferedNeuralDataSet buffer = new BufferedNeuralDataSet(binFile);
		buffer.beginLoad(inputCount, outputCount);
		for (final NeuralDataPair pair : csv) {
			buffer.add(pair);
		}
		buffer.endLoad();
	}

	/**
	 * Create a profile from the first available OpenCL GPU, or CPU, if no GPU
	 * is found. Try to determine the best values for the number of global work
	 * items and OpenCL ratio.
	 * 
	 * @param network
	 *            The network to be trained.
	 * @param training
	 *            The training data to be used.
	 * @return A training profile.
	 */
	public static OpenCLTrainingProfile createProfile(
			final BasicNetwork network, final NeuralDataSet training) {
		network.getStructure().updateFlatNetwork();
		final FlatNetwork flat = network.getStructure().getFlat();
		return OpenCLTrainingProfile.createProfile(flat,
				(EngineIndexableSet) training);
	}

	/**
	 * Create a profile from the first available OpenCL GPU, or CPU, if no GPU
	 * is found. Use the max values for the number of global work items and
	 * OpenCL ratio. If your GPU can handle it, this will provide the best
	 * performance. Note, that you might see your OS reboot your GPU if the
	 * kernel takes too long to execute.
	 * 
	 * See:
	 * 
	 * http://www.heatonresearch.com/encog/troubleshooting/ooresource.html
	 * 
	 * @param network
	 *            The network to be trained.
	 * @param training
	 *            The training data to be used.
	 * @return A training profile.
	 */
	public static OpenCLTrainingProfile createProfileMax(
			final BasicNetwork network, final NeuralDataSet training) {
		network.getStructure().updateFlatNetwork();
		final FlatNetwork flat = network.getStructure().getFlat();
		return OpenCLTrainingProfile.createProfileMax(flat,
				(EngineIndexableSet) training);
	}

	/**
	 * Create a profile from the first available OpenCL GPU, or CPU, if no GPU
	 * is found. The ratio allows you to specify how much of the training set
	 * should be sent to the kernel. Specify 1.0 for max performance, or 0.5 to
	 * send only half. The higher this value, the more likely your OS may
	 * timeout your kernel, especially with a less powerful GPU.
	 * 
	 * See:
	 * 
	 * http://www.heatonresearch.com/encog/troubleshooting/ooresource.html
	 * 
	 * @param network
	 *            The network to be trained.
	 * @param training
	 *            The training data to be used.
	 * @param ratio
	 *            The ratio to use. Specify 1.0(max) to process the entire
	 *            training set with each call to the OpenCL kernel.
	 * @return A training profile.
	 */
	public static OpenCLTrainingProfile createProfileRatio(
			final BasicNetwork network, final NeuralDataSet training,
			final double ratio) {
		network.getStructure().updateFlatNetwork();
		final FlatNetwork flat = network.getStructure().getFlat();
		return OpenCLTrainingProfile.createProfileRatio(flat,
				(EngineIndexableSet) training, ratio);
	}

	/**
	 * Create a profile a specific OpenCL device. The ratio allows you to
	 * specify how much of the training set should be sent to the kernel.
	 * Specify 1.0 for max performance, or 0.5 to send only half. The higher
	 * this value, the more likely your OS may timeout your kernel, especially
	 * with a less powerful GPU.
	 * 
	 * See:
	 * 
	 * http://www.heatonresearch.com/encog/troubleshooting/ooresource.html
	 * 
	 * @param device
	 *            The OpenCL device to use.
	 * @param network
	 *            The network to be trained.
	 * @param training
	 *            The training data to be used.
	 * @param ratio
	 *            The ratio to use. Specify 1.0(max) to process the entire
	 *            training set with each call to the OpenCL kernel.
	 * @return A training profile.
	 */
	public static OpenCLTrainingProfile createProfileRatio(
			final EncogCLDevice device, final BasicNetwork network,
			final NeuralDataSet training, final double ratio) {
		network.getStructure().updateFlatNetwork();
		final FlatNetwork flat = network.getStructure().getFlat();
		return OpenCLTrainingProfile.createProfileRatio(flat,
				(EngineIndexableSet) training, ratio);
	}

	/**
	 * Evaluate the network and display (to the console) the output for every
	 * value in the training set. Displays ideal and actual.
	 * 
	 * @param network
	 *            The network to evaluate.
	 * @param training
	 *            The training set to evaluate.
	 */
	public static void evaluate(final BasicNetwork network,
			final NeuralDataSet training) {
		for (final NeuralDataPair pair : training) {
			final NeuralData output = network.compute(pair.getInput());
			System.out.println("Input="
					+ EncogUtility.formatNeuralData(pair.getInput())
					+ ", Actual=" + EncogUtility.formatNeuralData(output)
					+ ", Ideal="
					+ EncogUtility.formatNeuralData(pair.getIdeal()));

		}
	}

	/**
	 * Format neural data as a list of numbers.
	 * 
	 * @param data
	 *            The neural data to format.
	 * @return The formatted neural data.
	 */
	public static String formatNeuralData(final NeuralData data) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			if (i != 0) {
				result.append(',');
			}
			result.append(Format.formatDouble(data.getData(i), 4));
		}
		return result.toString();
	}

	/**
	 * Create a simple feedforward neural network.
	 * 
	 * @param input
	 *            The number of input neurons.
	 * @param hidden1
	 *            The number of hidden layer 1 neurons.
	 * @param hidden2
	 *            The number of hidden layer 2 neurons.
	 * @param output
	 *            The number of output neurons.
	 * @param tanh
	 *            True to use hyperbolic tangent activation function, false to
	 *            use the sigmoid activation function.
	 * @return The neural network.
	 */
	public static BasicNetwork simpleFeedForward(final int input,
			final int hidden1, final int hidden2, final int output,
			final boolean tanh) {
		final FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(input);
		pattern.setOutputNeurons(output);
		if (tanh) {
			pattern.setActivationFunction(new ActivationTANH());
		} else {
			pattern.setActivationFunction(new ActivationSigmoid());
		}

		if (hidden1 > 0) {
			pattern.addHiddenLayer(hidden1);
		}
		if (hidden2 > 0) {
			pattern.addHiddenLayer(hidden2);
		}

		final BasicNetwork network = pattern.generate();
		network.reset();
		return network;
	}

	/**
	 * Train the neural network, using SCG training, and output status to the
	 * console.
	 * 
	 * @param network
	 *            The network to train.
	 * @param trainingSet
	 *            The training set.
	 * @param minutes
	 *            The number of minutes to train for.
	 */
	public static void trainConsole(final BasicNetwork network,
			final NeuralDataSet trainingSet, final int minutes) {
		final Propagation train = new ResilientPropagation(network, trainingSet);
		train.setNumThreads(0);
		EncogUtility.trainConsole(train, network, trainingSet, minutes);
	}

	/**
	 * Train the network, using the specified training algorithm, and send the
	 * output to the console.
	 * 
	 * @param train
	 *            The training method to use.
	 * @param network
	 *            The network to train.
	 * @param trainingSet
	 *            The training set.
	 * @param minutes
	 *            The number of minutes to train for.
	 */
	public static void trainConsole(final Train train,
			final BasicNetwork network, final NeuralDataSet trainingSet,
			final int minutes) {

		long remaining;

		System.out.println("Beginning training...");
		final long start = System.currentTimeMillis();
		do {
			train.iteration();

			final long current = System.currentTimeMillis();
			final long elapsed = (current - start) / 1000;// seconds
			remaining = minutes - elapsed / 60;

			int iteration = train.getIteration();
			
			System.out.println("Iteration #" + Format.formatInteger(iteration)
					+ " Error:" + Format.formatPercent(train.getError())
					+ " elapsed time = " + Format.formatTimeSpan((int) elapsed)
					+ " time left = "
					+ Format.formatTimeSpan((int) remaining * 60));

		} while (remaining > 0);
		train.finishTraining();
	}

	/**
	 * Train using SCG and display progress to a dialog box.
	 * 
	 * @param network
	 *            The network to train.
	 * @param trainingSet
	 *            The training set to use.
	 */
	public static void trainDialog(final BasicNetwork network,
			final NeuralDataSet trainingSet) {
		final Propagation train = new ResilientPropagation(network, trainingSet);
		train.setNumThreads(0);
		EncogUtility.trainDialog(train, network, trainingSet);
	}

	/**
	 * Train, using the specified training method, display progress to a dialog
	 * box.
	 * 
	 * @param train
	 *            The training method to use.
	 * @param network
	 *            The network to train.
	 * @param trainingSet
	 *            The training set to use.
	 */
	public static void trainDialog(final Train train,
			final BasicNetwork network, final NeuralDataSet trainingSet) {

		final TrainingDialog dialog = new TrainingDialog();
		dialog.setVisible(true);

		final long start = System.currentTimeMillis();
		do {
			train.iteration();
			int iteration = train.getIteration();
			final long current = System.currentTimeMillis();
			final long elapsed = (current - start) / 1000;// seconds
			dialog.setIterations(iteration);
			dialog.setError(train.getError());
			dialog.setTime((int) elapsed);
		} while (!dialog.shouldStop());
		train.finishTraining();
		dialog.dispose();
	}

	/**
	 * Train the network, to a specific error, send the output to the console.
	 * 
	 * @param network
	 *            The network to train.
	 * @param trainingSet
	 *            The training set to use.
	 * @param error
	 *            The error level to train to.
	 */
	public static void trainToError(final BasicNetwork network,
			final NeuralDataSet trainingSet, final double error) {

		Train train;

		if (network instanceof SVMNetwork) {
			train = new SVMTrain(network, trainingSet);
		} else {
			train = new ResilientPropagation(network, trainingSet);
		}
		EncogUtility.trainToError(train, network, trainingSet, error);
	}

	/**
	 * Train to a specific error, using the specified training method, send the
	 * output to the console.
	 * 
	 * @param train
	 *            The training method.
	 * @param network
	 *            The network to train.
	 * @param trainingSet
	 *            The training set to use.
	 * @param error
	 *            The desired error level.
	 */
	public static void trainToError(final Train train,
			final BasicNetwork network, final NeuralDataSet trainingSet,
			final double error) {

		int epoch = 1;

		System.out.println("Beginning training...");

		do {
			train.iteration();

			System.out.println("Iteration #" + Format.formatInteger(epoch)
					+ " Error:" + Format.formatPercent(train.getError())
					+ " Target Error: " + Format.formatPercent(error));
			epoch++;
		} while ((train.getError() > error) && !train.isTrainingDone());
		train.finishTraining();
	}

	/**
	 * Private constructor.
	 */
	private EncogUtility() {

	}
}
