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

package org.encog.neural.data.temporal;

import java.util.Iterator;

import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.temporal.TemporalDataDescription.Type;

import junit.framework.TestCase;

public class TestTemporal extends TestCase {
	public void testBasicTemporal()
	{
		TemporalNeuralDataSet temporal = new TemporalNeuralDataSet(5,1);
		temporal.addDescription(new TemporalDataDescription(Type.RAW, true, false));
		temporal.addDescription(new TemporalDataDescription(Type.RAW, true, false));
		temporal.addDescription(new TemporalDataDescription(Type.RAW, false, true));
		for(int i=0;i<10;i++)
		{
			TemporalPoint tp = temporal.createPoint(i);
			tp.setData(0, 1.0+(i*3));
			tp.setData(1, 2.0+(i*3));
			tp.setData(2, 3.0+(i*3));
		}
		
		temporal.generate();
		
		TestCase.assertEquals(10, temporal.getInputNeuronCount());
		TestCase.assertEquals(1, temporal.getOutputNeuronCount());
		TestCase.assertEquals(10, temporal.calculateActualSetSize());
		
		Iterator<NeuralDataPair> itr = temporal.iterator();
		
		// set 0
		NeuralDataPair pair = itr.next();
		TestCase.assertEquals(10, pair.getInput().size());
		TestCase.assertEquals(1, pair.getIdeal().size());
		TestCase.assertEquals(1.0, pair.getInput().getData(0));
		TestCase.assertEquals(2.0, pair.getInput().getData(1));
		TestCase.assertEquals(4.0, pair.getInput().getData(2));
		TestCase.assertEquals(5.0, pair.getInput().getData(3));
		TestCase.assertEquals(7.0, pair.getInput().getData(4));
		TestCase.assertEquals(8.0, pair.getInput().getData(5));
		TestCase.assertEquals(10.0, pair.getInput().getData(6));
		TestCase.assertEquals(11.0, pair.getInput().getData(7));
		TestCase.assertEquals(13.0, pair.getInput().getData(8));
		TestCase.assertEquals(14.0, pair.getInput().getData(9));
		TestCase.assertEquals(18.0, pair.getIdeal().getData(0));
		
		// set 1
		pair = itr.next();
		TestCase.assertEquals(10, pair.getInput().size());
		TestCase.assertEquals(1, pair.getIdeal().size());
		TestCase.assertEquals(4.0, pair.getInput().getData(0));
		TestCase.assertEquals(5.0, pair.getInput().getData(1));
		TestCase.assertEquals(7.0, pair.getInput().getData(2));
		TestCase.assertEquals(8.0, pair.getInput().getData(3));
		TestCase.assertEquals(10.0, pair.getInput().getData(4));
		TestCase.assertEquals(11.0, pair.getInput().getData(5));
		TestCase.assertEquals(13.0, pair.getInput().getData(6));
		TestCase.assertEquals(14.0, pair.getInput().getData(7));
		TestCase.assertEquals(16.0, pair.getInput().getData(8));
		TestCase.assertEquals(17.0, pair.getInput().getData(9));
		TestCase.assertEquals(21.0, pair.getIdeal().getData(0));
		
		// set 2
		pair = itr.next();
		TestCase.assertEquals(10, pair.getInput().size());
		TestCase.assertEquals(1, pair.getIdeal().size());
		TestCase.assertEquals(7.0, pair.getInput().getData(0));
		TestCase.assertEquals(8.0, pair.getInput().getData(1));
		TestCase.assertEquals(10.0, pair.getInput().getData(2));
		TestCase.assertEquals(11.0, pair.getInput().getData(3));
		TestCase.assertEquals(13.0, pair.getInput().getData(4));
		TestCase.assertEquals(14.0, pair.getInput().getData(5));
		TestCase.assertEquals(16.0, pair.getInput().getData(6));
		TestCase.assertEquals(17.0, pair.getInput().getData(7));
		TestCase.assertEquals(19.0, pair.getInput().getData(8));
		TestCase.assertEquals(20.0, pair.getInput().getData(9));
		TestCase.assertEquals(24.0, pair.getIdeal().getData(0));
		
		// set 3
		pair = itr.next();
		TestCase.assertEquals(10, pair.getInput().size());
		TestCase.assertEquals(1, pair.getIdeal().size());
		TestCase.assertEquals(10.0, pair.getInput().getData(0));
		TestCase.assertEquals(11.0, pair.getInput().getData(1));
		TestCase.assertEquals(13.0, pair.getInput().getData(2));
		TestCase.assertEquals(14.0, pair.getInput().getData(3));
		TestCase.assertEquals(16.0, pair.getInput().getData(4));
		TestCase.assertEquals(17.0, pair.getInput().getData(5));
		TestCase.assertEquals(19.0, pair.getInput().getData(6));
		TestCase.assertEquals(20.0, pair.getInput().getData(7));
		TestCase.assertEquals(22.0, pair.getInput().getData(8));
		TestCase.assertEquals(23.0, pair.getInput().getData(9));
		TestCase.assertEquals(27.0, pair.getIdeal().getData(0));
		
		TestCase.assertNull(itr.next());
	}
	
	public void testHiLowTemporal()
	{
		TemporalNeuralDataSet temporal = new TemporalNeuralDataSet(5,1);
		temporal.addDescription(new TemporalDataDescription(Type.RAW, true, false));
		temporal.addDescription(new TemporalDataDescription(Type.RAW, true, false));
		temporal.addDescription(new TemporalDataDescription(Type.RAW, false, true));
		for(int i=0;i<10;i++)
		{
			TemporalPoint tp = temporal.createPoint(i);
			tp.setData(0, 1.0+(i*3));
			tp.setData(1, 2.0+(i*3));
			tp.setData(2, 3.0+(i*3));
		}
		
		temporal.setHighSequence(8);
		temporal.setLowSequence(2);
		temporal.generate();
		
		TestCase.assertEquals(10, temporal.getInputNeuronCount());
		TestCase.assertEquals(1, temporal.getOutputNeuronCount());
		TestCase.assertEquals(7, temporal.calculateActualSetSize());
		
		Iterator<NeuralDataPair> itr = temporal.iterator();
		
		// set 0
		NeuralDataPair pair = itr.next();
		TestCase.assertEquals(10, pair.getInput().size());
		TestCase.assertEquals(1, pair.getIdeal().size());
		TestCase.assertEquals(7.0, pair.getInput().getData(0));
		TestCase.assertEquals(8.0, pair.getInput().getData(1));
		TestCase.assertEquals(10.0, pair.getInput().getData(2));
		TestCase.assertEquals(11.0, pair.getInput().getData(3));
		TestCase.assertEquals(13.0, pair.getInput().getData(4));
		TestCase.assertEquals(14.0, pair.getInput().getData(5));
		TestCase.assertEquals(16.0, pair.getInput().getData(6));
		TestCase.assertEquals(17.0, pair.getInput().getData(7));
		TestCase.assertEquals(19.0, pair.getInput().getData(8));
		TestCase.assertEquals(20.0, pair.getInput().getData(9));
		TestCase.assertEquals(24.0, pair.getIdeal().getData(0));
		
		// only one set
		TestCase.assertNull(itr.next());
	}
	
	public void testFormatTemporal()
	{
		TemporalNeuralDataSet temporal = new TemporalNeuralDataSet(5,1);
		temporal.addDescription(new TemporalDataDescription(Type.DELTA_CHANGE, true, false));
		temporal.addDescription(new TemporalDataDescription(Type.PERCENT_CHANGE, true, false));
		temporal.addDescription(new TemporalDataDescription(Type.RAW, false, true));
		for(int i=0;i<10;i++)
		{
			TemporalPoint tp = temporal.createPoint(i);
			tp.setData(0, 1.0+(i*3));
			tp.setData(1, 2.0+(i*3));
			tp.setData(2, 3.0+(i*3));
		}
		
		temporal.generate();
		
		Iterator<NeuralDataPair> itr = temporal.iterator();
		
		// set 0
		NeuralDataPair pair = itr.next();
		TestCase.assertEquals(10, pair.getInput().size());
		TestCase.assertEquals(1, pair.getIdeal().size());
		TestCase.assertEquals(3.0, pair.getInput().getData(0));
		TestCase.assertEquals(1.5, pair.getInput().getData(1));
		TestCase.assertEquals(3.0, pair.getInput().getData(2));
		TestCase.assertEquals(0.6, pair.getInput().getData(3));
		TestCase.assertEquals(3.0, pair.getInput().getData(4));
		TestCase.assertEquals(0.375, pair.getInput().getData(5));
		TestCase.assertEquals(3.0, pair.getInput().getData(6));
		TestCase.assertEquals(0.25, Math.round(pair.getInput().getData(7)*4.0)/4.0);
		TestCase.assertEquals(3.0, pair.getInput().getData(8));
		TestCase.assertEquals(0.25, Math.round(pair.getInput().getData(9)*4.0)/4.0);
		TestCase.assertEquals(18.0, pair.getIdeal().getData(0));
	}
	
	public void testActivationTemporal()
	{
		TemporalNeuralDataSet temporal = new TemporalNeuralDataSet(5,1);
		temporal.addDescription(new TemporalDataDescription(new ActivationTANH(),Type.RAW, true, false));
		temporal.addDescription(new TemporalDataDescription(new ActivationTANH(),Type.RAW, true, false));
		temporal.addDescription(new TemporalDataDescription(new ActivationTANH(),Type.RAW, false, true));
		for(int i=0;i<10;i++)
		{
			TemporalPoint tp = temporal.createPoint(i);
			tp.setData(0, 1.0+(i*3));
			tp.setData(1, 2.0+(i*3));
			tp.setData(2, 3.0+(i*3));
		}
		
		temporal.generate();
		
		Iterator<NeuralDataPair> itr = temporal.iterator();
		
		// set 0
		NeuralDataPair pair = itr.next();
		TestCase.assertEquals(10, pair.getInput().size());
		TestCase.assertEquals(1, pair.getIdeal().size());
		TestCase.assertEquals(0.5, Math.round(pair.getInput().getData(0)*4.0)/4.0);
		TestCase.assertEquals(0.75, Math.round(pair.getInput().getData(1)*4.0)/4.0);
		TestCase.assertEquals(1.0, Math.round(pair.getInput().getData(2)*4.0)/4.0);
		TestCase.assertEquals(1.0, Math.round(pair.getInput().getData(3)*4.0)/4.0);

	}
	
}
