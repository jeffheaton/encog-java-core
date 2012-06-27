package helpers;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public interface EvaluationTechnique {

	public double getCVError(BasicNeuralDataSet testSet);
	public int train(double trainToError);
	public void init();
	public String getLabel();
	public MLDataSet getTrainingSet();
	public MLDataSet getTestSet();
	
}