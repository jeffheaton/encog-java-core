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
