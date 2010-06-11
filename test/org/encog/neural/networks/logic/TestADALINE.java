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

package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.simple.TrainAdaline;
import org.encog.neural.pattern.ADALINEPattern;

import junit.framework.TestCase;

public class TestADALINE extends TestCase {

	public void testAdalineNet() throws Throwable
	{
		ADALINEPattern pattern = new ADALINEPattern();
		pattern.setInputNeurons(2);
		pattern.setOutputNeurons(1);
		BasicNetwork network = pattern.generate();
		
		// train it
		NeuralDataSet training = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		Train train = new TrainAdaline(network,training,0.01);
		NetworkUtil.testTraining(train,0.01);
	}
	
}
