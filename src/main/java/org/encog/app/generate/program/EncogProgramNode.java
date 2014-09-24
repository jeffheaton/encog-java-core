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
package org.encog.app.generate.program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A node that holds a program.
 * 
 */
public class EncogProgramNode extends EncogTreeNode {
	/**
	 * The argements to the program.
	 */
	private final List<EncogProgramArg> args = new ArrayList<EncogProgramArg>();

	/**
	 * The type of node that this is.
	 */
	private final NodeType type;

	/**
	 * The name od this node.
	 */
	private final String name;

	/**
	 * Construct the program node.
	 * 
	 * @param theProgram
	 *            THe program.
	 * @param theParent
	 *            The parent.
	 * @param theNodeType
	 *            The node type.
	 * @param theName
	 *            The name of the node.
	 */
	public EncogProgramNode(final EncogGenProgram theProgram,
			final EncogTreeNode theParent, final NodeType theNodeType,
			final String theName) {
		super(theProgram, theParent);
		this.type = theNodeType;
		this.name = theName;
	}

	/**
	 * Add a double argument.
	 * 
	 * @param argValue
	 *            The argument value.
	 */
	public void addArg(final double argValue) {
		final EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}

	/**
	 * Add an int argument.
	 * 
	 * @param argValue
	 *            The argument value.
	 */
	public void addArg(final int argValue) {
		final EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}

	/**
	 * Add an object argument.
	 * 
	 * @param argValue
	 *            The argument value.
	 */
	public void addArg(final Object argValue) {
		final EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}

	/**
	 * Add a string argument.
	 * 
	 * @param argValue
	 *            The argument value.
	 */
	public void addArg(final String argValue) {
		final EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}

	/**
	 * Create an array.
	 * 
	 * @param name
	 *            THe name of the array.
	 * @param a
	 *            The value to init the array to.
	 * @return The newly creatred array.
	 */
	public EncogProgramNode createArray(final String name, final double[] a) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.InitArray, name);
		node.addArg(a);
		getChildren().add(node);
		return node;
	}

	/**
	 * Create a function.
	 * 
	 * @param theName
	 *            The name of the function.
	 * @return The newly created function.
	 */
	public EncogProgramNode createFunction(final String theName) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.StaticFunction, theName);
		getChildren().add(node);
		return node;
	}

	/**
	 * Create a function call.
	 * 
	 * @param funct
	 *            The function to call.
	 * @param returnType
	 *            The type returned.
	 * @param returnVariable
	 *            The value to assigne the function call to.
	 * @return The newly created function call.
	 */
	public EncogProgramNode createFunctionCall(final EncogProgramNode funct,
			final String returnType, final String returnVariable) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.FunctionCall, funct.getName());
		node.addArg(returnType);
		node.addArg(returnVariable);
		getChildren().add(node);
		return node;

	}

	/**
	 * Create a function call.
	 * 
	 * @param name
	 *            The name of the function to call.
	 * @param returnType
	 *            The return type.
	 * @param returnVariable
	 *            The variable to assign the function to.
	 * @return The newly created function call.
	 */
	public EncogProgramNode createFunctionCall(final String name,
			final String returnType, final String returnVariable) {

		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.FunctionCall, name);
		node.addArg(returnType);
		node.addArg(returnVariable);
		getChildren().add(node);
		return node;

	}

	/**
	 * Create a new main function.
	 * 
	 * @return The newly created main function.
	 */
	public EncogProgramNode createMainFunction() {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.MainFunction, null);
		getChildren().add(node);
		return node;
	}

	/**
	 * Create a new network function.
	 * 
	 * @param name
	 *            The name of the network function.
	 * @param method
	 *            The method to call.
	 * @return The newly created network function.
	 */
	public EncogProgramNode createNetworkFunction(final String name,
			final File method) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.CreateNetwork, name);
		node.addArg(method);
		getChildren().add(node);
		return node;
	}

	/**
	 * Define a const.
	 * 
	 * @param type
	 *            The type of const.
	 * @param name
	 *            The name of the const.
	 * @param value
	 *            The value of the const.
	 */
	public void defineConst(final EncogArgType type, final String name,
			final String value) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.Const, name);
		node.addArg(value);
		node.addArg(type.toString());
		getChildren().add(node);
	}

	/**
	 * Embed training data.
	 * 
	 * @param data
	 *            The training data to embed.
	 * @return The newly created embeded training data.
	 */
	public EncogProgramNode embedTraining(final File data) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.EmbedTraining, "");
		node.addArg(data);
		getChildren().add(node);
		return node;
	}

	/**
	 * Load the training data.
	 * 
	 * @param data
	 *            The data.
	 * @return The newly created data load.
	 */
	public EncogProgramNode generateLoadTraining(final File data) {
		final EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.LoadTraining, "");
		node.addArg(data);
		getChildren().add(node);
		return node;
	}

	/**
	 * @return The args.
	 */
	public List<EncogProgramArg> getArgs() {
		return this.args;
	}

	/**
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The type.
	 */
	public NodeType getType() {
		return this.type;
	}
}
