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
package org.encog.neural.thermal;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

/**
 * Persist the Boltzmann machine.
 *
 */
public class PersistBoltzmann implements EncogPersistor {

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
		return "BoltzmannMachine";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object read(final InputStream is) {
		final BoltzmannMachine result = new BoltzmannMachine();
		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("BOLTZMANN")
					&& section.getSubSectionName().equals("PARAMS")) {
				final Map<String, String> params = section.parseParams();
				result.getProperties().putAll(params);
			}
			if (section.getSectionName().equals("BOLTZMANN")
					&& section.getSubSectionName().equals("NETWORK")) {
				final Map<String, String> params = section.parseParams();
				result.setWeights(NumberList.fromList(CSVFormat.EG_FORMAT,
						params.get(PersistConst.WEIGHTS)));
				result.setCurrentState(NumberList.fromList(CSVFormat.EG_FORMAT,
						params.get(PersistConst.OUTPUT)));
				result.setNeuronCount(EncogFileSection.parseInt(params,
						PersistConst.NEURON_COUNT));

				result.setThreshold(NumberList.fromList(CSVFormat.EG_FORMAT,
						params.get(PersistConst.THRESHOLDS)));
				result.setAnnealCycles(EncogFileSection.parseInt(params,
						BoltzmannMachine.ANNEAL_CYCLES));
				result.setRunCycles(EncogFileSection.parseInt(params,
						BoltzmannMachine.RUN_CYCLES));
				result.setTemperature(EncogFileSection.parseDouble(params,
						PersistConst.TEMPERATURE));
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
		final BoltzmannMachine boltz = (BoltzmannMachine) obj;
		out.addSection("BOLTZMANN");
		out.addSubSection("PARAMS");
		out.addProperties(boltz.getProperties());
		out.addSubSection("NETWORK");
		out.writeProperty(PersistConst.WEIGHTS, boltz.getWeights());
		out.writeProperty(PersistConst.OUTPUT, boltz.getCurrentState()
				.getData());
		out.writeProperty(PersistConst.NEURON_COUNT, boltz.getNeuronCount());

		out.writeProperty(PersistConst.THRESHOLDS, boltz.getThreshold());
		out.writeProperty(BoltzmannMachine.ANNEAL_CYCLES,
				boltz.getAnnealCycles());
		out.writeProperty(BoltzmannMachine.RUN_CYCLES, boltz.getRunCycles());
		out.writeProperty(PersistConst.TEMPERATURE, boltz.getTemperature());

		out.flush();

	}

}
