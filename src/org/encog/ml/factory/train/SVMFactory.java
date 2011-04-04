package org.encog.ml.factory.train;

import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.svm.SVM;
import org.encog.ml.svm.training.SVMTrain;
import org.encog.neural.data.NeuralDataSet;

public class SVMFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String args) {
		return new SVMTrain((SVM) method, training);
	}
}
