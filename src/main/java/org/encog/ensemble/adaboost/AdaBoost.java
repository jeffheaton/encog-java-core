package org.encog.ensemble.adaboost;

import org.encog.ensemble.Ensemble;
import org.encog.ensemble.EnsembleDataSetFactory;
import org.encog.ensemble.EnsembleML;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.EnsembleTypes.ProblemType;
import org.encog.mathutil.VectorAlgebra;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;

import java.util.ArrayList;
import java.util.TreeMap;

public class AdaBoost implements Ensemble {

	private int T;
	private MLDataSet trainingData;
	private EnsembleTrainFactory tf;
	private AdaBoostDataSetFactory dataSetFactory;
	private EnsembleMLMethodFactory mlFactory;
	private TreeMap<AdaBoostML,Double> members;
	private VectorAlgebra va;
	
	public AdaBoost(int iterationsT, EnsembleMLMethodFactory mlFactory) {
		this.T = iterationsT;
		this.mlFactory = mlFactory;
		this.va = new VectorAlgebra();
	}
	
	@Override
	public void setTrainingMethod(EnsembleTrainFactory newTrainFactory) {
		this.tf = newTrainFactory;
	}

	@Override
	public void setTrainingData(MLDataSet trainingData) {
		this.trainingData = trainingData;
		if(dataSetFactory != null) dataSetFactory.setInputData(trainingData); 
	}

	@Override
	public void setTrainingDataFactory(EnsembleDataSetFactory dataSetFactory) {
		this.dataSetFactory = (AdaBoostDataSetFactory) dataSetFactory;
	}

	public int train(double targetAccuracy, boolean verbose) {
		ArrayList<Double> D = new ArrayList<Double>();
		for (int k = 0; k < trainingData.size(); k++)
			D.add(1.0 / (float) trainingData.size());
		for (int i = 0; i < T; i++) {
			//TODO: use internal dataSet significance
			dataSetFactory.setWeights(D);
			MLDataSet thisSet = dataSetFactory.getNewDataSet();
			AdaBoostML newML = new AdaBoostML(mlFactory.createML(this.trainingData.getInputSize(), this.trainingData.getIdealSize()));
			MLTrain train = tf.getTraining(newML.getMl(), thisSet);
			newML.train(train,targetAccuracy,verbose);
			double newWeight = getWeightedError(newML,D,thisSet);
			members.put(newML,newWeight);
			D = updateD(newML,thisSet,D);
		}
		return T;
	}

	@Override
	public int train(double targetError) {
		return train(targetError, false);
	}

	private double epsilon(AdaBoostML ml, MLDataSet dataSet) {
		int bad = 0;
		for (MLDataPair data: dataSet) {
			if (ml.classify(data.getInput()) != ml.winner(data.getIdeal()))
				bad++;
		}
		return (float) bad / (float) dataSet.size();
	}
	
	private ArrayList<Double> updateD(AdaBoostML ml, MLDataSet dataSet, ArrayList<Double> D_t) {
		ArrayList<Double> D_tplus1 = new ArrayList<Double>();
		double epsilon = epsilon(ml, dataSet);
		double alpha_t = Math.log(1 - epsilon / epsilon);
		for (int i = 0; i < dataSet.size(); i++) {
			double D_tplus1_i = D_t.get(i) * Math.exp(-alpha_t * va.dotProduct(dataSet.get(i).getIdeal().getData(), ml.compute(dataSet.get(i).getInput()).getData()));
			D_tplus1.add(D_tplus1_i);
		}
		return D_tplus1;
	}

	private double getWeightedError(AdaBoostML newML, ArrayList<Double> D, MLDataSet dataSet) {
		double sum = 0;
		for (int i = 0; i < dataSet.size(); i++)
			if (newML.classify(dataSet.get(i).getInput()) != newML.winner(dataSet.get(i).getIdeal()))
				sum += D.get(i);
		return sum;
	}

	@Override
	public MLDataSet getTrainingSet(int setNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnsembleML getMember(int memberNumber) {
		return (EnsembleML) members.keySet().toArray()[memberNumber];
	}

	@Override
	public void addMember(EnsembleML newMember) throws NotPossibleInThisMethod {
		throw new NotPossibleInThisMethod();
	}

	@Override
	public MLData compute(MLData input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getCrossValidationError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ProblemType getProblemType() {
		// TODO Auto-generated method stub
		return null;
	}

}
