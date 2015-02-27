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
package org.encog.ml.data.basic;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.data.MLDataPair;

public class TestBasicMLSequenceSet extends TestCase {
	
	public static MLDataPair TEST1 = new BasicMLDataPair(new BasicMLData(new double[] { 1.0 } ),null);
	public static MLDataPair TEST2 = new BasicMLDataPair(new BasicMLData(new double[] { 2.0 } ),null);
	public static MLDataPair TEST3 = new BasicMLDataPair(new BasicMLData(new double[] { 3.0 } ),null);
	public static MLDataPair TEST4 = new BasicMLDataPair(new BasicMLData(new double[] { 4.0 } ),null);
	public static MLDataPair TEST5 = new BasicMLDataPair(new BasicMLData(new double[] { 5.0 } ),null);
	public static int[] CHECK = { 1,2,3,4,1,2,3,2,1 };
	
	public void testSimple() {
		BasicMLSequenceSet seq = new BasicMLSequenceSet();
		seq.startNewSequence();
		seq.add(TEST1);
		seq.add(TEST2);
		seq.add(TEST3);
		seq.add(TEST4);
		seq.startNewSequence();
		seq.add(TEST1);
		seq.add(TEST2);
		seq.startNewSequence();
		seq.add(TEST3);
		seq.add(TEST2);
		seq.add(TEST1);
		
		Assert.assertEquals(9, seq.getRecordCount());
		Assert.assertEquals(3, seq.getSequenceCount());
		
		int i = 0;
		for(MLDataPair pair: seq) {
			Assert.assertEquals(CHECK[i++], (int)pair.getInputArray()[0]);
		}
		
	}
}
