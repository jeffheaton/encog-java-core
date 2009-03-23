package org.encog.neural.networks.synapse;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.layers.Layer;

public class WeightlessSynapse extends BasicSynapse {

	public WeightlessSynapse(Layer fromLayer,Layer toLayer)
	{
		this.setFromLayer(fromLayer);
		this.setToLayer(toLayer);		
	}
	
	public WeightlessSynapse() {
		// TODO Auto-generated constructor stub
	}

	public NeuralData compute(NeuralData input) {
		NeuralData result = new BasicNeuralData(getToNeuronCount());
		// just sum the input
		double sum = 0;	
		for(int i=0;i<input.size();i++)
		{
			sum+=input.getData(i);
		}
		
		for (int i = 0; i < getToNeuronCount(); i++) {
			result.setData(i,sum);
		}
		return result;
	}

	public Matrix getMatrix() {
		return null;
	}

	public int getMatrixSize() {
		return 0;
	}

	public void setMatrix(Matrix matrix) {
		throw new NeuralNetworkError("Can't set the matrix for a WeightlessSynapse");
	}

	public SynapseType getType() {
		return SynapseType.Weighted;
	}
	
	public boolean isTeachable()
	{
		return false;
	}
	
	public Object clone() {
		WeightlessSynapse result = new WeightlessSynapse();
		result.setMatrix(this.getMatrix().clone());
		return result;
	}

}
