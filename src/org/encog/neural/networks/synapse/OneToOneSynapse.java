package org.encog.neural.networks.synapse;

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.layers.Layer;

public class OneToOneSynapse extends BasicSynapse {

	public OneToOneSynapse(Layer fromLayer,Layer toLayer)
	{
		if( fromLayer.getNeuronCount()!=toLayer.getNeuronCount())
		{
			throw new NeuralNetworkError("From and to layers must have the same number of neurons.");
		}
		this.setFromLayer(fromLayer);
		this.setToLayer(toLayer);		
	}
	
	public NeuralData compute(NeuralData input) {
		return input;
	}

	public Matrix getMatrix() {
		return null;
	}

	public int getMatrixSize() {
		return 0;
	}

	public void setMatrix(Matrix matrix) {
		throw new NeuralNetworkError("Can't set the matrix for a OneToOneSynapse");
	}

}
