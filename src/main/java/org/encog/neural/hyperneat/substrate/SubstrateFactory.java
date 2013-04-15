/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2013 Heaton Research, Inc.
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
