/*
 * Encog(tm) Core v2.4
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

package org.encog.persist;

import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.RadialBasisPattern;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestPersist extends TestCase {
	
	private BasicNetwork getRBF()
	{
		RadialBasisPattern pattern = new RadialBasisPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		BasicNetwork net = pattern.generate();
		return net;
	}
	
	private BasicNetwork getElman()
	{
		ElmanPattern pattern = new ElmanPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		pattern.setActivationFunction(new ActivationSigmoid());
		BasicNetwork net = pattern.generate();
		return net;
	}
	
	public void testPersist()
	{
		EncogPersistedCollection encog = 
			new EncogPersistedCollection("encogtest.eg");
		encog.create();
		BasicNetwork net1 = getRBF();
		BasicNetwork net2 = getElman();
		encog.add("rbf", net1);
		encog.add("elman", net2);
		
		net1 = (BasicNetwork)encog.find("rbf");
		net2 = (BasicNetwork)encog.find("elman");
		
		Assert.assertNotNull(net1);
		Assert.assertNotNull(net2);
		
	}
}
