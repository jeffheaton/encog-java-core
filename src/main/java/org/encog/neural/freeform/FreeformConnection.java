package org.encog.neural.freeform;

public interface FreeformConnection extends TempTrainingData {
	
	double getWeight();
	
	void setWeight(double weight);

	FreeformNeuron getSource();

	void setSource(FreeformNeuron source);

	FreeformNeuron getTarget();

	void setTarget(FreeformNeuron target);

	boolean isRecurrent();

	void setRecurrent(boolean recurrent);

	void addWeight(double delta);

}
