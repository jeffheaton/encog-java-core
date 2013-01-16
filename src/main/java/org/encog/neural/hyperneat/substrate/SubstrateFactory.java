package org.encog.neural.hyperneat.substrate;

public class SubstrateFactory {
	
	public static Substrate factorSandwichSubstrate(int inputEdgeSize, int outputEdgeSize) {
		Substrate result = new Substrate(3);
	
		// create the input layer
		double tick = (2.0 / inputEdgeSize) - 1;
		
		for(int row=0;row<inputEdgeSize;row++) {
			for(int col=0;col<inputEdgeSize;col++) {
				SubstrateNode inputNode = result.createInputNode();
				inputNode.getLocation()[0] = -1;
				inputNode.getLocation()[1] = row * tick;
				inputNode.getLocation()[2] = col * tick;
			}
		}
		
		// create the output layer (and connect to input layer)
		
		for(int orow=0;orow<inputEdgeSize;orow++) {
			for(int ocol=0;ocol<inputEdgeSize;ocol++) {
				SubstrateNode outputNode = result.createOutputNode();
				outputNode.getLocation()[0] = 1;
				outputNode.getLocation()[1] = orow * tick;
				outputNode.getLocation()[2] = ocol * tick;
				
				// link this output node to every input node
				for(SubstrateNode inputNode : result.getInputNodes()) {
					result.createLink(inputNode,outputNode);
				}
			}
		}
		
		return result;
	}
	
}
