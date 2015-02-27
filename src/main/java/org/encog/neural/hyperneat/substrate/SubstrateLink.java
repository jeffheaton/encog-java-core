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

/**
 * A substrate link.
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
public class SubstrateLink implements Serializable {
	
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The source.
	 */
	private final SubstrateNode source;
	
	/**
	 * The target.
	 */
	private final SubstrateNode target;
	
	public SubstrateLink(SubstrateNode source, SubstrateNode target) {
		super();
		this.source = source;
		this.target = target;
	}

	/**
	 * @return the source
	 */
	public SubstrateNode getSource() {
		return source;
	}

	/**
	 * @return the target
	 */
	public SubstrateNode getTarget() {
		return target;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[SubstrateLink: source=");
		result.append(this.source.toString());
		result.append(",target=");
		result.append(this.target.toString());
		result.append("]");
		return result.toString();
	}
	
	
}
