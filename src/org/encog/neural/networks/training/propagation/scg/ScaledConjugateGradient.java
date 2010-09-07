/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training.propagation.scg;

import org.encog.engine.network.train.prop.TrainFlatNetworkResilient;
import org.encog.engine.network.train.prop.TrainFlatNetworkSCG;
import org.encog.engine.util.BoundNumbers;
import org.encog.engine.util.EngineArray;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.propagation.Propagation;

/**
 * This is a training class that makes use of scaled conjugate 
 * gradient methods.  It is a very fast and efficient training
 * algorithm.
 *
 */
public class ScaledConjugateGradient extends Propagation {


	/**
	 * Construct a training class.
	 * @param network The network to train.
	 * @param training The training data.
	 */
	public ScaledConjugateGradient(final BasicNetwork network,
			final NeuralDataSet training) {
		super(network, training);

		TrainFlatNetworkSCG rpropFlat = new TrainFlatNetworkSCG(
				network.getStructure().getFlat(),
				this.getTraining()); 
		this.setFlatTraining( rpropFlat );
	}


}
