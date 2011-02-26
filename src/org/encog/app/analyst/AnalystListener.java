package org.encog.app.analyst;

import org.encog.neural.networks.training.Train;

public interface AnalystListener {

	void requestShutdown();
	boolean shouldShutDown();
	void reportCommandBegin(int total, int current, String name);
	void reportCommandEnd();
	void reportTrainingBegin();
	void reportTrainingEnd();
	void reportTraining(Train train);
	void report(int total, int current, String message);

}
