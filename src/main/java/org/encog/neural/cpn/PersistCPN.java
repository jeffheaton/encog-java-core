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
package org.encog.neural.cpn;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.mathutil.matrices.Matrix;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;

/**
 * Persist a CPN network.
 */
public class PersistCPN implements EncogPersistor {

	/**
	 * The input to instar property.
	 */
	final static String PROPERTY_inputToInstar = "inputToInstar";
	
	/**
	 * The instar to input property.
	 */
	final static String PROPERTY_instarToInput = "instarToInput";
	
	/**
	 * The winner count property.
	 */
	final static String PROPERTY_winnerCount = "winnerCount";

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
		return "CPN";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object read(final InputStream is) {
		Map<String, String> networkParams = null;
		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		int inputCount = 0;
		int instarCount = 0;
		int outputCount = 0;
		int winnerCount = 0;
		Matrix m1 = null;
		Matrix m2 = null;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("CPN")
					&& section.getSubSectionName().equals("PARAMS")) {
				networkParams = section.parseParams();
			}
			if (section.getSectionName().equals("CPN")
					&& section.getSubSectionName().equals("NETWORK")) {
				final Map<String, String> params = section.parseParams();

				inputCount = EncogFileSection.parseInt(params,
						PersistConst.INPUT_COUNT);
				instarCount = EncogFileSection.parseInt(params,
						PersistConst.INSTAR);
				outputCount = EncogFileSection.parseInt(params,
						PersistConst.OUTPUT_COUNT);
				winnerCount = EncogFileSection.parseInt(params,
						PersistCPN.PROPERTY_winnerCount);
				m1 = EncogFileSection.parseMatrix(params,
						PersistCPN.PROPERTY_inputToInstar);
				m2 = EncogFileSection.parseMatrix(params,
						PersistCPN.PROPERTY_instarToInput);
			}
		}

		final CPN result = new CPN(inputCount, instarCount, outputCount,
				winnerCount);
		result.getProperties().putAll(networkParams);
		result.getWeightsInputToInstar().set(m1);
		result.getWeightsInstarToOutstar().set(m2);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void save(final OutputStream os, final Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final CPN cpn = (CPN) obj;
		out.addSection("CPN");
		out.addSubSection("PARAMS");
		out.addProperties(cpn.getProperties());
		out.addSubSection("NETWORK");

		out.writeProperty(PersistConst.INPUT_COUNT, cpn.getInputCount());
		out.writeProperty(PersistConst.INSTAR, cpn.getInstarCount());
		out.writeProperty(PersistConst.OUTPUT_COUNT, cpn.getOutputCount());
		out.writeProperty(PersistCPN.PROPERTY_inputToInstar,
				cpn.getWeightsInputToInstar());
		out.writeProperty(PersistCPN.PROPERTY_instarToInput,
				cpn.getWeightsInstarToOutstar());
		out.writeProperty(PersistCPN.PROPERTY_winnerCount, 
				cpn.getWinnerCount());

		out.flush();
	}

}
