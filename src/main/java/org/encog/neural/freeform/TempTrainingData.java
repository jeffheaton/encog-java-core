package org.encog.neural.freeform;

public interface TempTrainingData {
	void clearTempTraining();
	void allocateTempTraining(int l);
	void setTempTraining(int index, double value);
	double getTempTraining(int index);
	void addTempTraining(int i, double value);
}
