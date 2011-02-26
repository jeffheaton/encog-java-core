package org.encog.app.analyst;

import org.encog.neural.networks.training.Train;

public interface AnalystListener {

	void requestShutdown();
	void requestCancelCommand();
	boolean shouldShutDown();
	boolean shouldStopCommand();
	void reportCommandBegin(int total, int current, String name);
	void reportCommandEnd(boolean canceled);
	void reportTrainingBegin();
	void reportTrainingEnd();
	void reportTraining(Train train);
	void report(int total, int current, String message);

}
