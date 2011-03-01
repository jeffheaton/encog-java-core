package org.encog.app.analyst;

import org.encog.engine.util.Format;
import org.encog.engine.util.Stopwatch;
import org.encog.neural.networks.training.Train;

public class ConsoleAnalystListener implements AnalystListener {

	private String currentTask = "";
	private Stopwatch stopwatch = new Stopwatch();
	private boolean shutdownRequested;
	private boolean cancelCommand;

	@Override
	public void report(int total, int current, String message) {
		if (total == 0) {
			System.out.println(current + " : " + message);
		} else {
			System.out.println(current + "/" + total + " : " + message);
		}

	}

	@Override
	public void reportCommandBegin(int total, int current, String name) {
		System.out.println();
		if (total == 0) {
			System.out.println("Beginning Task#" + current + " : " + name);
		} else {
			System.out.println("Beginning Task#" + current + "/" + total
					+ " : " + name);
		}
		currentTask = name;
		this.stopwatch.start();
	}

	@Override
	public void reportCommandEnd(boolean cancel) {
		this.cancelCommand = false;
		this.stopwatch.stop();
		System.out.println("Task "
				+ this.currentTask
				+ " " + (cancel?"canceled":"completed") + ", task elapsed time "
				+ Format.formatTimeSpan((int) (this.stopwatch
						.getElapsedMilliseconds() / 1000)));

	}

	@Override
	public void reportTrainingBegin() {

	}

	@Override
	public void reportTrainingEnd() {

	}

	@Override
	public void reportTraining(Train train) {
		
		System.out.println("Iteration #" + Format.formatInteger(train.getIteration())
				+ " Error:" + Format.formatPercent(train.getError())
				+ " elapsed time = " + Format.formatTimeSpan((int) (this.stopwatch.getElapsedMilliseconds()/1000)));
	}

	@Override
	public synchronized void requestShutdown() {
		this.shutdownRequested = true;
		
	}

	@Override
	public synchronized boolean shouldShutDown() {
		return this.shutdownRequested;
	}

	@Override
	public synchronized void requestCancelCommand() {
		this.cancelCommand = true;		
	}

	@Override
	public synchronized boolean shouldStopCommand() {
		return this.cancelCommand;
	}

}
