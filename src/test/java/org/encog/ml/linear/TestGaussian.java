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

import java.util.Arrays;

import junit.framework.TestCase;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.fitting.gaussian.GaussianFitting;
import org.encog.ml.fitting.gaussian.TrainGaussian;

public class TestGaussian extends TestCase {
	public void testGauss() {
		double[][] INPUT = { {3,8}, {4,7}, {5,5}, {6,3}, {7,2} };
		
		MLDataSet data = new BasicMLDataSet(INPUT, null);
		GaussianFitting fit = new GaussianFitting(INPUT[0].length);
		TrainGaussian train = new TrainGaussian(fit,data);
		train.iteration();
		
		for(MLDataPair pair: data){
			MLData output = fit.compute(pair.getInput());
			System.out.println( output.getData(0));
		}
		
		for( double[] d : fit.getSigma().getData()) {
			System.out.println( Arrays.toString(d));
		}
	}
	

}
