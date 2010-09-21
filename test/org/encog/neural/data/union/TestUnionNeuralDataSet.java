/*
 * Encog(tm) Unit Tests v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.data.union;

import java.util.Iterator;

import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestUnionNeuralDataSet extends TestCase {
	
	public static final double[][] INPUT1 = {
		{ 1.0, 2.0 },
		{ 3.0, 4.0 },
		{ 5.0, 6.0 },
	};
	
	public static final double[][] INPUT2 = {
		{ 11.0, 12.0 },
		{ 13.0, 14.0 },
		{ 15.0, 16.0 },
	};
	
	public static final double[][] IDEAL1 = {
		{ 21.0 },
		{ 22.0 },
		{ 23.0 },
	};
	
	public static final double[][] IDEAL2 = {
		{ 31.0 },
		{ 32.0 },
		{ 33.0 },
	};
	
	public void testUnion()
	{
		NeuralDataSet set1 = new BasicNeuralDataSet(INPUT1,IDEAL1);
		NeuralDataSet set2 = new BasicNeuralDataSet(INPUT2,IDEAL2);
		UnionNeuralDataSet union = new UnionNeuralDataSet(2,1);
		union.addSubset(set1);
		union.addSubset(set2);
		Iterator<NeuralDataPair> iterator = union.iterator();
		
		Assert.assertTrue(iterator.hasNext());
		NeuralDataPair pair = iterator.next();
		Assert.assertEquals(1.0, pair.getInput().getData(0),0.5);
		Assert.assertEquals(2.0, pair.getInput().getData(1),0.5);
		Assert.assertEquals(21.0, pair.getIdeal().getData(0),0.5);
		
		Assert.assertTrue(iterator.hasNext());
		pair = iterator.next();
		Assert.assertEquals(3.0, pair.getInput().getData(0),0.5);
		Assert.assertEquals(4.0, pair.getInput().getData(1),0.5);
		Assert.assertEquals(22.0, pair.getIdeal().getData(0),0.5);
		
		Assert.assertTrue(iterator.hasNext());
		pair = iterator.next();
		Assert.assertEquals(5.0, pair.getInput().getData(0),0.5);
		Assert.assertEquals(6.0, pair.getInput().getData(1),0.5);
		Assert.assertEquals(23.0, pair.getIdeal().getData(0),0.5);
		
		//
		Assert.assertTrue(iterator.hasNext());
		pair = iterator.next();		
		Assert.assertEquals(11.0, pair.getInput().getData(0),0.5);
		Assert.assertEquals(12.0, pair.getInput().getData(1),0.5);
		Assert.assertEquals(31.0, pair.getIdeal().getData(0),0.5);
		
		Assert.assertTrue(iterator.hasNext());
		pair = iterator.next();
		Assert.assertEquals(13.0, pair.getInput().getData(0),0.5);
		Assert.assertEquals(14.0, pair.getInput().getData(1),0.5);
		Assert.assertEquals(32.0, pair.getIdeal().getData(0),0.5);
		
		Assert.assertTrue(iterator.hasNext());
		pair = iterator.next();
		Assert.assertEquals(15.0, pair.getInput().getData(0),0.5);
		Assert.assertEquals(16.0, pair.getInput().getData(1),0.5);
		Assert.assertEquals(33.0, pair.getIdeal().getData(0),0.5);
		
		Assert.assertFalse(iterator.hasNext());
	}
}
