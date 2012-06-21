package org.encog.ensemble.aggregator;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

public class TestMajorityVoting extends TestCase {

	public void testMajorityVoting() {

		BasicMLData right = new BasicMLData(new double[]{0.0,1.0});
		BasicMLData wrong = new BasicMLData(new double[]{1.0,0.0});
		
		ArrayList<MLData> outs = new ArrayList<MLData>();
		outs.add(right);
		outs.add(wrong);
		outs.add(right);
		outs.add(right);
		
		MajorityVoting majorityVotingUnderTest = new MajorityVoting();
		
		BasicMLData result = (BasicMLData) majorityVotingUnderTest.evaluate(outs);
		TestCase.assertEquals(0.0,result.getData(0));
		TestCase.assertEquals(1.0,result.getData(1));
		
	}
	
}
