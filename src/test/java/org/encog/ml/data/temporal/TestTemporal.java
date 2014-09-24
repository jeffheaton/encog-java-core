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
package org.encog.ml.data.temporal;

import java.util.Iterator;

import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.temporal.TemporalDataDescription.Type;

public class TestTemporal extends TestCase {
	public void testBasicTemporal()
	{
		TemporalMLDataSet temporal = new TemporalMLDataSet(5,1);
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
		
		Iterator<MLDataPair> itr = temporal.iterator();
		
		// set 0
		MLDataPair pair = itr.next();
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
		TemporalMLDataSet temporal = new TemporalMLDataSet(5,1);
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
		
		Iterator<MLDataPair> itr = temporal.iterator();
		
		// set 0
		MLDataPair pair = itr.next();
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
		TemporalMLDataSet temporal = new TemporalMLDataSet(5,1);
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
		
		Iterator<MLDataPair> itr = temporal.iterator();
		
		// set 0
		MLDataPair pair = itr.next();
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
		TemporalMLDataSet temporal = new TemporalMLDataSet(5,1);
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
		
		Iterator<MLDataPair> itr = temporal.iterator();
		
		// set 0
		MLDataPair pair = itr.next();
		TestCase.assertEquals(10, pair.getInput().size());
		TestCase.assertEquals(1, pair.getIdeal().size());
		TestCase.assertEquals(0.75, Math.round(pair.getInput().getData(0)*4.0)/4.0);
		TestCase.assertEquals(1.0, Math.round(pair.getInput().getData(1)*4.0)/4.0);
		TestCase.assertEquals(1.0, Math.round(pair.getInput().getData(2)*4.0)/4.0);
		TestCase.assertEquals(1.0, Math.round(pair.getInput().getData(3)*4.0)/4.0);

	}
	
}
