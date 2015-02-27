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
package org.encog.app.generate.generators.mql4;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.generate.AnalystCodeGenerationError;
import org.encog.app.generate.generators.AbstractTemplateGenerator;
import org.encog.ml.MLMethod;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.EngineArray;

public class GenerateMQL4 extends AbstractTemplateGenerator {

	@Override
	public String getNullArray() {
		return "{-1}";
	}

	@Override
	public String getTemplatePath() {
		return "org/encog/data/mt4.mql4";
	}

	private void processCalc() {
		AnalystField firstOutputField = null;
		final int barsNeeded = Math.abs(getAnalyst().determineMinTimeSlice());

		final int inputCount = getAnalyst().determineInputCount();
		final int outputCount = getAnalyst().determineOutputCount();

		setIndentLevel(2);
		addLine("if( _inputCount>0 && Bars>=" + barsNeeded + " )");
		addLine("{");
		indentIn();
		addLine("double input[" + inputCount + "];");
		addLine("double output[" + outputCount + "];");

		int idx = 0;
		for (final AnalystField field : getAnalyst().getScript().getNormalize()
				.getNormalizedFields()) {
			if (field.isInput()) {

				final DataField df = getAnalyst().getScript().findDataField(
						field.getName());
				String str;

				switch (field.getAction()) {
				case PassThrough:
					str = EngineArray.replace(df.getSource(), "##", "pos+"
							+ (-field.getTimeSlice()));
					addLine("input[" + idx + "]=" + str + ";");
					idx++;
					break;
				case Normalize:
					str = EngineArray.replace(df.getSource(), "##", "pos+"
							+ (-field.getTimeSlice()));
					addLine("input[" + idx + "]=Norm(" + str + ","
							+ field.getNormalizedHigh() + ","
							+ field.getNormalizedLow() + ","
							+ field.getActualHigh() + ","
							+ field.getActualLow() + ");");
					idx++;
					break;
				case Ignore:
					break;
				default:
					throw new AnalystCodeGenerationError(
							"Can't generate Ninjascript code, unsupported normalizatoin action: "
									+ field.getAction().toString());
				}
			}
			if (field.isOutput()) {
				if (firstOutputField == null) {
					firstOutputField = field;
				}
			}
		}

		if (firstOutputField == null) {
			throw new AnalystCodeGenerationError(
					"Could not find an output field.");
		}

		addLine("Compute(input,output);");

		addLine("ExtMapBuffer1[pos] = DeNorm(output[0]" + ","
				+ firstOutputField.getNormalizedHigh() + ","
				+ firstOutputField.getNormalizedLow() + ","
				+ firstOutputField.getActualHigh() + ","
				+ firstOutputField.getActualLow() + ");");
		indentOut();
		addLine("}");
		setIndentLevel(2);

	}

	private void processHeaders() {
		final DataField[] fields = getAnalyst().getScript().getFields();

		final StringBuilder line = new StringBuilder();
		line.append("FileWrite(iHandle");

		for (final DataField df : fields) {
			line.append(",");
			line.append("\"");
			line.append(df.getName());
			line.append("\"");
		}

		line.append(");");
		addLine(line.toString());
	}

	private void processMainBlock() {
		final EncogAnalyst analyst = getAnalyst();

		final String processID = analyst.getScript().getProperties()
				.getPropertyString(ScriptProperties.PROCESS_CONFIG_SOURCE_FILE);

		final String methodID = analyst
				.getScript()
				.getProperties()
				.getPropertyString(
						ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		final File methodFile = analyst.getScript().resolveFilename(methodID);

		final File processFile = analyst.getScript().resolveFilename(processID);

		MLMethod method = null;
		int[] contextTargetOffset = null;
		int[] contextTargetSize = null;
		boolean hasContext = false;
		int inputCount = 0;
		int[] layerContextCount = null;
		int[] layerCounts = null;
		int[] layerFeedCounts = null;
		int[] layerIndex = null;
		double[] layerOutput = null;
		double[] layerSums = null;
		int outputCount = 0;
		int[] weightIndex = null;
		double[] weights = null;
		int neuronCount = 0;
		int layerCount = 0;
		int[] activation = null;
		double[] p = null;

		if (methodFile.exists()) {
			method = (MLMethod) EncogDirectoryPersistence
					.loadObject(methodFile);
			final FlatNetwork flat = ((BasicNetwork) method).getFlat();

			contextTargetOffset = flat.getContextTargetOffset();
			contextTargetSize = flat.getContextTargetSize();
			hasContext = flat.getHasContext();
			inputCount = flat.getInputCount();
			layerContextCount = flat.getLayerContextCount();
			layerCounts = flat.getLayerCounts();
			layerFeedCounts = flat.getLayerFeedCounts();
			layerIndex = flat.getLayerIndex();
			layerOutput = flat.getLayerOutput();
			layerSums = flat.getLayerSums();
			outputCount = flat.getOutputCount();
			weightIndex = flat.getWeightIndex();
			weights = flat.getWeights();
			activation = createActivations(flat);
			p = createParams(flat);
			neuronCount = flat.getLayerOutput().length;
			layerCount = flat.getLayerCounts().length;
		}

		setIndentLevel(2);
		indentIn();
		addNameValue("string EXPORT_FILENAME", "\"" + processFile.getName()
				+ "\"");

		addNameValue("int _neuronCount", neuronCount);
		addNameValue("int _layerCount", layerCount);
		addNameValue("int _contextTargetOffset[]", contextTargetOffset);
		addNameValue("int _contextTargetSize[]", contextTargetSize);
		addNameValue("bool _hasContext", hasContext ? "true" : "false");
		addNameValue("int _inputCount", inputCount);
		addNameValue("int _layerContextCount[]", layerContextCount);
		addNameValue("int _layerCounts[]", layerCounts);
		addNameValue("int _layerFeedCounts[]", layerFeedCounts);
		addNameValue("int _layerIndex[]", layerIndex);
		addNameValue("double _layerOutput[]", layerOutput);
		addNameValue("double _layerSums[]", layerSums);
		addNameValue("int _outputCount", outputCount);
		addNameValue("int _weightIndex[]", weightIndex);
		addNameValue("double _weights[]", weights);
		addNameValue("int _activation[]", activation);
		addNameValue("double _p[]", p);

		indentOut();
		setIndentLevel(0);
	}

	private void processObtain() {
		setIndentLevel(3);

		addLine("FileWrite(iHandle, when,");

		final DataField[] fields = getAnalyst().getScript().getFields();
		String lastLine = null;
		for (final DataField field : fields) {
			final DataField df = field;
			if (!df.getName().equalsIgnoreCase("time")
					&& !df.getName().equalsIgnoreCase("prediction")) {
				final String str = EngineArray.replace(df.getSource(), "##",
						"pos");
				if (lastLine != null) {
					addLine(lastLine + ",");
				}
				lastLine = str;
			}
		}

		if (lastLine != null) {
			addLine(lastLine);
		}
		addLine(");");
		setIndentLevel(0);
	}

	@Override
	public void processToken(final String command) {
		if (command.equalsIgnoreCase("MAIN-BLOCK")) {
			processMainBlock();
		} else if (command.equals("CALC")) {
			processCalc();
		} else if (command.equals("OBTAIN")) {
			processObtain();
		} else if (command.equals("HEADERS")) {
			processHeaders();
		}
		setIndentLevel(0);
	}

}
