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
package org.encog.persist;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.hmm.HiddenMarkovModel;
import org.encog.ml.hmm.alog.KullbackLeiblerDistanceCalculator;
import org.encog.ml.hmm.distributions.ContinousDistribution;
import org.encog.ml.hmm.distributions.DiscreteDistribution;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

public class TestPersistHMM extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
		
	static HiddenMarkovModel buildContHMM()
	{	
		double [] mean1 = {0.25, -0.25};
		double [][] covariance1 = { {1, 2}, {1, 4} };
		
		double [] mean2 = {0.5, 0.25};
		double [][] covariance2 = { {4, 2}, {3, 4} };
		
		HiddenMarkovModel hmm = new HiddenMarkovModel(2);
		
		hmm.setPi(0, 0.8);
		hmm.setPi(1, 0.2);
		
		hmm.setStateDistribution(0, new ContinousDistribution(mean1,covariance1));
		hmm.setStateDistribution(1, new ContinousDistribution(mean2,covariance2));
		
		hmm.setTransitionProbability(0, 1, 0.05);
		hmm.setTransitionProbability(0, 0, 0.95);
		hmm.setTransitionProbability(1, 0, 0.10);
		hmm.setTransitionProbability(1, 1, 0.90);
		
		return hmm;
	}
	
	static HiddenMarkovModel buildDiscHMM()
	{	
		HiddenMarkovModel hmm = 
			new HiddenMarkovModel(2, 2);
		
		hmm.setPi(0, 0.95);
		hmm.setPi(1, 0.05);
		
		hmm.setStateDistribution(0, new DiscreteDistribution(new double[][] { { 0.95, 0.05 } }));
		hmm.setStateDistribution(1, new DiscreteDistribution(new double[][] { { 0.20, 0.80 } }));
		
		hmm.setTransitionProbability(0, 1, 0.05);
		hmm.setTransitionProbability(0, 0, 0.95);
		hmm.setTransitionProbability(1, 0, 0.10);
		hmm.setTransitionProbability(1, 1, 0.90);
		
		return hmm;
	}
	
	public void validate(HiddenMarkovModel result, HiddenMarkovModel source)
	{
		KullbackLeiblerDistanceCalculator klc = 
				new KullbackLeiblerDistanceCalculator();
					
			double e = klc.distance(result, source);
			Assert.assertTrue(e<0.01);
	}
	
	public void testDiscPersistEG()
	{
		HiddenMarkovModel sourceHMM = buildDiscHMM();

		EncogDirectoryPersistence.saveObject(EG_FILENAME, sourceHMM);
		HiddenMarkovModel resultHMM = (HiddenMarkovModel)EncogDirectoryPersistence.loadObject(EG_FILENAME);

		validate(resultHMM,sourceHMM);
	}
	
	public void testDiscPersistSerial() throws IOException, ClassNotFoundException
	{
		HiddenMarkovModel sourceHMM = buildDiscHMM();
		
		SerializeObject.save(SERIAL_FILENAME, sourceHMM);
		HiddenMarkovModel resultHMM = (HiddenMarkovModel)SerializeObject.load(SERIAL_FILENAME);
				
		validate(resultHMM,sourceHMM);
	}
	
	public void testContPersistEG()
	{
		HiddenMarkovModel sourceHMM = buildContHMM();

		EncogDirectoryPersistence.saveObject(EG_FILENAME, sourceHMM);
		HiddenMarkovModel resultHMM = (HiddenMarkovModel)EncogDirectoryPersistence.loadObject(EG_FILENAME);

		validate(resultHMM,sourceHMM);
	}
	
	public void testContPersistSerial() throws IOException, ClassNotFoundException
	{
		HiddenMarkovModel sourceHMM = buildContHMM();
		
		SerializeObject.save(SERIAL_FILENAME, sourceHMM);
		HiddenMarkovModel resultHMM = (HiddenMarkovModel)SerializeObject.load(SERIAL_FILENAME);
				
		validate(resultHMM,sourceHMM);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}

	
}
