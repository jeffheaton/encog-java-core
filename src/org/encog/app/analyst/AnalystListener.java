package org.encog.app.analyst;

import org.encog.ml.MLTrain;

/**
 * Reports the progress of the Encog Analyst. If you would like to use this with
 * an Encog StatusReportable object, use the bridge utilituy object:
 * 
 * org.encog.app.analyst.util.AnalystReportBridge
 * 
 */
public interface AnalystListener {

	void requestShutdown();

	void requestCancelCommand();

	boolean shouldShutDown();

	boolean shouldStopCommand();

	void reportCommandBegin(int total, int current, String name);

	void reportCommandEnd(boolean canceled);

	void reportTrainingBegin();

	void reportTrainingEnd();

	void reportTraining(MLTrain train);

	void report(int total, int current, String message);

}
