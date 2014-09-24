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
package org.encog.neural.freeform;

import org.encog.neural.freeform.basic.BasicFreeformNeuron;

/**
 * Defines a freeform context neuron.
 *
 */
public class FreeformContextNeuron extends BasicFreeformNeuron {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The context source.
	 */
	private FreeformNeuron contextSource;

	/**
	 * Construct the context neuron.
	 * @param theContextSource The context source.
	 */
	public FreeformContextNeuron(final FreeformNeuron theContextSource) {
		super(null);
		this.contextSource = theContextSource;
	}

	/**
	 * @return the contextSource
	 */
	public FreeformNeuron getContextSource() {
		return this.contextSource;
	}

	/**
	 * @param contextSource
	 *            the contextSource to set
	 */
	public void setContextSource(final FreeformNeuron contextSource) {
		this.contextSource = contextSource;
	}

	@Override
	public void updateContext() {
		setActivation(this.contextSource.getActivation());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[FreeformContextNeuron: ");
		result.append("outputCount=");
		result.append(this.getOutputs().size());
		result.append("]");
		return result.toString();
	}

}
