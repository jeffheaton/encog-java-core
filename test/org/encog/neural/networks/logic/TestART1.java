package org.encog.neural.networks.logic;

import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ART1Pattern;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestART1 extends TestCase {

	public static final int INPUT_NEURONS = 5;
	public static final int OUTPUT_NEURONS = 2;


	public BiPolarNeuralData setupInput(String pattern) {
		BiPolarNeuralData result = new BiPolarNeuralData(INPUT_NEURONS);
		for (int i = 0; i < INPUT_NEURONS; i++) {
			result.setData(i, pattern.charAt(i) == 'O');
		}

		return result;
	}
	
	public int present(ART1Logic logic, String str)
	{
		BiPolarNeuralData in = setupInput(str);
		BiPolarNeuralData out = new BiPolarNeuralData(OUTPUT_NEURONS);
		logic.compute(in, out);
		if (logic.hasWinner()) {
			return logic.getWinner();
		} else {
			return -1;
		}
	}

	public void testART1Patterns() throws Throwable {

		ART1Pattern pattern = new ART1Pattern();
		pattern.setInputNeurons(INPUT_NEURONS);
		pattern.setOutputNeurons(OUTPUT_NEURONS);
		BasicNetwork network = pattern.generate();
		ART1Logic logic = (ART1Logic) network.getLogic();
		
		Assert.assertEquals(present(logic,"   O "),0);
		Assert.assertEquals(present(logic,"  O O"),1);
		Assert.assertEquals(present(logic,"    O"),1);
		Assert.assertEquals(present(logic,"  O O"),-1);
		Assert.assertEquals(present(logic,"    O"),1);
		Assert.assertEquals(present(logic,"  O O"),-1);
		Assert.assertEquals(present(logic,"    O"),1);

	}

}
