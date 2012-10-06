package org.encog.neural.freeform;

import java.util.List;

public interface FreeformNeuron extends TempTrainingData {
	void setActivation(double activation);
	double getActivation();
	InputSummation getInputSummation();
	List<FreeformConnection> getOutputs();
	void addInput(FreeformConnection targetNeuron);
	void addOutput(FreeformConnection sourceNeuron);
	void performCalculation();
	void setInputSummation(InputSummation theInputSummation);
	boolean isBias();
	double getSum();
	void setBias(boolean b);
	void updateContext();	
}
