/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version, Unit Tests
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

package org.encog.util;

import java.io.ByteArrayOutputStream;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.RadialBasisPattern;

import junit.framework.Assert;
import junit.framework.TestCase;


public class TestSerializeObject extends TestCase {

	public void testSerializeXOR() throws Throwable
	{
		BasicNeuralDataSet set = new BasicNeuralDataSet(XOR.XOR_INPUT,
				XOR.XOR_IDEAL);
		SerializeObject.save("encog.ser", set);
		set = (BasicNeuralDataSet) SerializeObject.load("encog.ser");
		XOR.testXORDataSet(set);
	}
	
	public void testSerializeNetwork() throws Throwable
	{
		RadialBasisPattern pattern = new RadialBasisPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		BasicNetwork net = pattern.generate();

		SerializeObject.save("encog.ser", net);
		net = (BasicNetwork) SerializeObject.load("encog.ser");
		Assert.assertEquals(3, net.getStructure().getLayers().size());
		
	}
	

	public void testSerializeNetwork2() throws Throwable
	{
		ElmanPattern pattern = new ElmanPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		BasicNetwork net = pattern.generate();

		SerializeObject.save("encog.ser", net);
		net = (BasicNetwork) SerializeObject.load("encog.ser");
		Assert.assertEquals(4, net.getStructure().getLayers().size());
		
	}
	
}
