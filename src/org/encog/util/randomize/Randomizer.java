package org.encog.util.randomize;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.BasicNetwork;

public interface Randomizer {
	
	public double randomize(double d);
	public void randomize(double[] d);
	public void randomize(double[][] d);
	public void randomize(Matrix m);
	public void randomize(BasicNetwork network);
	
}
