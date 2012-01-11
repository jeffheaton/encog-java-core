/*
 * Encog(tm) Core v3.1 - Java Version
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
package org.encog.neural.neat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a NEAT neuron. Neat neurons are of a specific type, defined by the
 * NEATNeuronType enum. Usually NEAT uses a sigmoid activation function. The
 * activation response is used to allow the slope of the sigmoid to be evolved.
 *
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 *
 * http://www.cs.ucf.edu/~kstanley/
 *
 */
public class NEATNeuron implements Serializable {

	public static final String NEURON_ID = "neuronID";
	
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -2815145950124389743L;

	/**
	 * Inbound links to this neuron.
	 */
	private final List<NEATLink> inboundLinks = new ArrayList<NEATLink>();

	/**
	 * The neuron id.
	 */
	private long neuronID;

	/**
	 * The type of neuron this is.
	 */
	private NEATNeuronType neuronType;

	/**
	 * The output from the neuron.
	 */
	private double output;

	/**
	 * The outbound links for this neuron.
	 */
	private List<NEATLink> outputboundLinks = new ArrayList<NEATLink>();

	/**
	 * The x-position of this neuron. Used to split links, as well as display.
	 */
	private int posX;

	/**
	 * The y-position of this neuron. Used to split links, as well as display.
	 */
	private int posY;

	/**
	 * The split value for X. Used to track splits.
	 */
	private double splitX;

	/**
	 * The split value for Y. Used to track splits.
	 */
	private double splitY;

	/**
	 * The sum activation.
	 */
	private double sumActivation;

	/**
	 * Default constructor, used for persistence.
	 */
	public NEATNeuron() {

	}

	/**
	 * Construct a NEAT neuron.
	 *
	 * @param neuronType
	 *            The type of neuron.
	 * @param neuronID
	 *            The id of the neuron.
	 * @param splitY
	 *            The split for y.
	 * @param splitX
	 *            THe split for x.
	 * @param activationResponse
	 *            The activation response.
	 */
	public NEATNeuron(final NEATNeuronType neuronType, final long neuronID,
			final double splitY, final double splitX) {
		this.neuronType = neuronType;
		this.neuronID = neuronID;
		this.splitY = splitY;
		this.splitX = splitX;
		posX = 0;
		posY = 0;
		output = 0;
		sumActivation = 0;
	}

	/**
	 * @return the inbound links.
	 */
	public List<NEATLink> getInboundLinks() {
		return inboundLinks;
	}

	/**
	 * @return The neuron id.
	 */
	public long getNeuronID() {
		return neuronID;
	}

	/**
	 * @return the neuron type.
	 */
	public NEATNeuronType getNeuronType() {
		return neuronType;
	}

	/**
	 * @return The output from this neuron.
	 */
	public double getOutput() {
		return output;
	}

	/**
	 * @return The outbound links.
	 */
	public List<NEATLink> getOutputboundLinks() {
		return outputboundLinks;
	}

	/**
	 * @return The x position.
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return The y position.
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * @return The split x.
	 */
	public double getSplitX() {
		return splitX;
	}

	/**
	 * @return The split y.
	 */
	public double getSplitY() {
		return splitY;
	}

	/**
	 * @return The sum activation.
	 */
	public double getSumActivation() {
		return sumActivation;
	}

	/**
	 * Set the output.
	 *
	 * @param output
	 *            The output of the neuron.
	 */
	public void setOutput(final double output) {
		this.output = output;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[NEATNeuron:id=");
		result.append(neuronID);
		result.append(",type=");
		switch (neuronType) {
		case Input:
			result.append("I");
			break;
		case Output:
			result.append("O");
			break;
		case Bias:
			result.append("B");
			break;
		case Hidden:
			result.append("H");
			break;
		default:
			result.append("Unknown");
		}
		result.append("]");
		return result.toString();
	}
	
	public static NEATNeuronType string2NeuronType(String t)
	{
		String type = t.toLowerCase().trim();
		
		if( type.length()>0 ) {
			switch(type.charAt(0))
			{
				case 'i':
					return NEATNeuronType.Input;
				case 'o':
					return NEATNeuronType.Output;
				case 'h':
					return NEATNeuronType.Hidden;
				case 'b':
					return NEATNeuronType.Bias;
				case 'n':
					return NEATNeuronType.None;
			}
		}
		
		return null;
	}
	
	public static String neuronType2String(NEATNeuronType t)
	{
		switch( t )
		{
			case Input:
				return "I";
			case Bias:
				return "B";
			case Hidden:
				return "H";
			case Output:
				return "O";
			case None:
				return "N";
			default:
				return null;			
		}
	}
	
	

}
