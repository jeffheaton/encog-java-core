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
package org.encog.neural.art;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;

/**
 * Persist an ART1 network.  
 */
public class PersistART1 implements EncogPersistor {

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
		return "ART1";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object read(final InputStream is) {
		final ART1 result = new ART1();
		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("ART1")
					&& section.getSubSectionName().equals("PARAMS")) {
				final Map<String, String> params = section.parseParams();
				result.getProperties().putAll(params);
			}
			if (section.getSectionName().equals("ART1")
					&& section.getSubSectionName().equals("NETWORK")) {
				final Map<String, String> params = section.parseParams();

				result.setA1(EncogFileSection.parseDouble(params,
						ART.PROPERTY_A1));
				result.setB1(EncogFileSection.parseDouble(params,
						ART.PROPERTY_B1));
				result.setC1(EncogFileSection.parseDouble(params,
						ART.PROPERTY_C1));
				result.setD1(EncogFileSection.parseDouble(params,
						ART.PROPERTY_D1));
				result.setF1Count(EncogFileSection.parseInt(params,
						PersistConst.PROPERTY_F1_COUNT));
				result.setF2Count(EncogFileSection.parseInt(params,
						PersistConst.PROPERTY_F2_COUNT));
				result.setNoWinner(EncogFileSection.parseInt(params,
						ART.PROPERTY_NO_WINNER));
				result.setL(EncogFileSection
						.parseDouble(params, ART.PROPERTY_L));
				result.setVigilance(EncogFileSection.parseDouble(params,
						ART.PROPERTY_VIGILANCE));
				result.setWeightsF1toF2(EncogFileSection.parseMatrix(params,
						PersistConst.PROPERTY_WEIGHTS_F1_F2));
				result.setWeightsF2toF1(EncogFileSection.parseMatrix(params,
						PersistConst.PROPERTY_WEIGHTS_F2_F1));

			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void save(final OutputStream os, final Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final ART1 art1 = (ART1) obj;
		out.addSection("ART1");
		out.addSubSection("PARAMS");
		out.addProperties(art1.getProperties());
		out.addSubSection("NETWORK");

		out.writeProperty(ART.PROPERTY_A1, art1.getA1());
		out.writeProperty(ART.PROPERTY_B1, art1.getB1());
		out.writeProperty(ART.PROPERTY_C1, art1.getC1());
		out.writeProperty(ART.PROPERTY_D1, art1.getD1());
		out.writeProperty(PersistConst.PROPERTY_F1_COUNT, art1.getF1Count());
		out.writeProperty(PersistConst.PROPERTY_F2_COUNT, art1.getF2Count());
		out.writeProperty(ART.PROPERTY_NO_WINNER, art1.getNoWinner());
		out.writeProperty(ART.PROPERTY_L, art1.getL());
		out.writeProperty(ART.PROPERTY_VIGILANCE, art1.getVigilance());
		out.writeProperty(PersistConst.PROPERTY_WEIGHTS_F1_F2,
				art1.getWeightsF1toF2());
		out.writeProperty(PersistConst.PROPERTY_WEIGHTS_F2_F1,
				art1.getWeightsF2toF1());

		out.flush();

	}

}
