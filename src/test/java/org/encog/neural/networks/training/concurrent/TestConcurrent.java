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
package org.encog.neural.networks.training.concurrent;

import junit.framework.TestCase;

import org.encog.ml.train.strategy.end.EndMaxErrorStrategy;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.concurrent.jobs.BPROPJob;
import org.encog.neural.networks.training.concurrent.jobs.RPROPJob;
import org.encog.neural.networks.training.concurrent.jobs.TrainingJob;

public class TestConcurrent extends TestCase {
	private void internalTest(boolean splitCores) {
		ConcurrentTrainingManager ctm = ConcurrentTrainingManager.getInstance();
		ctm.clearPerformers();
		ctm.detectPerformers(splitCores);
		
		// create a RPROP job
		TrainingJob job1 = new RPROPJob(
				XOR.createUnTrainedXOR(),
				XOR.createXORDataSet(),true);
		job1.getStrategies().add(new EndMaxErrorStrategy(0.01));
		ctm.addTrainingJob(job1);
		
		// create a BPROP job
		TrainingJob job2 = new BPROPJob(
				XOR.createUnTrainedXOR(),
				XOR.createXORDataSet(),true, 0.07, 0.07);
		job2.getStrategies().add(new EndMaxErrorStrategy(0.01));
		ctm.addTrainingJob(job2);
		
		ctm.start();
		ctm.join();
		
		XOR.verifyXOR(job1.getNetwork(), 0.1);
		XOR.verifyXOR(job2.getNetwork(), 0.1);	
	}
	
	
	public void testSingleThreaded() {
		internalTest(true);		
	}
	
	public void testMultiThreaded() {
		internalTest(false);		
	}
}
