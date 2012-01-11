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
package org.encog.neural.networks;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.flat.FlatNetwork;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.persist.PersistError;
import org.encog.util.csv.CSVFormat;

/**
 * Persist a basic network.
 *
 */
public class PersistBasicNetwork implements EncogPersistor {

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
		return "BasicNetwork";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object read(final InputStream is) {
		final BasicNetwork result = new BasicNetwork();
		final FlatNetwork flat = new FlatNetwork();
		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("PARAMS")) {
				final Map<String, String> params = section.parseParams();
				result.getProperties().putAll(params);
			}
			if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("NETWORK")) {
				final Map<String, String> params = section.parseParams();

				flat.setBeginTraining(EncogFileSection.parseInt(params,
						BasicNetwork.TAG_BEGIN_TRAINING));
				flat.setConnectionLimit(EncogFileSection.parseDouble(params,
						BasicNetwork.TAG_CONNECTION_LIMIT));
				flat.setContextTargetOffset(EncogFileSection.parseIntArray(
						params, BasicNetwork.TAG_CONTEXT_TARGET_OFFSET));
				flat.setContextTargetSize(EncogFileSection.parseIntArray(
						params, BasicNetwork.TAG_CONTEXT_TARGET_SIZE));
				flat.setEndTraining(EncogFileSection.parseInt(params,
						BasicNetwork.TAG_END_TRAINING));
				flat.setHasContext(EncogFileSection.parseBoolean(params,
						BasicNetwork.TAG_HAS_CONTEXT));
				flat.setInputCount(EncogFileSection.parseInt(params,
						PersistConst.INPUT_COUNT));
				flat.setLayerCounts(EncogFileSection.parseIntArray(params,
						BasicNetwork.TAG_LAYER_COUNTS));
				flat.setLayerFeedCounts(EncogFileSection.parseIntArray(params,
						BasicNetwork.TAG_LAYER_FEED_COUNTS));
				flat.setLayerContextCount(EncogFileSection.parseIntArray(
						params, BasicNetwork.TAG_LAYER_CONTEXT_COUNT));
				flat.setLayerIndex(EncogFileSection.parseIntArray(params,
						BasicNetwork.TAG_LAYER_INDEX));
				flat.setLayerOutput(section.parseDoubleArray(params,
						PersistConst.OUTPUT));
				flat.setLayerSums(new double[flat.getLayerOutput().length]);
				flat.setOutputCount(EncogFileSection.parseInt(params,
						PersistConst.OUTPUT_COUNT));
				flat.setWeightIndex(EncogFileSection.parseIntArray(params,
						BasicNetwork.TAG_WEIGHT_INDEX));
				flat.setWeights(section.parseDoubleArray(params,
						PersistConst.WEIGHTS));
				flat.setBiasActivation(section.parseDoubleArray(
						params, BasicNetwork.TAG_BIAS_ACTIVATION));
			} else if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("ACTIVATION")) {
				int index = 0;

				flat.setActivationFunctions(new ActivationFunction[flat
						.getLayerCounts().length]);

				for (final String line : section.getLines()) {
					ActivationFunction af = null;
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					final String name = "org.encog.engine.network.activation."
							+ cols.get(0);
					try {
						final Class<?> clazz = Class.forName(name);
						af = (ActivationFunction) clazz.newInstance();
					} catch (final ClassNotFoundException e) {
						throw new PersistError(e);
					} catch (final InstantiationException e) {
						throw new PersistError(e);
					} catch (final IllegalAccessException e) {
						throw new PersistError(e);
					}

					for (int i = 0; i < af.getParamNames().length; i++) {
						af.setParam(i,
								CSVFormat.EG_FORMAT.parse(cols.get(i + 1)));
					}

					flat.getActivationFunctions()[index++] = af;
				}
			}
		}

		result.getStructure().setFlat(flat);

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void save(final OutputStream os, final Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final BasicNetwork net = (BasicNetwork) obj;
		final FlatNetwork flat = net.getStructure().getFlat();
		out.addSection("BASIC");
		out.addSubSection("PARAMS");
		out.addProperties(net.getProperties());
		out.addSubSection("NETWORK");

		out.writeProperty(BasicNetwork.TAG_BEGIN_TRAINING,
				flat.getBeginTraining());
		out.writeProperty(BasicNetwork.TAG_CONNECTION_LIMIT,
				flat.getConnectionLimit());
		out.writeProperty(BasicNetwork.TAG_CONTEXT_TARGET_OFFSET,
				flat.getContextTargetOffset());
		out.writeProperty(BasicNetwork.TAG_CONTEXT_TARGET_SIZE,
				flat.getContextTargetSize());
		out.writeProperty(BasicNetwork.TAG_END_TRAINING, flat.getEndTraining());
		out.writeProperty(BasicNetwork.TAG_HAS_CONTEXT, flat.getHasContext());
		out.writeProperty(PersistConst.INPUT_COUNT, flat.getInputCount());
		out.writeProperty(BasicNetwork.TAG_LAYER_COUNTS, flat.getLayerCounts());
		out.writeProperty(BasicNetwork.TAG_LAYER_FEED_COUNTS,
				flat.getLayerFeedCounts());
		out.writeProperty(BasicNetwork.TAG_LAYER_CONTEXT_COUNT,
				flat.getLayerContextCount());
		out.writeProperty(BasicNetwork.TAG_LAYER_INDEX, flat.getLayerIndex());
		out.writeProperty(PersistConst.OUTPUT, flat.getLayerOutput());
		out.writeProperty(PersistConst.OUTPUT_COUNT, flat.getOutputCount());
		out.writeProperty(BasicNetwork.TAG_WEIGHT_INDEX, flat.getWeightIndex());
		out.writeProperty(PersistConst.WEIGHTS, flat.getWeights());
		out.writeProperty(BasicNetwork.TAG_BIAS_ACTIVATION,
				flat.getBiasActivation());
		out.addSubSection("ACTIVATION");
		for (final ActivationFunction af : flat.getActivationFunctions()) {
			out.addColumn(af.getClass().getSimpleName());
			for (int i = 0; i < af.getParams().length; i++) {
				out.addColumn(af.getParams()[i]);
			}
			out.writeLine();
		}

		out.flush();
	}

}
