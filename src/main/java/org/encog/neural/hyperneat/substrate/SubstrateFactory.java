package org.encog.neural.hyperneat.substrate;

public class SubstrateFactory {
	
	public static Substrate factorSandwichSubstrate(int inputEdgeSize, int outputEdgeSize) {
		Substrate result = new Substrate(3);
	
		double inputTick = 2.0 / inputEdgeSize;
		double outputTick = 2.0 / inputEdgeSize;
		double inputOrig = -1.0 + (inputTick/2.0);
		double outputOrig = -1.0 + (inputTick/2.0);
		
		// create the input layer

		for(int row=0;row<inputEdgeSize;row++) {
			for(int col=0;col<inputEdgeSize;col++) {
				SubstrateNode inputNode = result.createInputNode();
				inputNode.getLocation()[0] = -1;
				inputNode.getLocation()[1] = inputOrig + (row * inputTick);
				inputNode.getLocation()[2] = inputOrig + (col * inputTick);
			}
		}
		
		// create the output layer (and connect to input layer)
		
		for(int orow=0;orow<outputEdgeSize;orow++) {
			for(int ocol=0;ocol<outputEdgeSize;ocol++) {
				SubstrateNode outputNode = result.createOutputNode();
				outputNode.getLocation()[0] = 1;
				outputNode.getLocation()[1] = outputOrig + (orow * outputTick);
				outputNode.getLocation()[2] = outputOrig + (ocol * outputTick);
				
				// link this output node to every input node
				for(SubstrateNode inputNode : result.getInputNodes()) {
					result.createLink(inputNode,outputNode);
				}
			}
		}
		
		return result;
	}
	
}
