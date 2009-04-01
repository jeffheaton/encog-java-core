package org.encog.util.randomize;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.layers.Layer;

public abstract class BasicRandomizer implements Randomizer {

	public void randomize(Double[] d)
	{
		for(int i=0;i<d.length;i++)
		{
			d[i] = randomize(d[i]);
		}
	}

	public void randomize(double[] d) {
		for(int i=0;i<d.length;i++)
		{
			d[i] = randomize(d[i]);
		}
		
	}

	public void randomize(double[][] d) {
		for (int r = 0; r < d.length; r++) {
			for (int c = 0; c < d[0].length; c++) {
				d[r][c] = randomize(d[r][c]);
			}
		}
		
	}
	
	public void randomize(Double[][] d) {
		for (int r = 0; r < d.length; r++) {
			for (int c = 0; c < d[0].length; c++) {
				d[r][c] = randomize(d[r][c]);
			}
		}
		
	}

	public void randomize(Matrix m) {
		for (int r = 0; r < m.getRows(); r++) {
			for (int c = 0; c < m.getCols(); c++) {
				m.set(r,c,randomize(m.get(r,c)));
			}
		}
	}

	public void randomize(BasicNetwork network) {
		
		// randomize the weight matrix
		for(Synapse synapse: network.getStructure().getSynapses())
		{
			if( synapse.getMatrix()!=null )
				randomize(synapse.getMatrix());
		}
		
		// randomize the thresholds
		for(Layer layer: network.getStructure().getLayers() )
		{
			randomize(layer.getThreshold());
		}
	}
	
}
