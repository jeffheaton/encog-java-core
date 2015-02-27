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
package org.encog.app.analyst;

import junit.framework.TestCase;

import org.encog.app.analyst.wizard.WizardMethodType;

public class TestEncogAnalyst extends TestCase {
	

	public void testIrisClassificationFF() {
		AnalystTestingUtility test = new AnalystTestingUtility("org/encog/data/iris.csv");		
		test.wizard(AnalystGoal.Classification, WizardMethodType.FeedForward,true);		
		
		test.validateDataField(0,7.900000,5.843333,4.300000,0.825301,"sepal_l",false,true,false,true);
		test.validateDataField(1,4.400000,3.057333,2.000000,0.434411,"sepal_w",false,true,false,true);
		test.validateDataField(2,6.900000,3.758000,1.000000,1.759404,"petal_l",false,true,false,true);
		test.validateDataField(3,2.500000,1.199333,0.100000,0.759693,"petal_w",false,true,false,true);
		test.validateDataField(4,0,0,0,0,"species",true,true,false,false);
		test.validateDataClass(4, "Iris-setosa", "Iris-versicolor", "Iris-virginica");
		test.validateAnalystField(0, 7.900000,4.300000,1.000000,-1.000000,"sepal_l",0,"Normalize");
		test.validateAnalystField(1, 4.400000,2.000000,1.000000,-1.000000,"sepal_w",0,"Normalize");
		test.validateAnalystField(2, 6.900000,1.000000,1.000000,-1.000000,"petal_l",0,"Normalize");
		test.validateAnalystField(3, 2.500000,0.100000,1.000000,-1.000000,"petal_w",0,"Normalize");
		test.validateAnalystField(4, 0.000000,0.000000,1.000000,-1.000000,"species",0,"Equilateral");
		
		test.process(0.05);
		
		test.validateMethodError(0.05);
	}
	
	public void testEuroIrisClassificationFF() {
		AnalystTestingUtility test = new AnalystTestingUtility("org/encog/data/iris-euro.csv");	
		test.setFormat(AnalystFileFormat.DECCOMMA_SEMI);
		test.wizard(AnalystGoal.Classification, WizardMethodType.FeedForward,true);		
		
		test.validateDataField(0,7.900000,5.843333,4.300000,0.825301,"sepal_l",false,true,false,true);
		test.validateDataField(1,4.400000,3.057333,2.000000,0.434411,"sepal_w",false,true,false,true);
		test.validateDataField(2,6.900000,3.758000,1.000000,1.759404,"petal_l",false,true,false,true);
		test.validateDataField(3,2.500000,1.199333,0.100000,0.759693,"petal_w",false,true,false,true);
		test.validateDataField(4,0,0,0,0,"species",true,true,false,false);
		test.validateDataClass(4, "Iris-setosa", "Iris-versicolor", "Iris-virginica");
		test.validateAnalystField(0, 7.900000,4.300000,1.000000,-1.000000,"sepal_l",0,"Normalize");
		test.validateAnalystField(1, 4.400000,2.000000,1.000000,-1.000000,"sepal_w",0,"Normalize");
		test.validateAnalystField(2, 6.900000,1.000000,1.000000,-1.000000,"petal_l",0,"Normalize");
		test.validateAnalystField(3, 2.500000,0.100000,1.000000,-1.000000,"petal_w",0,"Normalize");
		test.validateAnalystField(4, 0.000000,0.000000,1.000000,-1.000000,"species",0,"Equilateral");
		
		test.process(0.05);
		
		test.validateMethodError(0.05);
	}
	
	public void testIrisRegressionFF() {
		AnalystTestingUtility test = new AnalystTestingUtility("org/encog/data/iris.csv");		
		test.wizard(AnalystGoal.Regression, WizardMethodType.FeedForward,true);		
		
		test.validateDataField(0,7.900000,5.843333,4.300000,0.825301,"sepal_l",false,true,false,true);
		test.validateDataField(1,4.400000,3.057333,2.000000,0.434411,"sepal_w",false,true,false,true);
		test.validateDataField(2,6.900000,3.758000,1.000000,1.759404,"petal_l",false,true,false,true);
		test.validateDataField(3,2.500000,1.199333,0.100000,0.759693,"petal_w",false,true,false,true);
		test.validateDataField(4,0,0,0,0,"species",true,true,false,false);
		test.validateDataClass(4, "Iris-setosa", "Iris-versicolor", "Iris-virginica");
		test.validateAnalystField(0, 7.900000,4.300000,1.000000,-1.000000,"sepal_l",0,"Normalize");
		test.validateAnalystField(1, 4.400000,2.000000,1.000000,-1.000000,"sepal_w",0,"Normalize");
		test.validateAnalystField(2, 6.900000,1.000000,1.000000,-1.000000,"petal_l",0,"Normalize");
		test.validateAnalystField(3, 2.500000,0.100000,1.000000,-1.000000,"petal_w",0,"Normalize");
		test.validateAnalystField(4, 0.000000,0.000000,1.000000,-1.000000,"species",0,"Equilateral");
		
		test.process(0.05);
		
		test.validateMethodError(0.05);
	}
	
	public void testIrisClassificationSVM() {
		AnalystTestingUtility test = new AnalystTestingUtility("org/encog/data/iris.csv");		
		test.wizard(AnalystGoal.Classification, WizardMethodType.SVM, true);
				
		test.validateDataField(0,7.900000,5.843333,4.300000,0.825301,"sepal_l",false,true,false,true);
		test.validateDataField(1,4.400000,3.057333,2.000000,0.434411,"sepal_w",false,true,false,true);
		test.validateDataField(2,6.900000,3.758000,1.000000,1.759404,"petal_l",false,true,false,true);
		test.validateDataField(3,2.500000,1.199333,0.100000,0.759693,"petal_w",false,true,false,true);
		test.validateDataField(4,0,0,0,0,"species",true,true,false,false);
		test.validateDataClass(4, "Iris-setosa", "Iris-versicolor", "Iris-virginica");
		test.validateAnalystField(0, 7.900000,4.300000,1.000000,-1.000000,"sepal_l",0,"Normalize");
		test.validateAnalystField(1, 4.400000,2.000000,1.000000,-1.000000,"sepal_w",0,"Normalize");
		test.validateAnalystField(2, 6.900000,1.000000,1.000000,-1.000000,"petal_l",0,"Normalize");
		test.validateAnalystField(3, 2.500000,0.100000,1.000000,-1.000000,"petal_w",0,"Normalize");
		test.validateAnalystField(4, 0.000000,0.000000,1.000000,-1.000000,"species",0,"SingleField");
		
		test.process(0.05);
		
		test.validateMethodError(0.05);
	}
	
	public void testIrisClassificationPNN() {
		AnalystTestingUtility test = new AnalystTestingUtility("org/encog/data/iris.csv");		
		test.wizard(AnalystGoal.Classification, WizardMethodType.PNN, true);
				
		test.validateDataField(0,7.900000,5.843333,4.300000,0.825301,"sepal_l",false,true,false,true);
		test.validateDataField(1,4.400000,3.057333,2.000000,0.434411,"sepal_w",false,true,false,true);
		test.validateDataField(2,6.900000,3.758000,1.000000,1.759404,"petal_l",false,true,false,true);
		test.validateDataField(3,2.500000,1.199333,0.100000,0.759693,"petal_w",false,true,false,true);
		test.validateDataField(4,0,0,0,0,"species",true,true,false,false);
		test.validateDataClass(4, "Iris-setosa", "Iris-versicolor", "Iris-virginica");
		test.validateAnalystField(0, 7.900000,4.300000,1.000000,-1.000000,"sepal_l",0,"Normalize");
		test.validateAnalystField(1, 4.400000,2.000000,1.000000,-1.000000,"sepal_w",0,"Normalize");
		test.validateAnalystField(2, 6.900000,1.000000,1.000000,-1.000000,"petal_l",0,"Normalize");
		test.validateAnalystField(3, 2.500000,0.100000,1.000000,-1.000000,"petal_w",0,"Normalize");
		test.validateAnalystField(4, 0.000000,0.000000,1.000000,-1.000000,"species",0,"SingleField");
		
		test.process(0.05);
		
		test.validateMethodError(0.05);
	}
	
	public void testIrisClassificationBayes() {
		AnalystTestingUtility test = new AnalystTestingUtility("org/encog/data/iris.csv");		
		test.wizard(AnalystGoal.Classification, WizardMethodType.BayesianNetwork, true);
				
		test.validateDataField(0,7.900000,5.843333,4.300000,0.825301,"sepal_l",false,true,false,true);
		test.validateDataField(1,4.400000,3.057333,2.000000,0.434411,"sepal_w",false,true,false,true);
		test.validateDataField(2,6.900000,3.758000,1.000000,1.759404,"petal_l",false,true,false,true);
		test.validateDataField(3,2.500000,1.199333,0.100000,0.759693,"petal_w",false,true,false,true);
		test.validateDataField(4,0,0,0,0,"species",true,true,false,false);
		test.validateDataClass(4, "Iris-setosa", "Iris-versicolor", "Iris-virginica");
		test.validateAnalystField(0, 0,0,0,0,"sepal_l",0,"PassThrough");
		test.validateAnalystField(1, 0,0,0,0,"sepal_w",0,"PassThrough");
		test.validateAnalystField(2, 0,0,0,0,"petal_l",0,"PassThrough");
		test.validateAnalystField(3, 0,0,0,0,"petal_w",0,"PassThrough");
		test.validateAnalystField(4, 0,0,0,0,"species",0,"SingleField");
		
		test.process(0.05);
		
		test.validateMethodError(0.05);
	}
	
	public void testIrisSOM() {
		AnalystTestingUtility test = new AnalystTestingUtility("org/encog/data/iris.csv");		
		test.wizard(AnalystGoal.Classification, WizardMethodType.SOM,true);		
		
		test.validateDataField(0,7.900000,5.843333,4.300000,0.825301,"sepal_l",false,true,false,true);
		test.validateDataField(1,4.400000,3.057333,2.000000,0.434411,"sepal_w",false,true,false,true);
		test.validateDataField(2,6.900000,3.758000,1.000000,1.759404,"petal_l",false,true,false,true);
		test.validateDataField(3,2.500000,1.199333,0.100000,0.759693,"petal_w",false,true,false,true);
		test.validateDataField(4,0,0,0,0,"species",true,true,false,false);
		test.validateDataClass(4, "Iris-setosa", "Iris-versicolor", "Iris-virginica");
		test.validateAnalystField(0, 7.900000,4.300000,1.000000,-1.000000,"sepal_l",0,"Normalize");
		test.validateAnalystField(1, 4.400000,2.000000,1.000000,-1.000000,"sepal_w",0,"Normalize");
		test.validateAnalystField(2, 6.900000,1.000000,1.000000,-1.000000,"petal_l",0,"Normalize");
		test.validateAnalystField(3, 2.500000,0.100000,1.000000,-1.000000,"petal_w",0,"Normalize");
		test.validateAnalystField(4, 0.000000,0.000000,1.000000,-1.000000,"species",0,"SingleField");
		
		test.process(0.05);
		
		test.validateMethodError(0.05);
	}
}
