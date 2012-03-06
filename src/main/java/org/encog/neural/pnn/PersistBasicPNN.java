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
package org.encog.neural.pnn;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.util.EngineArray;
import org.encog.util.csv.CSVFormat;

/**
 * Persist a PNN.
 */
public class PersistBasicPNN implements EncogPersistor {

	/**
	 * The output mode property.
	 */
	public static final String PROPERTY_outputMode = "outputMode";

	/**
	 * Convert a kernel type to a string.
	 * @param k The kernel type.
	 * @return The string.
	 */
	public static String kernelToString(final PNNKernelType k) {
		switch (k) {
		case Gaussian:
			return "gaussian";
		case Reciprocal:
			return "reciprocal";
		default:
			return null;
		}
	}

	/**
	 * Convert output mode to string.
	 * @param mode The output mode.
	 * @return The string.
	 */
	public static String outputModeToString(final PNNOutputMode mode) {
		switch (mode) {
		case Regression:
			return "regression";
		case Unsupervised:
			return "unsupervised";
		case Classification:
			return "classification";
		default:
			return null;
		}
	}

	/**
	 * Convert a string to a PNN kernel.
	 * @param k The string.
	 * @return The kernel.
	 */
	public static PNNKernelType stringToKernel(final String k) {
		if (k.equalsIgnoreCase("gaussian")) {
			return PNNKernelType.Gaussian;
		} else if (k.equalsIgnoreCase("reciprocal")) {
			return PNNKernelType.Reciprocal;
		} else {
			return null;
		}
	}

	/**
	 * Convert a string to a PNN output mode.
	 * @param mode The string.
	 * @return The output ndoe.
	 */
	public static PNNOutputMode stringToOutputMode(final String mode) {
		if (mode.equalsIgnoreCase("regression")) {
			return PNNOutputMode.Regression;
		} else if (mode.equalsIgnoreCase("unsupervised")) {
			return PNNOutputMode.Unsupervised;
		} else if (mode.equalsIgnoreCase("classification")) {
			return PNNOutputMode.Classification;
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getFileVersion() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPersistClassString() {
		return "BasicPNN";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object read(final InputStream is) {

		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		final BasicMLDataSet samples = new BasicMLDataSet();
		Map<String, String> networkParams = null;
		PNNKernelType kernel = null;
		PNNOutputMode outmodel = null;
		int inputCount = 0;
		int outputCount = 0;
		double error = 0;
		double[] sigma = null;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("PNN")
					&& section.getSubSectionName().equals("PARAMS")) {
				networkParams = section.parseParams();
			}
			if (section.getSectionName().equals("PNN")
					&& section.getSubSectionName().equals("NETWORK")) {
				final Map<String, String> params = section.parseParams();
				inputCount = EncogFileSection.parseInt(params,
						PersistConst.INPUT_COUNT);
				outputCount = EncogFileSection.parseInt(params,
						PersistConst.OUTPUT_COUNT);
				kernel = PersistBasicPNN.stringToKernel(params
						.get(PersistConst.KERNEL));
				outmodel = PersistBasicPNN.stringToOutputMode(params
						.get(PersistBasicPNN.PROPERTY_outputMode));
				error = EncogFileSection
						.parseDouble(params, PersistConst.ERROR);
				sigma = section.parseDoubleArray(params,
						PersistConst.SIGMA);
			}
			if (section.getSectionName().equals("PNN")
					&& section.getSubSectionName().equals("SAMPLES")) {
				for (final String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					int index = 0;
					final MLData inputData = new BasicMLData(inputCount);
					for (int i = 0; i < inputCount; i++) {
						inputData.setData(i,
								CSVFormat.EG_FORMAT.parse(cols.get(index++)));
					}
					final MLData idealData = new BasicMLData(inputCount);
					idealData.setData(0,CSVFormat.EG_FORMAT.parse(cols.get(index++)));
					final MLDataPair pair = new BasicMLDataPair(inputData,idealData);
					samples.add(pair);
				}
			}
		}

		final BasicPNN result = new BasicPNN(kernel, outmodel, inputCount,
				outputCount);
		if (networkParams != null) {
			result.getProperties().putAll(networkParams);
		}
		result.setSamples(samples);
		result.setError(error);
		if (sigma != null) {
			EngineArray.arrayCopy(sigma, result.getSigma());
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void save(final OutputStream os, final Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final BasicPNN pnn = (BasicPNN) obj;
		out.addSection("PNN");
		out.addSubSection("PARAMS");
		out.addProperties(pnn.getProperties());
		out.addSubSection("NETWORK");

		out.writeProperty(PersistConst.ERROR, pnn.getError());
		out.writeProperty(PersistConst.INPUT_COUNT, pnn.getInputCount());
		out.writeProperty(PersistConst.KERNEL,
				PersistBasicPNN.kernelToString(pnn.getKernel()));
		out.writeProperty(PersistConst.OUTPUT_COUNT, pnn.getOutputCount());
		out.writeProperty(PersistBasicPNN.PROPERTY_outputMode,
				PersistBasicPNN.outputModeToString(pnn.getOutputMode()));
		out.writeProperty(PersistConst.SIGMA, pnn.getSigma());

		out.addSubSection("SAMPLES");
		
		if (pnn.getSamples() != null) {
			for (final MLDataPair pair : pnn.getSamples()) {
				for (int i = 0; i < pair.getInput().size(); i++) {
					out.addColumn(pair.getInput().getData(i));
				}
				for (int i = 0; i < pair.getIdeal().size(); i++) {
					out.addColumn(pair.getIdeal().getData(i));
				}
				out.writeLine();
			}
		}

		out.flush();
	}
}
