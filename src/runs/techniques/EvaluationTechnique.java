package techniques;

import helpers.DataLoader;
import helpers.DataMapper;
import helpers.PerfResults;

import java.util.ArrayList;

import org.encog.ensemble.Ensemble;
import org.encog.ensemble.EnsembleAggregator;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public abstract class EvaluationTechnique {

	protected EnsembleDataSet trainingSet;
	private EnsembleDataSet testSet;
	protected EnsembleMLMethodFactory mlMethod;
	protected EnsembleTrainFactory trainFactory;
	protected EnsembleAggregator aggregator;
	protected Ensemble ensemble;
	protected String label;
	
	public double getMisclassification(BasicNeuralDataSet evalSet, DataMapper dataMapper) {
		int bad = 0;
		for(int i = 0; i < evalSet.getRecordCount(); i++)
		{
			MLDataPair pair = evalSet.get(i);
			MLData output = compute(pair.getInput());
			ArrayList<String> result = dataMapper.unmap(output);
			ArrayList<String> expected = dataMapper.unmap(pair.getIdeal());
			if (!dataMapper.compare(result,expected,false))
				bad++;
		}
		double error = (double) bad / (double) evalSet.getRecordCount();
		return error;
	}
	
	public int getMisclassificationCount(BasicNeuralDataSet evalSet, DataMapper dataMapper) {
		int bad = 0;
		for(int i = 0; i < evalSet.getRecordCount(); i++)
		{
			MLDataPair pair = evalSet.get(i);
			MLData output = compute(pair.getInput());
			ArrayList<String> result = dataMapper.unmap(output);
			ArrayList<String> expected = dataMapper.unmap(pair.getIdeal());
			if (!dataMapper.compare(result,expected,false))
				bad++;
		}
		return bad;
	}
	
	public void train(double trainToError, double selectionError, boolean verbose) {
		ensemble.train(trainToError,selectionError,(EnsembleDataSet) testSet,verbose);
	}

	public void trainStep() {		
	}
	
	public double trainError() {
		return ensemble.getMember(0).getError(trainingSet);
	}
	
	public double testError() {
		return ensemble.getMember(0).getError(testSet);
	}	
	
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
		this.trainingSet = new EnsembleDataSet(trainingSet);
	}

	public void setTestSet(MLDataSet testSet) {
		this.testSet = new EnsembleDataSet(testSet);
	}
	
	public MLData compute(MLData input) {
		return ensemble.compute(input);
	}
	
	public PerfResults testPerformance(BasicNeuralDataSet evalSet, DataMapper dataMapper, boolean debug) {
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
			if (debug) {
				System.out.println("Computed class " + dataMapper.unmap(output).toString() + 
						" for " + dataMapper.unmap(pair.getIdeal()).toString());
			}
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