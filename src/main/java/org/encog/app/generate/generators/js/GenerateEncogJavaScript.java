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
package org.encog.app.generate.generators.js;

import java.io.File;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.app.generate.AnalystCodeGenerationError;
import org.encog.app.generate.generators.AbstractGenerator;
import org.encog.app.generate.program.EncogGenProgram;
import org.encog.app.generate.program.EncogProgramNode;
import org.encog.app.generate.program.EncogTreeNode;
import org.encog.engine.network.activation.ActivationElliott;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.MLFactory;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.simple.EncogUtility;

public class GenerateEncogJavaScript extends AbstractGenerator {

	private boolean embed;

	private void embedNetwork(final EncogProgramNode node) {
		addBreak();

		final File methodFile = (File) node.getArgs().get(0).getValue();

		final MLMethod method = (MLMethod) EncogDirectoryPersistence
				.loadObject(methodFile);

		if (!(method instanceof MLFactory)) {
			throw new EncogError("Code generation not yet supported for: "
					+ method.getClass().getName());
		}

		final FlatNetwork flat = ((ContainsFlat) method).getFlat();

		// header
		final StringBuilder line = new StringBuilder();
		line.append("public static MLMethod ");
		line.append(node.getName());
		line.append("() {");
		indentLine(line.toString());

		// create factory
		line.setLength(0);

		addLine("var network = ENCOG.BasicNetwork.create( null );");
		addLine("network.inputCount = " + flat.getInputCount() + ";");
		addLine("network.outputCount = " + flat.getOutputCount() + ";");
		addLine("network.layerCounts = "
				+ toSingleLineArray(flat.getLayerCounts()) + ";");
		addLine("network.layerContextCount = "
				+ toSingleLineArray(flat.getLayerContextCount()) + ";");
		addLine("network.weightIndex = "
				+ toSingleLineArray(flat.getWeightIndex()) + ";");
		addLine("network.layerIndex = "
				+ toSingleLineArray(flat.getLayerIndex()) + ";");
		addLine("network.activationFunctions = "
				+ toSingleLineArray(flat.getActivationFunctions()) + ";");
		addLine("network.layerFeedCounts = "
				+ toSingleLineArray(flat.getLayerFeedCounts()) + ";");
		addLine("network.contextTargetOffset = "
				+ toSingleLineArray(flat.getContextTargetOffset()) + ";");
		addLine("network.contextTargetSize = "
				+ toSingleLineArray(flat.getContextTargetSize()) + ";");
		addLine("network.biasActivation = "
				+ toSingleLineArray(flat.getBiasActivation()) + ";");
		addLine("network.beginTraining = " + flat.getBeginTraining() + ";");
		addLine("network.endTraining=" + flat.getEndTraining() + ";");
		addLine("network.weights = WEIGHTS;");
		addLine("network.layerOutput = "
				+ toSingleLineArray(flat.getLayerOutput()) + ";");
		addLine("network.layerSums = " + toSingleLineArray(flat.getLayerSums())
				+ ";");

		// return
		addLine("return network;");

		unIndentLine("}");
	}

	private void embedTraining(final EncogProgramNode node) {

		final File dataFile = (File) node.getArgs().get(0).getValue();
		final MLDataSet data = EncogUtility.loadEGB2Memory(dataFile);

		// generate the input data

		indentLine("var INPUT_DATA = [");
		for (final MLDataPair pair : data) {
			final MLData item = pair.getInput();

			final StringBuilder line = new StringBuilder();

			NumberList.toList(CSVFormat.EG_FORMAT, line, item.getData());
			line.insert(0, "[ ");
			line.append(" ],");
			addLine(line.toString());
		}
		unIndentLine("];");

		addBreak();

		// generate the ideal data

		indentLine("var IDEAL_DATA = [");
		for (final MLDataPair pair : data) {
			final MLData item = pair.getIdeal();

			final StringBuilder line = new StringBuilder();

			NumberList.toList(CSVFormat.EG_FORMAT, line, item.getData());
			line.insert(0, "[ ");
			line.append(" ],");
			addLine(line.toString());
		}
		unIndentLine("];");
	}

	@Override
	public void generate(final EncogGenProgram program, final boolean shouldEmbed) {
		if (!shouldEmbed) {
			throw new AnalystCodeGenerationError(
					"Must embed when generating Javascript");
		}
		this.embed = shouldEmbed;
		generateForChildren(program);
	}

	private void generateArrayInit(final EncogProgramNode node) {
		final StringBuilder line = new StringBuilder();
		line.append("var ");
		line.append(node.getName());
		line.append(" = [");
		indentLine(line.toString());

		final double[] a = (double[]) node.getArgs().get(0).getValue();

		line.setLength(0);

		int lineCount = 0;
		for (int i = 0; i < a.length; i++) {
			line.append(CSVFormat.EG_FORMAT.format(a[i],
					Encog.DEFAULT_PRECISION));
			if (i < (a.length - 1)) {
				line.append(",");
			}

			lineCount++;
			if (lineCount >= 10) {
				addLine(line.toString());
				line.setLength(0);
				lineCount = 0;
			}
		}

		if (line.length() > 0) {
			addLine(line.toString());
			line.setLength(0);
		}

		unIndentLine("];");
	}

	private void generateClass(final EncogProgramNode node) {
		addBreak();

		addLine("<!DOCTYPE html>");
		addLine("<html>");
		addLine("<head>");
		addLine("<title>Encog Generated Javascript</title>");
		addLine("</head>");
		addLine("<body>");
		addLine("<script src=\"../encog.js\"></script>");
		addLine("<script src=\"../encog-widget.js\"></script>");
		addLine("<pre>");
		addLine("<script type=\"text/javascript\">");

		generateForChildren(node);

		addLine("</script>");
		addLine("<noscript>Your browser does not support JavaScript! Note: if you are trying to view this in Encog Workbench, right-click file and choose \"Open as Text\".</noscript>");
		addLine("</pre>");
		addLine("</body>");
		addLine("</html>");
	}

	private void generateComment(final EncogProgramNode commentNode) {
		addLine("// " + commentNode.getName());
	}

	private void generateConst(final EncogProgramNode node) {
		final StringBuilder line = new StringBuilder();
		line.append("var ");
		line.append(node.getName());
		line.append(" = \"");
		line.append(node.getArgs().get(0).getValue());
		line.append("\";");

		addLine(line.toString());
	}

	private void generateForChildren(final EncogTreeNode parent) {
		for (final EncogProgramNode node : parent.getChildren()) {
			generateNode(node);
		}
	}

	private void generateFunction(final EncogProgramNode node) {
		addBreak();

		final StringBuilder line = new StringBuilder();
		line.append("function ");
		line.append(node.getName());
		line.append("() {");
		indentLine(line.toString());

		generateForChildren(node);
		unIndentLine("}");
	}

	private void generateFunctionCall(final EncogProgramNode node) {
		addBreak();
		final StringBuilder line = new StringBuilder();
		if (node.getArgs().get(0).getValue().toString().length() > 0) {
			line.append("var ");
			line.append(node.getArgs().get(1).getValue().toString());
			line.append(" = ");
		}

		line.append(node.getName());
		line.append("();");
		addLine(line.toString());
	}

	private void generateMainFunction(final EncogProgramNode node) {
		addBreak();
		generateForChildren(node);
	}

	private void generateNode(final EncogProgramNode node) {
		switch (node.getType()) {
		case Comment:
			generateComment(node);
			break;
		case Class:
			generateClass(node);
			break;
		case MainFunction:
			generateMainFunction(node);
			break;
		case Const:
			generateConst(node);
			break;
		case StaticFunction:
			generateFunction(node);
			break;
		case FunctionCall:
			generateFunctionCall(node);
			break;
		case CreateNetwork:
			embedNetwork(node);
			break;
		case InitArray:
			generateArrayInit(node);
			break;
		case EmbedTraining:
			embedTraining(node);
			break;
		}
	}

	private String toSingleLineArray(
			final ActivationFunction[] activationFunctions) {
		final StringBuilder result = new StringBuilder();
		result.append('[');
		for (int i = 0; i < activationFunctions.length; i++) {
			if (i > 0) {
				result.append(',');
			}

			final ActivationFunction af = activationFunctions[i];
			if (af instanceof ActivationSigmoid) {
				result.append("ENCOG.ActivationSigmoid.create()");
			} else if (af instanceof ActivationTANH) {
				result.append("ENCOG.ActivationTANH.create()");
			} else if (af instanceof ActivationLinear) {
				result.append("ENCOG.ActivationLinear.create()");
			} else if (af instanceof ActivationElliott) {
				result.append("ENCOG.ActivationElliott.create()");
			} else if (af instanceof ActivationElliott) {
				result.append("ENCOG.ActivationElliott.create()");
			} else {
				throw new AnalystCodeGenerationError(
						"Unsupported activatoin function for code generation: "
								+ af.getClass().getSimpleName());
			}

		}
		result.append(']');
		return result.toString();
	}

	private String toSingleLineArray(final double[] d) {
		final StringBuilder line = new StringBuilder();
		line.append("[");
		for (int i = 0; i < d.length; i++) {
			line.append(CSVFormat.EG_FORMAT.format(d[i],
					Encog.DEFAULT_PRECISION));
			if (i < (d.length - 1)) {
				line.append(",");
			}
		}
		line.append("]");
		return line.toString();
	}

	private String toSingleLineArray(final int[] d) {
		final StringBuilder line = new StringBuilder();
		line.append("[");
		for (int i = 0; i < d.length; i++) {
			line.append(d[i]);
			if (i < (d.length - 1)) {
				line.append(",");
			}
		}
		line.append("]");
		return line.toString();
	}
}
