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
package org.encog.app.generate.program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EncogProgramNode extends EncogTreeNode {
	private final List<EncogProgramArg> args = new ArrayList<EncogProgramArg>();
	private final NodeType type;
	private final String name;

	public EncogProgramNode(final EncogProgram theProgram,
			final EncogTreeNode theParent, final NodeType theNodeType,
			final String theName) {
		super(theProgram, theParent);
		this.type = theNodeType;
		this.name = theName;
	}

	public void addArg(final double argValue) {
		final EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}

	public void addArg(final int argValue) {
		final EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}

	public void addArg(final Object argValue) {
		final EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}

	public void addArg(final String argValue) {
		final EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}

	public EncogProgramNode createArray(final String name, final double[] a) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.InitArray, name);
		node.addArg(a);
		getChildren().add(node);
		return node;
	}

	public EncogProgramNode createFunction(final String theName) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.StaticFunction, theName);
		getChildren().add(node);
		return node;
	}

	public EncogProgramNode createFunctionCall(final EncogProgramNode funct,
			final String returnType, final String returnVariable) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.FunctionCall, funct.getName());
		node.addArg(returnType);
		node.addArg(returnVariable);
		getChildren().add(node);
		return node;

	}

	public EncogProgramNode createFunctionCall(final String name,
			final String returnType, final String returnVariable) {

		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.FunctionCall, name);
		node.addArg(returnType);
		node.addArg(returnVariable);
		getChildren().add(node);
		return node;

	}

	public EncogProgramNode createMainFunction() {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.MainFunction, null);
		getChildren().add(node);
		return node;
	}

	public EncogProgramNode createNetworkFunction(final String name,
			final File method) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.CreateNetwork, name);
		node.addArg(method);
		getChildren().add(node);
		return node;
	}

	public void defineConst(final EncogArgType type, final String name,
			final String value) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.Const, name);
		node.addArg(value);
		node.addArg(type.toString());
		getChildren().add(node);
	}

	public EncogProgramNode embedTraining(final File data) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.EmbedTraining, "");
		node.addArg(data);
		getChildren().add(node);
		return node;
	}

	public EncogProgramNode generateLoadTraining(final File data) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.LoadTraining, "");
		node.addArg(data);
		getChildren().add(node);
		return node;
	}

	public List<EncogProgramArg> getArgs() {
		return this.args;
	}

	public String getName() {
		return this.name;
	}

	public NodeType getType() {
		return this.type;
	}
}
