package org.encog.neural.networks.training.concurrent;

import java.util.ArrayList;
import java.util.List;

import org.encog.Encog;
import org.encog.NullStatusReportable;
import org.encog.engine.StatusReportable;
import org.encog.engine.network.train.prop.RPROPConst;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.concurrent.jobs.BPROPJob;
import org.encog.neural.networks.training.concurrent.jobs.RPROPJob;
import org.encog.neural.networks.training.concurrent.jobs.TrainingJob;
import org.encog.neural.networks.training.concurrent.performers.ConcurrentTrainingPerformer;
import org.encog.neural.networks.training.concurrent.performers.ConcurrentTrainingPerformerCPU;
import org.encog.neural.networks.training.concurrent.performers.ConcurrentTrainingPerformerOpenCL;
import org.encog.neural.networks.training.strategy.end.EndIterationsStrategy;
import org.encog.neural.networks.training.strategy.end.EndTrainingStrategy;

public class ConcurrentTrainingManager implements Runnable {

	private List<ConcurrentTrainingPerformer> performers = new ArrayList<ConcurrentTrainingPerformer>();
	private List<TrainingJob> queue = new ArrayList<TrainingJob>();
	private Thread thread;
	private static ConcurrentTrainingManager instance;
	private StatusReportable report = new NullStatusReportable();

	private ConcurrentTrainingManager() {

	}

	public void clearPerformers() {
		this.performers.clear();
	}

	public void clearQueue() {
		this.queue.clear();
	}

	public void addPerformer(ConcurrentTrainingPerformer performer) {
		this.performers.add(performer);
	}

	public void addTrainingJob(TrainingJob job) {
		this.queue.add(job);

	}

	public ConcurrentTrainingPerformer waitForFreePerformer() {
		ConcurrentTrainingPerformer result = null;

		while (result == null) {
			for (ConcurrentTrainingPerformer performer : this.performers) {
				if (performer.ready())
					result = performer;
			}

			if (result == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}

		return result;
	}

	public void run() {

		this.report.report(this.queue.size(), 0, "Starting first job");

		int count = 0;
		for (TrainingJob job : this.queue) {
			// find a performer
			ConcurrentTrainingPerformer perform = this.waitForFreePerformer();
			perform.perform(job);
			count++;
			this.report.report(this.queue.size(), count, "Jobs submitted: "
					+ count + " of " + this.queue.size());
			reportErrors();
		}

		// now wait for all performers to finish
		boolean done = false;

		this.report.report(this.queue.size(), count,
				"No more jobs to submit, waiting for last job.");
		while (!done) {
			boolean foundOne = false;
			for (ConcurrentTrainingPerformer performer : this.performers) {
				if (!performer.ready())
					foundOne = true;
			}
			if (foundOne) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
			} else {
				done = true;
			}
		}

		this.report.report(this.queue.size(), count, "All training done.");
	}

	private void reportErrors() {
		for (TrainingJob job : this.queue) {
			if (job.getError() != null) {
				throw new NeuralNetworkError(job.getError());
			}
		}
	}

	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void join() {
		try {
			this.thread.join();
		} catch (InterruptedException e) {

		}

	}

	public static ConcurrentTrainingManager getInstance() {
		if (ConcurrentTrainingManager.instance == null)
			ConcurrentTrainingManager.instance = new ConcurrentTrainingManager();
		return ConcurrentTrainingManager.instance;
	}

	public void setReport(StatusReportable report) {
		this.report = report;
	}

	public void detectPerformers() {
		detectPerformers(false);
	}

	public void detectPerformers(boolean splitCores) {
		boolean useCPU = true;
		this.clearPerformers();

		// handle OpenCL mode
		if (Encog.getInstance().getCL() != null) {

			// should we let OpenCL run the CPU?
			if (Encog.getInstance().getCL().areCPUsPresent())
				useCPU = false;

			// add a performer for each OpenCL device.
			for (EncogCLDevice device : Encog.getInstance().getCL()
					.getDevices()) {
				addPerformer(new ConcurrentTrainingPerformerOpenCL(device));
			}
		}

		// now create CPU performers
		if (useCPU) {
			int threads;

			if (splitCores) {
				Runtime runtime = Runtime.getRuntime();
				threads = runtime.availableProcessors();
			} else {
				threads = 1;
			}

			for (int i = 0; i < threads; i++) {
				addPerformer(new ConcurrentTrainingPerformerCPU());
			}
		}
	}

	public TrainingJob addTrainRPROP(BasicNetwork network, NeuralDataSet training,
			boolean loadToMemory, double initialUpdate, double maxStep, EndTrainingStrategy ending) {
		RPROPJob job = new RPROPJob(network, training, loadToMemory,
				initialUpdate, maxStep);
		job.getStrategies().add(ending);
		this.addTrainingJob(job);
		return job;
	}

	public TrainingJob addTrainRPROP(BasicNetwork network, NeuralDataSet training,
			EndIterationsStrategy ending) {
		
		return addTrainRPROP(
				network,
				training,
				true,
				RPROPConst.DEFAULT_INITIAL_UPDATE,
				RPROPConst.DEFAULT_MAX_STEP,
				ending);
	}

}
