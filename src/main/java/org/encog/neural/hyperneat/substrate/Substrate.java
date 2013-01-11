package org.encog.neural.hyperneat.substrate;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.neat.NEATNetwork;

public class Substrate {

	private final int dimensions;
	private final List<SubstrateNode> inputNodes = new ArrayList<SubstrateNode>();
	private final List<SubstrateNode> outputNodes = new ArrayList<SubstrateNode>();
	private final List<SubstrateNode> hiddenNodes = new ArrayList<SubstrateNode>();
	
	public Substrate(int theDimensions) {
		this.dimensions = theDimensions;
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
		SubstrateNode result = new SubstrateNode(0, this.dimensions);
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
		// TODO Auto-generated method stub
		
	}
	
	public NEATNetwork decode(NEATNetwork cppn) {
		return null;
	}

}
