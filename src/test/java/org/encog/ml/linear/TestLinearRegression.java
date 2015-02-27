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
package org.encog.ml.linear;

import junit.framework.TestCase;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.fitting.linear.LinearRegression;
import org.encog.ml.fitting.linear.TrainLinearRegression;
import org.encog.util.Format;

public class TestLinearRegression extends TestCase {
	public void testLinear1() {
		double[][] INPUT = { {3}, {6}, {4}, {5} };
		double[][] IDEAL = { {0}, {-3}, {-1}, {-2} };
		
		MLDataSet data = new BasicMLDataSet(INPUT,IDEAL);
		LinearRegression lin = new LinearRegression(1);
		TrainLinearRegression train = new TrainLinearRegression(lin,data);
		train.iteration();
		
		System.out.println("w0 = " + Format.formatDouble(lin.getWeights()[0],2));
		System.out.println("w1 = " + Format.formatDouble(lin.getWeights()[1],2));
		System.out.println("Error: " + lin.calculateError(data));
	}
	
	public void testLinear2() {
		double[][] INPUT = { {2}, {4}, {6}, {8} };
		double[][] IDEAL = { {2}, {5}, {5}, {8} };
		
		MLDataSet data = new BasicMLDataSet(INPUT,IDEAL);
		LinearRegression lin = new LinearRegression(1);
		TrainLinearRegression train = new TrainLinearRegression(lin,data);
		train.iteration();
		System.out.println("w0 = " + Format.formatDouble(lin.getWeights()[0],2));
		System.out.println("w1 = " + Format.formatDouble(lin.getWeights()[1],2));
		System.out.println("Error: " + lin.calculateError(data));
	}
}
