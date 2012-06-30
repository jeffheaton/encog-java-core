package helpers;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public interface EvaluationTechnique {

	public double getError(BasicNeuralDataSet testSet, DataMapper dataMapper);
	public int train(double trainToError);
	public void init(DataLoader dataLoader);
	public String getLabel();
	public MLDataSet getTrainingSet();
	public void setTrainingSet(MLDataSet trainingSet);
	public MLDataSet getTestSet();
	public void setTestSet(MLDataSet testSet);
	
}