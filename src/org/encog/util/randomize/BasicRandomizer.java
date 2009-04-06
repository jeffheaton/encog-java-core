/*
 * Encog Artificial Intelligence Framework v2.x
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
package org.encog.util.randomize;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.layers.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicRandomizer implements Randomizer {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void randomize(Double[] d)
	{
		for(int i=0;i<d.length;i++)
		{
			d[i] = randomize(d[i]);
		}
	}

	public void randomize(double[] d) {
		for(int i=0;i<d.length;i++)
		{
			d[i] = randomize(d[i]);
		}
		
	}

	public void randomize(double[][] d) {
		for (int r = 0; r < d.length; r++) {
			for (int c = 0; c < d[0].length; c++) {
				d[r][c] = randomize(d[r][c]);
			}
		}
		
	}
	
	public void randomize(Double[][] d) {
		for (int r = 0; r < d.length; r++) {
			for (int c = 0; c < d[0].length; c++) {
				d[r][c] = randomize(d[r][c]);
			}
		}
		
	}

	public void randomize(Matrix m) {
		for (int r = 0; r < m.getRows(); r++) {
			for (int c = 0; c < m.getCols(); c++) {
				m.set(r,c,randomize(m.get(r,c)));
			}
		}
	}

	public void randomize(BasicNetwork network) {
		
		// randomize the weight matrix
		for(Synapse synapse: network.getStructure().getSynapses())
		{
			if( synapse.getMatrix()!=null )
				randomize(synapse.getMatrix());
		}
		
		// randomize the thresholds
		for(Layer layer: network.getStructure().getLayers() )
		{
			if( layer.hasThreshold() )
			{
				randomize(layer.getThreshold());
			}
		}
	}
	
}
