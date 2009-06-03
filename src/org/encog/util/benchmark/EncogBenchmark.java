/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.util.benchmark;

import org.encog.StatusReportable;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.util.logging.Logging;

/**
 * Benchmark Encog with several network types.
 * @author jheaton
 *
 */
public class EncogBenchmark {

	/**
	 * Number of steps in all.
	 */
	private static final int STEPS = 7;
	
	/**
	 * The first step.
	 */
	private static final int STEP1 = 1;
	
	/**
	 * The second step.
	 */
	private static final int STEP2 = 2;
	
	/**
	 * The third step.
	 */
	private static final int STEP3 = 3;
	
	/**
	 * The fourth step.
	 */
	private static final int STEP4 = 4;
	
	/**
	 * The fifth step.
	 */
	private static final int STEP5 = 5;
	
	/**
	 * The sixth step.
	 */
	private static final int STEP6 = 6;
	
	/**
	 * The seventh step.
	 */
	private static final int STEP7 = 7;
	
	/**
	 * The number of input neurons.
	 */	
	private static final int INPUT_COUNT = 20;
	
	/**
	 * The number of output neurons.
	 */	
	private static final int OUTPUT_COUNT = 20;
	
	/**
	 * The number of hidden neurons.
	 */	
	private static final int HIDDEN_COUNT = 30;
	
	/**
	 * Report progress.
	 */
	private final StatusReportable report;

	/**
	 * Construct a benchmark object.
	 * @param report The object to report progress to.
	 */
	public EncogBenchmark(final StatusReportable report) {
		this.report = report;
	}

	/**
	 * Benchmark a network with no hidden layers.
	 * @return The amount of time this benchmark took.
	 */
	private double benchmar0Hidden() {
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(INPUT_COUNT));
		network.addLayer(new BasicLayer(OUTPUT_COUNT));
		network.getStructure().finalizeStructure();
		network.reset();

		final NeuralDataSet training = RandomTrainingFactory.generate(10000,
				20, 20, -1, 1);

		final double result = Evaluate.evaluateNetwork(network, training);
		this.report.report(STEPS, STEP2, 
				"Evaluate 0 hidden layer result: " + result);
		return result;
	}

	/**
	 * Benchmark a network with one hidden layer.
	 * @return The amount of time this benchmark took.
	 */
	private double benchmar1Hidden() {
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(INPUT_COUNT));
		network.addLayer(new BasicLayer(HIDDEN_COUNT));
		network.addLayer(new BasicLayer(OUTPUT_COUNT));
		network.getStructure().finalizeStructure();
		network.reset();

		final NeuralDataSet training = RandomTrainingFactory.generate(10000,
				20, 20, -1, 1);

		final double result = Evaluate.evaluateNetwork(network, training);
		this.report.report(STEPS, STEP3, 
				"Evaluate 1 hidden layer result: " + result);
		return result;
	}

	/**
	 * Benchmark a network with two hidden layers.
	 * @return The amount of time this benchmark took.
	 */
	private double benchmar2Hidden() {
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(INPUT_COUNT));
		network.addLayer(new BasicLayer(HIDDEN_COUNT));
		network.addLayer(new BasicLayer(HIDDEN_COUNT));
		network.addLayer(new BasicLayer(OUTPUT_COUNT));
		network.getStructure().finalizeStructure();
		network.reset();

		final NeuralDataSet training = RandomTrainingFactory.generate(10000,
				20, 20, -1, 1);

		final double result = Evaluate.evaluateNetwork(network, training);
		this.report.report(STEPS, 
				STEP4, "Evaluate 2 hidden layer result: " + result);
		return result;
	}

	/**
	 * Perform the benchmark.  Returns the total amount of time for all of the
	 * benchmarks.  Returns the final score.  The lower the better for a score.
	 * @return The total time, which is the final Encog benchmark score.
	 */
	public double process() {
		Logging.stopConsoleLogging();
		this.report.report(STEPS, 0, 
				"Beginning benchmark");
		double total = 0;
		total += trainElman();
		total += benchmar0Hidden();
		total += benchmar1Hidden();
		total += benchmar2Hidden();
		total += train0Hidden();
		total += train1Hidden();
		total += train2Hidden();
		return total;
	}

	/**
	 * Train the neural network with 0 hidden layers.
	 * @return  The amount of time this benchmark took.
	 */
	private double train0Hidden() {
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(INPUT_COUNT));
		network.addLayer(new BasicLayer(OUTPUT_COUNT));
		network.getStructure().finalizeStructure();
		network.reset();

		final NeuralDataSet training = RandomTrainingFactory.generate(10000,
				20, 20, -1, 1);

		final double result = Evaluate.evaluateTrain(network, training);
		this.report.report(STEPS, 
				STEP5, "Train 0 hidden layer result: " + result);
		return result;
	}

	/**
	 * Train the neural network with 1 hidden layer.
	 * @return  The amount of time this benchmark took.
	 */
	private double train1Hidden() {
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(INPUT_COUNT));
		network.addLayer(new BasicLayer(HIDDEN_COUNT));
		network.addLayer(new BasicLayer(OUTPUT_COUNT));
		network.getStructure().finalizeStructure();
		network.reset();

		final NeuralDataSet training = RandomTrainingFactory.generate(10000,
				20, 20, -1, 1);

		final double result = Evaluate.evaluateTrain(network, training);
		this.report.report(STEPS, 
				STEP6, "Train 1 hidden layer result: " + result);
		return result;
	}

	/**
	 * Train the neural network with 2 hidden layers.
	 * @return  The amount of time this benchmark took.
	 */
	private double train2Hidden() {
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(INPUT_COUNT));
		network.addLayer(new BasicLayer(HIDDEN_COUNT));
		network.addLayer(new BasicLayer(HIDDEN_COUNT));
		network.addLayer(new BasicLayer(OUTPUT_COUNT));
		network.getStructure().finalizeStructure();
		network.reset();

		final NeuralDataSet training = RandomTrainingFactory.generate(10000,
				20, 20, -1, 1);

		final double result = Evaluate.evaluateTrain(network, training);
		this.report.report(STEPS, 
				STEP7, "Train 2 hidden layer result: " + result);
		return result;
	}

	/**
	 * Train an Elman neural network.
	 * @return  The amount of time this benchmark took.
	 */
	private double trainElman() {
		// construct an Elman type network
		Layer hidden;
		final Layer context = new ContextLayer(30);
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(INPUT_COUNT));
		hidden = new BasicLayer(HIDDEN_COUNT);
		network.addLayer(hidden);
		hidden.addNext(context, SynapseType.OneToOne);
		context.addNext(hidden);
		network.addLayer(new BasicLayer(OUTPUT_COUNT));
		network.getStructure().finalizeStructure();
		network.reset();

		final NeuralDataSet training = RandomTrainingFactory.generate(10000,
				20, 20, -1, 1);

		final double result = Evaluate.evaluateTrain(network, training);
		this.report.report(STEPS, STEP1, "Training Elman result: " + result);
		return result;
	}
}
