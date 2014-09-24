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
package org.encog.ml.data.versatile;

import junit.framework.Assert;

import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.junit.Test;

public class TestMatrixMLDataSet {
	
	public static final double[][] DATA1 = {
		{ 1.0, 10.0 },
		{ 2.0, 20.0 },
		{ 3.0, 30.0 },
		{ 4.0, 40.0 },
		{ 5.0, 50.0 },
		{ 6.0, 60.0 },
		{ 7.0, 70.0 },
		{ 8.0, 80.0 },
		{ 9.0, 90.0 },
		{ 10.0, 100.0 }
};
	
	@Test
	public void testTimeSeriesLead1Lag0() {
		MatrixMLDataSet dset = new MatrixMLDataSet(DATA1,1,1);
		dset.setLeadWindowSize(1);
		
		Assert.assertEquals(9, dset.size());
		
		MLDataPair p1 = dset.get(0);
		Assert.assertEquals(1.0, p1.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(20.0, p1.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		
		MLDataPair p2 = dset.get(1);
		Assert.assertEquals(2.0, p2.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(30.0, p2.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		
		MLDataPair p3 = dset.get(2);
		Assert.assertEquals(3.0, p3.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(40.0, p3.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	@Test
	public void testTimeSeriesLead0Lag1() {
		MatrixMLDataSet dset = new MatrixMLDataSet(DATA1,1,1);
		dset.setLagWindowSize(1);
		
		Assert.assertEquals(9, dset.size());
		
		MLDataPair p1 = dset.get(0);
		Assert.assertEquals(1.0, p1.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(2.0, p1.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(10.0, p1.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		
		MLDataPair p2 = dset.get(1);
		Assert.assertEquals(2.0, p2.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(3.0, p2.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(20.0, p2.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		
		MLDataPair p3 = dset.get(2);
		Assert.assertEquals(3.0, p3.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(4.0, p3.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(30.0, p3.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	@Test
	public void testTimeSeriesLead1Lag1() {
		MatrixMLDataSet dset = new MatrixMLDataSet(DATA1,1,1);
		dset.setLeadWindowSize(1);
		dset.setLagWindowSize(1);
		
		Assert.assertEquals(8, dset.size());
		
		MLDataPair p1 = dset.get(0);
		Assert.assertEquals(1.0, p1.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(2.0, p1.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(20.0, p1.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		
		MLDataPair p2 = dset.get(1);
		Assert.assertEquals(2.0, p2.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(3.0, p2.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(30.0, p2.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		
		MLDataPair p3 = dset.get(2);
		Assert.assertEquals(3.0, p3.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(4.0, p3.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(40.0, p3.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	@Test
	public void testTimeSeriesLead2Lag1() {
		MatrixMLDataSet dset = new MatrixMLDataSet(DATA1,1,1);
		dset.setLeadWindowSize(2);
		dset.setLagWindowSize(1);
		
		Assert.assertEquals(7, dset.size());
		
		MLDataPair p1 = dset.get(0);
		Assert.assertEquals(1.0, p1.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(2.0, p1.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(20.0, p1.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(30.0, p1.getIdeal().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		
		MLDataPair p2 = dset.get(1);
		Assert.assertEquals(2.0, p2.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(3.0, p2.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(30.0, p2.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(40.0, p2.getIdeal().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		
		MLDataPair p3 = dset.get(2);
		Assert.assertEquals(3.0, p3.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(4.0, p3.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(40.0, p3.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(50.0, p3.getIdeal().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	@Test
	public void testTimeSeriesLead1Lag2() {
		MatrixMLDataSet dset = new MatrixMLDataSet(DATA1,1,1);
		dset.setLeadWindowSize(1);
		dset.setLagWindowSize(2);
		
		Assert.assertEquals(7, dset.size());
		
		MLDataPair p1 = dset.get(0);
		Assert.assertEquals(1.0, p1.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(2.0, p1.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(3.0, p1.getInput().getData(2),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(20.0, p1.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		
		MLDataPair p2 = dset.get(1);
		Assert.assertEquals(2.0, p2.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(3.0, p2.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(4.0, p2.getInput().getData(2),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(30.0, p2.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		
		MLDataPair p3 = dset.get(2);
		Assert.assertEquals(3.0, p3.getInput().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(4.0, p3.getInput().getData(1),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(5.0, p3.getInput().getData(2),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals(40.0, p3.getIdeal().getData(0),Encog.DEFAULT_DOUBLE_EQUAL);
	}
}
