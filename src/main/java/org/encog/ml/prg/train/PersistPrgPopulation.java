/*
 * Encog(tm) Core v3.2 - Java Version
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
package org.encog.ml.prg.train;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;

/**
 * Persist a basic network.
 *
 */
public class PersistPrgPopulation implements EncogPersistor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getFileVersion() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPersistClassString() {
		return "PrgPopulation";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object read(final InputStream is) {
		EncogProgramContext context = new EncogProgramContext();
		
		final PrgPopulation result = new PrgPopulation(context);

		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("PARAMS")) {
				final Map<String, String> params = section.parseParams();
				result.getProperties().putAll(params);
			}
			if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("EPL-POPULATION")) {
				for(String line: section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					String code = cols.get(0);
					EncogProgram prg = new EncogProgram(context);
					prg.fromBase64(code);
					result.add(prg);
				}
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save(final OutputStream os, final Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final PrgPopulation pop = (PrgPopulation) obj;
		
		out.addSection("BASIC");
		out.addSubSection("PARAMS");
		out.addProperties(pop.getProperties());
		out.addSubSection("EPL-POPULATION");
		for(Genome genome: pop.getGenomes()) {
			EncogProgram prg = (EncogProgram)genome;
			out.addColumn(prg.toBase64());
			out.writeLine();
		}

		out.flush();
	}

}
