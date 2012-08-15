package techniques;

import helpers.DataLoader;
import helpers.DataMapper;
import helpers.PerfResults;

import java.util.ArrayList;

import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public abstract class EvaluationTechnique {

	protected MLDataSet trainingSet;
	private MLDataSet testSet;
	protected EnsembleMLMethodFactory mlMethod;
	protected EnsembleTrainFactory trainFactory;
	protected EnsembleAggregator aggregator;
	protected String label;
	
	public double getMisclassification(BasicNeuralDataSet evalSet, DataMapper dataMapper) {
		int bad = 0;
		int evals = 0;
		for(int i = 0; i < evalSet.getRecordCount(); i++)
		{
			MLDataPair pair = evalSet.get(i);
			MLData output = compute(pair.getInput());
			ArrayList<String> result = dataMapper.unmap(output);
			ArrayList<String> expected = dataMapper.unmap(pair.getIdeal());
			if (!dataMapper.compare(result,expected,false))
				bad++;
			evals++;
		}
		double error = (double) bad / (double) evals;
		return error;
	}
	
	public abstract void train(double trainToError, boolean verbose);
	
	public abstract void trainStep();
	
	public abstract double trainError();
	
	public abstract void init(DataLoader dataLoader);
	
	public String getLabel() {
		return label;
	}
	
	public MLDataSet getTrainingSet() {
		return trainingSet;
	}

	public MLDataSet getTestSet() {
		return testSet;
	}

	public void setTrainingSet(MLDataSet trainingSet) {
		this.trainingSet = trainingSet;
	}

	public void setTestSet(MLDataSet testSet) {
		this.testSet = testSet;
	}
	
	public abstract MLData compute(MLData input);
	
	public PerfResults testPerformance(BasicNeuralDataSet evalSet, DataMapper dataMapper) {
		int outputs = evalSet.getIdealSize();
		long size = evalSet.getRecordCount();
		int tp[] = new int[outputs];
		int tn[] = new int[outputs];
		int fp[] = new int[outputs];
		int fn[] = new int[outputs];
		for(int i = 0; i < size; i++)
		{
			MLDataPair pair = evalSet.get(i);
			MLData output = compute(pair.getInput());
			for(int thisClass = 0; thisClass < outputs; thisClass++) {
				if (output.getData(thisClass) > 0.5) {
					if (pair.getIdeal().getData(thisClass) > 0.5) {
						tp[thisClass]++;
					} else {
						fp[thisClass]++;
					}
				} else {
					if (pair.getIdeal().getData(thisClass) < 0.5) {
						tn[thisClass]++;
					} else {
						fn[thisClass]++;						
					}
				}
			}
		}
		return new PerfResults(tp,fp,tn,fn,outputs);
	}	
}