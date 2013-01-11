package org.encog.neural.hyperneat.substrate;

public class SubstrateFactory {
	
	public static Substrate factorSandwichSubstrate(int inputEdgeSize, int outputEdgeSize) {
		Substrate result = new Substrate(3);
	
		// create the input layer
		
		for(int row=0;row<inputEdgeSize;row++) {
			for(int col=0;col<inputEdgeSize;col++) {
				SubstrateNode inputNode = result.createInputNode();
			}
		}
		
		// create the output layer (and connect to input layer)
		
		for(int orow=0;orow<inputEdgeSize;orow++) {
			for(int ocol=0;ocol<inputEdgeSize;ocol++) {
				SubstrateNode outputNode = result.createOutputNode();
				
				// link this output node to every input node
				for(SubstrateNode inputNode : result.getInputNodes()) {
					result.createLink(inputNode,outputNode);
				}
			}
		}
		
		return result;
	}
	
}
