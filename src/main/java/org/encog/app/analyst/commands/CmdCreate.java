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
package org.encog.app.analyst.commands;

import java.io.File;
import java.util.Random;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.ml.ScriptOpcode;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.MLMethod;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.data.buffer.EncogEGBFile;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.prg.VariableMapping;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.ml.prg.extension.EncogOpcodeRegistry;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.generator.RampedHalfAndHalf;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.logging.EncogLogging;

/**
 * The Encog Analyst create command. This command is used to create a Machine
 * Learning method.
 * 
 */
public class CmdCreate extends Cmd {

	/**
	 * The name of this command.
	 */
	public static final String COMMAND_NAME = "CREATE";

	/**
	 * Construct the create command.
	 * 
	 * @param theAnalyst
	 *            The analyst to use.
	 */
	public CmdCreate(final EncogAnalyst theAnalyst) {
		super(theAnalyst);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean executeCommand(final String args) {
		// get filenames
		final String trainingID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_TRAINING_FILE);
		final String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		final File trainingFile = getScript().resolveFilename(trainingID);
		final File resourceFile = getScript().resolveFilename(resourceID);

		final String type = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_TYPE);
		final String arch = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_ARCHITECTURE);

		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Beginning create");
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "training file:"
				+ trainingID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "resource file:"
				+ resourceID);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "type:" + type);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "arch:" + arch);

		final EncogEGBFile egb = new EncogEGBFile(trainingFile);
		egb.open();
		final int input = egb.getInputCount();
		final int ideal = egb.getIdealCount();
		egb.close();

		final MLMethodFactory factory = new MLMethodFactory();
		final MLMethod obj = factory.create(type, arch, input, ideal);

		if (obj instanceof BayesianNetwork) {
			final String query = getProp().getPropertyString(
					ScriptProperties.ML_CONFIG_QUERY);
			((BayesianNetwork) obj).defineClassificationStructure(query);
		} else if (obj instanceof PrgPopulation) {
			handlePrgPopulation((PrgPopulation) obj);

		}

		EncogDirectoryPersistence.saveObject(resourceFile, obj);

		return false;
	}

	private void handlePrgPopulation(PrgPopulation pop) {
		
		// create the variables
		int classType = 0;
		pop.getContext().clearDefinedVariables();
		for(AnalystField field : getScript().getNormalize().getNormalizedFields() ) {
			DataField df = getScript().findDataField(field.getName());
			String varName = field.getName();
			
			VariableMapping mapping;
			
			switch( field.getAction() ) {
				case Ignore:
					mapping = null;
					break;
				case PassThrough:
					if( df.isInteger() ) {
						mapping = new VariableMapping(varName, ValueType.intType);
					} else if( df.isReal() ) {
						mapping = new VariableMapping(varName, ValueType.floatingType);
					} else {
						mapping = new VariableMapping(varName, ValueType.stringType);
					}
					break;
				case Equilateral:
				case OneOf:
				case Normalize:
					mapping = new VariableMapping(varName, ValueType.floatingType);
					break;
				case SingleField:
					if( df.isClass() ) {
						mapping = new VariableMapping(varName, ValueType.enumType, classType++, df.getClassMembers().size() );
					} else if( df.isInteger() ) {
						mapping = new VariableMapping(varName, ValueType.intType );
					} else {
						mapping = new VariableMapping(varName, ValueType.floatingType);
					}
					break;
				default:
					throw new AnalystError("Unknown normalization action: " + field.getAction().toString());
			}
			
			if( field.isOutput() ) {
				pop.getContext().setResult(mapping);
			} else {
				pop.getContext().defineVariable(mapping);
			}
		}
		
		
		
		// populate the opcodes
		if (this.getScript().getOpcodes().size() > 0) {
			for (ScriptOpcode op : this.getScript().getOpcodes()) {
				ProgramExtensionTemplate temp = EncogOpcodeRegistry.INSTANCE
						.findOpcode(op.getName(), op.getArgCount());
				pop.getContext().getFunctions().addExtension(temp);
			}
		}
		
		// generate initial population
		RampedHalfAndHalf generate = new RampedHalfAndHalf(pop.getContext(), 1, 6);		
		generate.generate(new Random(), pop);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return CmdCreate.COMMAND_NAME;
	}

}
