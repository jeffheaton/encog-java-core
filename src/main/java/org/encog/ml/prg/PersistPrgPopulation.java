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
package org.encog.ml.prg;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.BasicSpecies;
import org.encog.ml.ea.species.Species;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.train.PrgPopulation;
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
		final EncogProgramContext context = new EncogProgramContext();

		final PrgPopulation result = new PrgPopulation(context, 0);

		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		int count = 0;
		Species lastSpecies = null;
		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("PARAMS")) {
				final Map<String, String> params = section.parseParams();
				result.getProperties().putAll(params);
			} else if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("EPL-POPULATION")) {
				for (final String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);

					if (cols.get(0).equalsIgnoreCase("s")) {
						lastSpecies = new BasicSpecies();
						lastSpecies.setAge(Integer.parseInt(cols.get(1)));
						lastSpecies.setBestScore(CSVFormat.EG_FORMAT.parse(cols
								.get(2)));
						lastSpecies.setPopulation(result);
						lastSpecies.setGensNoImprovement(Integer.parseInt(cols
								.get(3)));
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

						final String code = cols.get(3);
						final EncogProgram prg = new EncogProgram(context);
						prg.compileEPL(code);
						prg.setScore(score);
						prg.setAdjustedScore(adjustedScore);
						if (lastSpecies == null) {
							throw new EncogError(
									"Have not defined a species yet");
						} else {
							lastSpecies.add(prg);
						}
						count++;
					}
				}
			} else if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("EPL-OPCODES")) {
				for (final String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					final String name = cols.get(0);
					final int args = Integer.parseInt(cols.get(1));
					result.getContext().getFunctions().addExtension(name,args);
				}
			} else if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("EPL-SYMBOLIC")) {
				for (final String line : section.getLines()) {
					final List<String> cols = EncogFileSection
							.splitColumns(line);
					final String name = cols.get(0);
					context.defineVariable(name);
				}
			}
		}
		result.setPopulationSize(count);

		// set the best genome, should be the first genome in the first species
		if (result.getSpecies().size() > 0) {
			Species species = result.getSpecies().get(0);
			if (species.getMembers().size() > 0) {
				result.setBestGenome(species.getMembers().get(0));
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
		out.addSubSection("EPL-OPCODES");
		for (final ProgramExtensionTemplate temp : pop.getContext()
				.getFunctions().getOpCodes()) {
			out.addColumn(temp.getName());
			out.addColumn(temp.getChildNodeCount());
			out.writeLine();
		}
		out.addSubSection("EPL-SYMBOLIC");
		for (final String name : pop.getContext().getDefinedVariables()) {
			out.addColumn(name);
			out.writeLine();
		}
		out.addSubSection("EPL-POPULATION");
		for (final Species species : pop.getSpecies()) {
			if (species.getMembers().size() > 0) {
				out.addColumn("s");
				out.addColumn(species.getAge());
				out.addColumn(species.getBestScore());
				out.addColumn(species.getGensNoImprovement());
				out.writeLine();
				for (final Genome genome : species.getMembers()) {
					final EncogProgram prg = (EncogProgram) genome;
					out.addColumn("p");
					if (Double.isInfinite(prg.getScore())
							|| Double.isNaN(prg.getScore())) {
						out.addColumn("NaN");
						out.addColumn("NaN");
					} else {

						out.addColumn(prg.getScore());
						out.addColumn(prg.getAdjustedScore());
					}

					out.addColumn(prg.generateEPL());
					out.writeLine();
				}
			}
		}

		out.flush();
	}

}
