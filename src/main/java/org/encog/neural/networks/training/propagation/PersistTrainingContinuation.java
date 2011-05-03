/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.neural.networks.training.propagation;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;

public class PersistTrainingContinuation implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "TrainingContinuation";
	}

	@Override
	public Object read(InputStream is) {
		TrainingContinuation result = new TrainingContinuation();
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("CONT")
					&& section.getSubSectionName().equals("PARAMS")) {
				Map<String, String> params = section.parseParams();
				for (String key : params.keySet()) {
					if (key.equalsIgnoreCase("type")) {
						result.setTrainingType(params.get(key));
					} else {
						double[] list = EncogFileSection.parseDoubleArray(
								params, key);
						result.put(key, list);
					}
				}
			}
		}

		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		TrainingContinuation cont = (TrainingContinuation) obj;
		out.addSection("CONT");
		out.addSubSection("PARAMS");
		out.writeProperty("type", cont.getTrainingType());
		for (String key : cont.getContents().keySet()) {
			double[] list = (double[]) cont.get(key);
			out.writeProperty(key, list);
		}
		out.flush();
	}

	@Override
	public int getFileVersion() {
		return 1;
	}

}
