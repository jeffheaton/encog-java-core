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
package org.encog.neural.hyperneat.substrate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The substrate defines the structure of the produced HyperNEAT network.
 * 
 * A substrate is made up of nodes and links. A node has a location that is an
 * n-dimensional coordinate. Nodes are grouped into input and output clusters.
 * There can also be hidden neurons between these two.
 * 
 * A HyperNEAT network works by training a CPPN that produces the actual
 * resulting NEAT network. The size of the substrate can then be adjusted to
 * create larger networks than what the HyperNEAT network was originally trained
 * with.
 * 
 * -----------------------------------------------------------------------------
 * http://www.cs.ucf.edu/~kstanley/ Encog's NEAT implementation was drawn from
 * the following three Journal Articles. For more complete BibTeX sources, see
 * NEATNetwork.java.
 * 
 * Evolving Neural Networks Through Augmenting Topologies
 * 
 * Generating Large-Scale Neural Networks Through Discovering Geometric
 * Regularities
 * 
 * Automatic feature selection in neuroevolution
 */
public class Substrate implements Serializable {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The dimensions of the network.
	 */
	private final int dimensions;

	/**
	 * The input nodes.
	 */
	private final List<SubstrateNode> inputNodes = new ArrayList<SubstrateNode>();

	/**
	 * The output nodes.
	 */
	private final List<SubstrateNode> outputNodes = new ArrayList<SubstrateNode>();

	/**
	 * The hidden nodes.
	 */
	private final List<SubstrateNode> hiddenNodes = new ArrayList<SubstrateNode>();

	/**
	 * The links between nodes.
	 */
	private final List<SubstrateLink> links = new ArrayList<SubstrateLink>();

	/**
	 * The current neuron id.
	 */
	private int currentNeuronNumber;

	/**
	 * The number of activation cycles.
	 */
	private int activationCycles = 1;

	/**
	 * Construct a substrate with the specified number of dimensions in the
	 * input/output layers.
	 * 
	 * @param theDimensions The dimensions of the network.
	 */
	public Substrate(int theDimensions) {
		this.dimensions = theDimensions;
		this.currentNeuronNumber = 1;
	}

	/**
	 * @return the hiddenNodes
	 */
	public List<SubstrateNode> getHiddenNodes() {
		return hiddenNodes;
	}

	public int getDimensions() {
		return this.dimensions;
	}

	/**
	 * @return the inputNodes
	 */
	public List<SubstrateNode> getInputNodes() {
		return inputNodes;
	}

	/**
	 * @return the outputNodes
	 */
	public List<SubstrateNode> getOutputNodes() {
		return outputNodes;
	}

	public int getInputCount() {
		return this.inputNodes.size();
	}

	public int getOutputCount() {
		return this.outputNodes.size();
	}

	public SubstrateNode createNode() {
		SubstrateNode result = new SubstrateNode(this.currentNeuronNumber++,
				this.dimensions);
		return result;
	}

	public SubstrateNode createInputNode() {
		SubstrateNode result = createNode();
		this.inputNodes.add(result);
		return result;
	}

	public SubstrateNode createOutputNode() {
		SubstrateNode result = createNode();
		this.outputNodes.add(result);
		return result;
	}

	public SubstrateNode createHiddenNode() {
		SubstrateNode result = createNode();
		this.hiddenNodes.add(result);
		return result;
	}

	public void createLink(SubstrateNode inputNode, SubstrateNode outputNode) {
		SubstrateLink link = new SubstrateLink(inputNode, outputNode);
		this.links.add(link);
	}

	/**
	 * @return the links
	 */
	public List<SubstrateLink> getLinks() {
		return links;
	}

	/**
	 * @return The link count.
	 */
	public int getLinkCount() {
		return links.size();
	}

	public int getNodeCount() {
		return 1 + this.inputNodes.size() + this.outputNodes.size()
				+ this.hiddenNodes.size();
	}

	/**
	 * @return the activationCycles
	 */
	public int getActivationCycles() {
		return activationCycles;
	}

	/**
	 * @param activationCycles
	 *            the activationCycles to set
	 */
	public void setActivationCycles(int activationCycles) {
		this.activationCycles = activationCycles;
	}

	/**
	 * @return A list of all nodes that are connected to the bias neuron. This
	 *         is typically all non-input neurons.
	 */
	public List<SubstrateNode> getBiasedNodes() {
		List<SubstrateNode> result = new ArrayList<SubstrateNode>();
		result.addAll(this.hiddenNodes);
		result.addAll(this.outputNodes);
		return result;
	}
}
