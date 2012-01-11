/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.util.benchmark;

import java.io.File;

import org.encog.StatusReportable;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import org.encog.util.Format;

/**
 * Benchmark Encog with several network types.
 * 
 * @author jheaton
 * 
 */
public class EncogBenchmark {

	/**
	 * Number of steps in all.
	 */
	private static final int STEPS = 4;

	/**
	 * The first step.
	 */
	private static final int STEP1 = 1;


	/**
	 * The third step.
	 */
	private static final int STEP3 = 3;

	/**
	 * The fourth step.
	 */
	private static final int STEP4 = 4;

	/**
	 * Report progress.
	 */
	private final StatusReportable report;

	private int cpuScore;
	private int clScore;
	private int memoryScore;
	private int binaryScore;

	/**
	 * Construct a benchmark object.
	 * 
	 * @param report
	 *            The object to report progress to.
	 */
	public EncogBenchmark(final StatusReportable report) {
		this.report = report;
	}

	/**
	 * Perform the benchmark. Returns the total amount of time for all of the
	 * benchmarks. Returns the final score. The lower the better for a score.
	 * 
	 * @return The total time, which is the final Encog benchmark score.
	 */
	public String process() {
		this.report.report(EncogBenchmark.STEPS, 0, "Beginning benchmark");

		evalCPU();
		evalMemory();
		evalBinary();

		StringBuilder result = new StringBuilder();

		result.append("Encog Benchmark: CPU:");
		result.append(Format.formatInteger(this.cpuScore));
		result.append(", Memory:");
		result.append(Format.formatInteger(this.memoryScore));
		result.append(", Disk:");
		result.append(Format.formatInteger(this.binaryScore));
		this.report.report(EncogBenchmark.STEPS, EncogBenchmark.STEPS, result
				.toString());

		return result.toString();
	}

	/**
	 * Train the neural network with 0 hidden layers.
	 * 
	 * @return The amount of time this benchmark took.
	 */
	private void evalCPU() {

		int small = Evaluate.evaluateTrain(2, 4, 0, 1);
		this.report.report(EncogBenchmark.STEPS, EncogBenchmark.STEP1,
				"Evaluate CPU, tiny= " + Format.formatInteger(small / 100));

		int medium = Evaluate.evaluateTrain(10, 20, 0, 1);
		this.report.report(EncogBenchmark.STEPS, EncogBenchmark.STEP1,
				"Evaluate CPU, small= " + Format.formatInteger(medium / 30));

		int large = Evaluate.evaluateTrain(100, 200, 40, 5);
		this.report.report(EncogBenchmark.STEPS, EncogBenchmark.STEP1,
				"Evaluate CPU, large= " + Format.formatInteger(large));

		int huge = Evaluate.evaluateTrain(200, 300, 200, 50);
		this.report.report(EncogBenchmark.STEPS, EncogBenchmark.STEP1,
				"Evaluate CPU, huge= " + Format.formatInteger(huge));

		int result = (small / 100) + (medium / 30) + large + huge;

		this.report.report(EncogBenchmark.STEPS, EncogBenchmark.STEP1,
				"CPU result: " + result);
		this.cpuScore = result;
	}

	private void evalMemory() {
		final BasicMLDataSet training = RandomTrainingFactory.generate(
				1000, 10000, 10, 10, -1, 1);

		final long start = System.currentTimeMillis();
		final long stop = start + (10 * Evaluate.MILIS);
		int record = 0;

		MLDataPair pair = BasicMLDataPair.createPair(10, 10);

		int iterations = 0;
		while (System.currentTimeMillis() < stop) {
			iterations++;
			training.getRecord(record++, pair);
			if (record >= training.getRecordCount())
				record = 0;
		}

		iterations /= 100000;

		this.report.report(EncogBenchmark.STEPS, EncogBenchmark.STEP3,
				"Memory dataset, result: " + Format.formatInteger(iterations));

		this.memoryScore = iterations;
	}

	private void evalBinary() {
		File file = new File("temp.egb");

		final BasicMLDataSet training = RandomTrainingFactory.generate(
				1000, 10000, 10, 10, -1, 1);

		// create the binary file

		file.delete();
		BufferedMLDataSet training2 = new BufferedMLDataSet(file);
		training2.load(training);

		final long start = System.currentTimeMillis();
		final long stop = start + (10 * Evaluate.MILIS);
		int record = 0;

		MLDataPair pair = BasicMLDataPair.createPair(10, 10);

		int iterations = 0;
		while (System.currentTimeMillis() < stop) {
			iterations++;
			training2.getRecord(record++, pair);
			if (record >= training2.getRecordCount())
				record = 0;
		}

		training.close();
		iterations /= 100000;

		this.report.report(EncogBenchmark.STEPS, EncogBenchmark.STEP4,
				"Disk(binary) dataset, result: "
						+ Format.formatInteger(iterations));

		file.delete();
		this.binaryScore = iterations;
	}

	/**
	 * @return the cpuScore
	 */
	public int getCpuScore() {
		return cpuScore;
	}

	/**
	 * @return the clScore
	 */
	public int getClScore() {
		return clScore;
	}

	/**
	 * @return the memoryScore
	 */
	public int getMemoryScore() {
		return memoryScore;
	}

	/**
	 * @return the binaryScore
	 */
	public int getBinaryScore() {
		return binaryScore;
	}



}
