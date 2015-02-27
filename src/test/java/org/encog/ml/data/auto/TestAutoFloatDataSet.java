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
package org.encog.ml.data.auto;

import junit.framework.Assert;

import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.junit.Test;

public class TestAutoFloatDataSet {

	@Test
	public void testSingle() {
		float[] data = { 1,2,3,4,5 };
		
		AutoFloatDataSet set = new AutoFloatDataSet(1,1,2,1);	
		set.addColumn(data);
		set.addColumn(data);
		MLDataPair pair;
		
		Assert.assertEquals(3, set.size());
		
		pair = set.get(0);
		System.out.println(pair);
		Assert.assertEquals(1, pair.getInputArray()[0],Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(2, pair.getInputArray()[1],Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(3, pair.getIdealArray()[0],Encog.DEFAULT_DOUBLE_EQUAL);
		
		pair = set.get(1);
		System.out.println(pair);
		Assert.assertEquals(2, pair.getInputArray()[0],Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(3, pair.getInputArray()[1],Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(4, pair.getIdealArray()[0],Encog.DEFAULT_DOUBLE_EQUAL);
		
		pair = set.get(2);
		System.out.println(pair);
		Assert.assertEquals(3, pair.getInputArray()[0],Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(4, pair.getInputArray()[1],Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(5, pair.getIdealArray()[0],Encog.DEFAULT_DOUBLE_EQUAL);
		
		System.out.println( set.size());
		
		pair = set.get(3);
		Assert.assertNull(pair);
		
	}
}
