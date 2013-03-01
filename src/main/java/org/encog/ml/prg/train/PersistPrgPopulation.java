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

import org.encog.EncogError;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.BasicSpecies;
import org.encog.ml.ea.species.Species;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.EncogOpcodeRegistry;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.util.csv.CSVFormat;

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

		final PrgPopulation result = new PrgPopulation(context, 0);

		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		int count = 0;
		Species lastSpecies=null;
		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("PARAMS")) {
				final Map<String, String> params = section.parseParams();
				result.getProperties().putAll(params);
			} else if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("EPL-POPULATION")) {
				for (String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);

					if (cols.get(0).equalsIgnoreCase("s")) {
						lastSpecies = new BasicSpecies();
						lastSpecies.setAge(Integer.parseInt(cols.get(1)));
						lastSpecies.setBestScore(CSVFormat.EG_FORMAT
								.parse(cols.get(2)));
						lastSpecies.setPopulation(result);
						lastSpecies.setGensNoImprovement(Integer.parseInt(cols.get(3)));
						result.getSpecies().add(lastSpecies);
					} else if (cols.get(0).equalsIgnoreCase("p")) {
						double score = 0;
						double adjustedScore = 0;

						if (cols.get(1).equalsIgnoreCase("nan")
								|| cols.get(2).equalsIgnoreCase("nan")) {
							score = Double.NaN;
							adjustedScore = Double.NaN;
						} else {
							score = CSVFormat.EG_FORMAT.parse(cols.get(1));
							adjustedScore = CSVFormat.EG_FORMAT.parse(cols
									.get(2));
						}

						String code = cols.get(3);
						EncogProgram prg = new EncogProgram(context);
						prg.fromBase64(code);
						prg.setScore(score);
						prg.setAdjustedScore(adjustedScore);
						if( lastSpecies==null ) {
							throw new EncogError("Have not defined a species yet");
						} else {
							lastSpecies.add(prg);
						}
						count++;
					}
				}
			} else if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("EPL-OPCODES")) {
				for (String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					String code = cols.get(0);
					int opcode = Integer.parseInt(code);
					EncogOpcodeRegistry.INSTANCE.register(context, opcode);
				}
			} else if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("EPL-SYMBOLIC")) {
				for (String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					String name = cols.get(0);
					context.defineVariable(name);
				}
			}
		}
		result.setPopulationSize(count);
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
		out.addSubSection("EPL-OPCODES");
		for (ProgramExtensionTemplate temp : pop.getContext().getFunctions()
				.generateOpcodeList()) {
			out.addColumn(temp.getOpcode());
			out.addColumn(temp.getName());
			out.writeLine();
		}
		out.addSubSection("EPL-SYMBOLIC");
		for (String name : pop.getContext().getDefinedVariables()) {
			out.addColumn(name);
			out.writeLine();
		}
		out.addSubSection("EPL-POPULATION");
		for (Species species : pop.getSpecies()) {
			out.addColumn("s");
			out.addColumn(species.getAge());
			out.addColumn(species.getBestScore());
			out.addColumn(species.getGensNoImprovement());
			out.writeLine();
			for (Genome genome : species.getMembers()) {
				EncogProgram prg = (EncogProgram) genome;
				out.addColumn("p");
				if (Double.isInfinite(prg.getScore())
						|| Double.isNaN(prg.getScore())) {
					out.addColumn("NaN");
					out.addColumn("NaN");
				} else {

					out.addColumn(prg.getScore());
					out.addColumn(prg.getAdjustedScore());
				}

				out.addColumn(prg.toBase64());
				out.writeLine();
			}
		}

		out.flush();
	}

}
