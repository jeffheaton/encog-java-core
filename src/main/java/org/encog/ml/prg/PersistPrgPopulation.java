/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.util.csv.CSVFormat;

/**
 * Persist the Encog population.
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
	 * Get the type string for the specified variable mapping.
	 * 
	 * @param mapping
	 *            The mapping.
	 * @return The value.
	 */
	private String getType(final VariableMapping mapping) {
		switch (mapping.getVariableType()) {
		case floatingType:
			return "f";
		case stringType:
			return "s";
		case booleanType:
			return "b";
		case intType:
			return "i";
		case enumType:
			return "e";
		}
		throw new EncogError("Unknown type: "
				+ mapping.getVariableType().toString());
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
						prg.setSpecies(lastSpecies);
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
					result.getContext().getFunctions().addExtension(name, args);
				}
			} else if (section.getSectionName().equals("BASIC")
					&& section.getSubSectionName().equals("EPL-SYMBOLIC")) {
				boolean first = true;
				for (final String line : section.getLines()) {
					if (!first) {
						final List<String> cols = EncogFileSection
								.splitColumns(line);
						final String name = cols.get(0);
						final String t = cols.get(1);
						ValueType vt = null;

						if (t.equalsIgnoreCase("f")) {
							vt = ValueType.floatingType;
						} else if (t.equalsIgnoreCase("b")) {
							vt = ValueType.booleanType;
						} else if (t.equalsIgnoreCase("i")) {
							vt = ValueType.intType;
						} else if (t.equalsIgnoreCase("s")) {
							vt = ValueType.stringType;
						} else if (t.equalsIgnoreCase("e")) {
							vt = ValueType.enumType;
						}

						final int enumType = Integer.parseInt(cols.get(2));
						final int enumCount = Integer.parseInt(cols.get(3));
						final VariableMapping mapping = new VariableMapping(
								name, vt, enumType, enumCount);
						if (mapping.getName().length() > 0) {
							result.getContext().defineVariable(mapping);
						} else {
							result.getContext().setResult(mapping);
						}
					} else {
						first = false;
					}
				}
			}
		}
		result.setPopulationSize(count);

		// set the best genome, should be the first genome in the first species
		if (result.getSpecies().size() > 0) {
			final Species species = result.getSpecies().get(0);
			if (species.getMembers().size() > 0) {
				result.setBestGenome(species.getMembers().get(0));
			}

			// set the leaders
			for (final Species sp : result.getSpecies()) {
				if (sp.getMembers().size() > 0) {
					sp.setLeader(sp.getMembers().get(0));
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
		out.addSubSection("EPL-OPCODES");
		for (final ProgramExtensionTemplate temp : pop.getContext()
				.getFunctions().getOpCodes()) {
			out.addColumn(temp.getName());
			out.addColumn(temp.getChildNodeCount());
			out.writeLine();
		}
		out.addSubSection("EPL-SYMBOLIC");
		out.addColumn("name");
		out.addColumn("type");
		out.addColumn("enum");
		out.addColumn("enum_type");
		out.addColumn("enum_count");
		out.writeLine();

		// write the first line, the result
		out.addColumn("");
		out.addColumn(getType(pop.getContext().getResult()));
		out.addColumn(pop.getContext().getResult().getEnumType());
		out.addColumn(pop.getContext().getResult().getEnumValueCount());
		out.writeLine();

		// write the next lines, the variables
		for (final VariableMapping mapping : pop.getContext()
				.getDefinedVariables()) {
			out.addColumn(mapping.getName());
			out.addColumn(getType(mapping));
			out.addColumn(mapping.getEnumType());
			out.addColumn(mapping.getEnumValueCount());
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
