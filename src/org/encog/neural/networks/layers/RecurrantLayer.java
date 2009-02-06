/*
 * Encog Artificial Intelligence Framework v1.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.networks.layers;

import org.encog.EncogError;
import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.Layer;

public class RecurrantLayer extends FeedforwardLayer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -778649153858552290L;

	public enum RecurrantSource {
		SELF,
		OUTPUT
	} ;
	
	private RecurrantSource recurrantSource;
	private Synapse recurrantSynapse; 
	
	public RecurrantLayer(ActivationFunction thresholdFunction, int neuronCount, RecurrantSource recurrantSource) {
		super(thresholdFunction, neuronCount);

		this.recurrantSource = recurrantSource;
		
		switch( this.recurrantSource )
		{
			case SELF:
				this.recurrantSynapse = new Synapse(neuronCount,neuronCount);
				break;
			case OUTPUT:
				// will need to be created later, the output
				// layer may not even exist yet.
				break;
		}

	}
	
	public RecurrantLayer(int neuronCount, RecurrantSource source) {
		this(new ActivationSigmoid(),neuronCount,source);
	}
	
	private Layer findOutput()
	{
		Layer result = this;
		while( result.getNext()!=null )
		{
			result = result.getNext();
		}
		return result;
	}
	
	/**
	 * Set the next layer.
	 * 
	 * @param next
	 *            the next layer.
	 */
	@Override
	public void setNext(final Layer next) {
		super.setNext(next);
		if( this.recurrantSource == RecurrantSource.OUTPUT )
		{
			Layer output = findOutput();
			if( (this.recurrantSynapse==null) ||
				(output.getNeuronCount() != this.recurrantSynapse.getNeuronCount()) )
			{
				this.recurrantSynapse = new Synapse(output.getNeuronCount(),this.getNeuronCount());
			}
		}
	}
	


	public RecurrantSource getRecurrantSource() {
		return recurrantSource;
	}
	
	/**
	 * Compute the outputs for this layer given the input pattern. The output is
	 * also stored in the fire instance variable.
	 * 
	 * @param pattern
	 *            The input pattern.
	 * @return The output from this layer.
	 */
	public NeuralData compute(final NeuralData pattern) {
		
		Layer recurrantLayer = null;

		switch( this.recurrantSource )
		{
			case SELF:
				recurrantLayer = this;
				break;
			case OUTPUT:
				recurrantLayer = findOutput();
				break;
			default:
				throw new EncogError("Unsupported recurrant sorce");
		}
		
		final Matrix inputRecurrant = createInputMatrix(recurrantLayer.getFire());
		
		int i;
		if (pattern != null) {
			for (i = 0; i < getNeuronCount(); i++) {
				setFire(i, pattern.getData(i));
			}
		}

		final Matrix inputThis = createInputMatrix(this.getFire());
				
		for (i = 0; i < getNext().getNeuronCount(); i++) {
			final Matrix colThis = getMatrix().getCol(i);
			final Matrix colRecurrant = recurrantSynapse.getMatrix().getCol(i);
			
			final double sumThis = MatrixMath.dotProduct(colThis, inputThis);
			final double sumRecurrant = MatrixMath.dotProduct(colRecurrant, inputRecurrant);
			
			getNext().setFire(i,
					this.getActivationFunction().activationFunction(sumThis+sumRecurrant));
		}

		return this.getFire();
	}
	
	public void reset() {
		super.reset();
		this.recurrantSynapse.getMatrix().ramdomize(-1, 1);
	}
	
	/**
	 * @return The string form of the layer.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[RecurrantLayer: Neuron Count=");
		result.append(getNeuronCount());
		result.append(", source=");
		result.append(this.recurrantSource);
		result.append("]");
		return result.toString();
	}

}
