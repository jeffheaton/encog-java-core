package org.encog.neural.networks.layers;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.location.PersistenceLocation;
import org.encog.persist.location.ResourcePersistence;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestContext  extends TestCase {
	
	
	/**
	 * 1 xor 0 = 1, 0 xor 0 = 0, 0 xor 1 = 1, 1 xor 1 = 0
	 */
	public static final double[] SEQUENCE = { 1.0, 0.0, 1.0, 0.0, 0.0, 0.0,
			0.0, 1.0, 1.0, 1.0, 1.0, 0.0 };

	private double[][] input;
	private double[][] ideal;

	public NeuralDataSet generate(final int count) {
		this.input = new double[count][1];
		this.ideal = new double[count][1];

		for (int i = 0; i < this.input.length; i++) {
			this.input[i][0] = TestContext.SEQUENCE[i
					% TestContext.SEQUENCE.length];
			this.ideal[i][0] = TestContext.SEQUENCE[(i + 1)
					% TestContext.SEQUENCE.length];
		}

		return new BasicNeuralDataSet(this.input, this.ideal);
	}
	
	public void testContextLayer()
	{
		PersistenceLocation location = new ResourcePersistence("org/encog/data/networks.eg");
		EncogPersistedCollection encog = new EncogPersistedCollection(location);
		BasicNetwork network = (BasicNetwork)encog.find("elman");
		NeuralDataSet data = generate(100);
		int rate = (int)(network.calculateError(data)*100);
		Assert.assertTrue(rate<35);
	}
}
