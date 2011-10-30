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
