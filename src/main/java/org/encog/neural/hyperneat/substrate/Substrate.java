package org.encog.neural.hyperneat.substrate;

import java.util.ArrayList;
import java.util.List;

public class Substrate {

	private final int dimensions;
	private final List<SubstrateNode> inputNodes = new ArrayList<SubstrateNode>();
	private final List<SubstrateNode> outputNodes = new ArrayList<SubstrateNode>();
	private final List<SubstrateNode> hiddenNodes = new ArrayList<SubstrateNode>();
	private final List<SubstrateLink> links = new ArrayList<SubstrateLink>();
	private int currentNeuronNumber;
	
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
		SubstrateNode result = new SubstrateNode(this.currentNeuronNumber++, this.dimensions);
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

	public int getLinkCount() {
		return links.size();
	}

	public int getNodeCount() {
		return 1+this.inputNodes.size()+this.outputNodes.size()+this.hiddenNodes.size();
	}
}
