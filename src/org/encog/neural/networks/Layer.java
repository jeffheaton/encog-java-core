package org.encog.neural.networks;

import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;

public interface Layer {
	public NeuralData compute(final NeuralData pattern);

	public void setPrevious(Layer layer);

	public void setNext(Layer layer);

	public NeuralData getFire();

	public int getNeuronCount();

	public boolean isInput();

	public boolean isHidden();

	public Matrix getMatrix();

	public void reset();

	public int getMatrixSize();

	public void setFire(int i, double activationFunction);

	public void setMatrix(Matrix deleteCol);

	public boolean isOutput();

	public Layer getNext();

	public boolean hasMatrix();
	
	public void setFire(NeuralData fire);
}
