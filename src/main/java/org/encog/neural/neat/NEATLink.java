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
package org.encog.neural.neat;

import java.io.Serializable;

/**
 * Implements a link between two NEAT neurons.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATLink implements Serializable, Comparable<NEATLink> {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -4117045705080951946L;


	private int fromNeuron;
	private int toNeuron;
	private double weight;
		


	public NEATLink(int theFromNeuron, int theToNeuron, double theWeight) {
		this.fromNeuron = theFromNeuron;
		this.toNeuron = theToNeuron;
		this.weight = theWeight;
	}



	public int getFromNeuron() {
		return fromNeuron;
	}



	public void setFromNeuron(int fromNeuron) {
		this.fromNeuron = fromNeuron;
	}



	public int getToNeuron() {
		return toNeuron;
	}



	public void setToNeuron(int toNeuron) {
		this.toNeuron = toNeuron;
	}



	public double getWeight() {
		return weight;
	}



	public void setWeight(double weight) {
		this.weight = weight;
	}



	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[NEATLink: fromNeuron=");
		result.append(this.fromNeuron);
		result.append(", toNeuron=");
		result.append(this.toNeuron);
		result.append("]");
		return result.toString();
	}



	@Override
	public int compareTo(NEATLink other) {
		return Integer.compare(this.fromNeuron, other.toNeuron);
	}
}
