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
package org.encog.app.generate.generators.java;

import java.io.File;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.app.generate.generators.AbstractGenerator;
import org.encog.app.generate.program.EncogGenProgram;
import org.encog.app.generate.program.EncogProgramNode;
import org.encog.app.generate.program.EncogTreeNode;
import org.encog.ml.MLFactory;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.simple.EncogUtility;

public class GenerateEncogJava extends AbstractGenerator {

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

		final MLFactory factoryMethod = (MLFactory) method;

		final String methodName = factoryMethod.getFactoryType();
		final String methodArchitecture = factoryMethod
				.getFactoryArchitecture();

		// header
		addInclude("org.encog.ml.MLMethod");
		addInclude("org.encog.persist.EncogDirectoryPersistence");

		final StringBuilder line = new StringBuilder();
		line.append("public static MLMethod ");
		line.append(node.getName());
		line.append("() {");
		indentLine(line.toString());

		// create factory
		line.setLength(0);
		addInclude("org.encog.ml.factory.MLMethodFactory");
		line.append("MLMethodFactory methodFactory = new MLMethodFactory();");
		addLine(line.toString());

		// factory create
		line.setLength(0);
		line.append("MLMethod result = ");

		line.append("methodFactory.create(");
		line.append("\"");
		line.append(methodName);
		line.append("\"");
		line.append(",");
		line.append("\"");
		line.append(methodArchitecture);
		line.append("\"");
		line.append(", 0, 0);");
		addLine(line.toString());

		line.setLength(0);
		addInclude("org.encog.ml.MLEncodable");
		line.append("((MLEncodable)result).decodeFromArray(WEIGHTS);");
		addLine(line.toString());

		// return
		addLine("return result;");

		unIndentLine("}");
	}

	private void embedTraining(final EncogProgramNode node) {

		final File dataFile = (File) node.getArgs().get(0).getValue();
		final MLDataSet data = EncogUtility.loadEGB2Memory(dataFile);

		// generate the input data

		indentLine("public static final double[][] INPUT_DATA = {");
		for (final MLDataPair pair : data) {
			final MLData item = pair.getInput();

			final StringBuilder line = new StringBuilder();

			NumberList.toList(CSVFormat.EG_FORMAT, line, item.getData());
			line.insert(0, "{ ");
			line.append(" },");
			addLine(line.toString());
		}
		unIndentLine("};");

		addBreak();

		// generate the ideal data

		indentLine("public static final double[][] IDEAL_DATA = {");
		for (final MLDataPair pair : data) {
			final MLData item = pair.getIdeal();

			final StringBuilder line = new StringBuilder();

			NumberList.toList(CSVFormat.EG_FORMAT, line, item.getData());
			line.insert(0, "{ ");
			line.append(" },");
			addLine(line.toString());
		}
		unIndentLine("};");
	}

	@Override
	public void generate(final EncogGenProgram program, final boolean shouldEmbed) {
		this.embed = shouldEmbed;
		generateForChildren(program);
		generateImports(program);
	}

	private void generateArrayInit(final EncogProgramNode node) {
		final StringBuilder line = new StringBuilder();
		line.append("public static final double[] ");
		line.append(node.getName());
		line.append(" = {");
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

		unIndentLine("};");
	}

	private void generateClass(final EncogProgramNode node) {
		addBreak();
		indentLine("public class " + node.getName() + " {");
		generateForChildren(node);
		unIndentLine("}");
	}

	private void generateComment(final EncogProgramNode commentNode) {
		addLine("// " + commentNode.getName());
	}

	private void generateConst(final EncogProgramNode node) {
		final StringBuilder line = new StringBuilder();
		line.append("public static final ");
		line.append(node.getArgs().get(1).getValue());
		line.append(" ");
		line.append(node.getName());
		line.append(" = \"");
		line.append(node.getArgs().get(0).getValue());
		line.append("\";");

		addLine(line.toString());
	}

	private void generateCreateNetwork(final EncogProgramNode node) {
		if (this.embed) {
			embedNetwork(node);
		} else {
			linkNetwork(node);
		}
	}

	private void generateEmbedTraining(final EncogProgramNode node) {
		if (this.embed) {
			embedTraining(node);
		}
	}

	private void generateForChildren(final EncogTreeNode parent) {
		for (final EncogProgramNode node : parent.getChildren()) {
			generateNode(node);
		}
	}

	private void generateFunction(final EncogProgramNode node) {
		addBreak();

		final StringBuilder line = new StringBuilder();
		line.append("public static void ");
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
			line.append(node.getArgs().get(0).getValue().toString());
			line.append(" ");
			line.append(node.getArgs().get(1).getValue().toString());
			line.append(" = ");
		}

		line.append(node.getName());
		line.append("();");
		addLine(line.toString());
	}

	private void generateImports(final EncogGenProgram program) {
		final StringBuilder imports = new StringBuilder();
		for (final String str : getIncludes()) {
			imports.append("import ");
			imports.append(str);
			imports.append(";\n");
		}

		imports.append("\n");

		addToBeginning(imports.toString());

	}

	private void generateLoadTraining(final EncogProgramNode node) {
		addBreak();

		final File methodFile = (File) node.getArgs().get(0).getValue();

		addInclude("org.encog.ml.data.MLDataSet");
		final StringBuilder line = new StringBuilder();
		line.append("public static MLDataSet createTraining() {");
		indentLine(line.toString());

		line.setLength(0);

		if (this.embed) {
			addInclude("org.encog.ml.data.basic.BasicMLDataSet");
			line.append("MLDataSet result = new BasicMLDataSet(INPUT_DATA,IDEAL_DATA);");
		} else {
			addInclude("org.encog.util.simple.EncogUtility");
			line.append("MLDataSet result = EncogUtility.loadEGB2Memory(new File(\"");
			line.append(methodFile.getAbsolutePath());
			line.append("\"));");
		}

		addLine(line.toString());

		// return
		addLine("return result;");

		unIndentLine("}");
	}

	private void generateMainFunction(final EncogProgramNode node) {
		addBreak();
		indentLine("public static void main(String[] args) {");
		generateForChildren(node);
		unIndentLine("}");
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
			generateCreateNetwork(node);
			break;
		case InitArray:
			generateArrayInit(node);
			break;
		case EmbedTraining:
			generateEmbedTraining(node);
			break;
		case LoadTraining:
			generateLoadTraining(node);
			break;
		}
	}

	private void linkNetwork(final EncogProgramNode node) {
		addBreak();

		final File methodFile = (File) node.getArgs().get(0).getValue();

		addInclude("org.encog.ml.MLMethod");
		final StringBuilder line = new StringBuilder();
		line.append("public static MLMethod ");
		line.append(node.getName());
		line.append("() {");
		indentLine(line.toString());

		line.setLength(0);
		line.append("MLMethod result = (MLMethod)EncogDirectoryPersistence.loadObject(new File(\"");
		line.append(methodFile.getAbsolutePath());
		line.append("\"));");
		addLine(line.toString());

		// return
		addLine("return result;");

		unIndentLine("}");
	}
}
