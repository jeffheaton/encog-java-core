package org.encog.ml.svm.training.search;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.encog.StatusReportable;
import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.svm.KernelType;
import org.encog.ml.svm.SVM;
import org.encog.ml.svm.training.EncodeSVMProblem;
import org.encog.ml.svm.training.SVMTrain;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.Format;
import org.encog.util.concurrency.job.ConcurrentJob;
import org.encog.util.concurrency.job.JobUnitContext;

public class SVMSearchJob extends ConcurrentJob implements MLTrain {

	/**
	 * The default starting number for C.
	 */
	public static final double DEFAULT_CONST_BEGIN = -5;

	/**
	 * The default ending number for C.
	 */
	public static final double DEFAULT_CONST_END = 15;

	/**
	 * The default step for C.
	 */
	public static final double DEFAULT_CONST_STEP = 2;

	/**
	 * The default gamma begin.
	 */
	public static final double DEFAULT_GAMMA_BEGIN = -10;

	/**
	 * The default gamma end.
	 */
	public static final double DEFAULT_GAMMA_END = 10;

	/**
	 * The default gamma step.
	 */
	public static final double DEFAULT_GAMMA_STEP = 1;

	/**
	 * The best values found for C.
	 */
	private double bestConst;

	/**
	 * The best values found for gamma.
	 */
	private double bestGamma;

	/**
	 * The best error.
	 */
	private double bestError;

	/**
	 * The current C.
	 */
	private double currentConst;

	/**
	 * The current gamma.
	 */
	private double currentGamma;

	/**
	 * The beginning value for C.
	 */
	private double constBegin = SVMSearchJob.DEFAULT_CONST_BEGIN;

	/**
	 * The step value for C.
	 */
	private double constStep = SVMSearchJob.DEFAULT_CONST_STEP;

	/**
	 * The ending value for C.
	 */
	private double constEnd = SVMSearchJob.DEFAULT_CONST_END;

	/**
	 * The beginning value for gamma.
	 */
	private double gammaBegin = SVMSearchJob.DEFAULT_GAMMA_BEGIN;

	/**
	 * The ending value for gamma.
	 */
	private double gammaEnd = SVMSearchJob.DEFAULT_GAMMA_END;

	/**
	 * The step value for gamma.
	 */
	private double gammaStep = SVMSearchJob.DEFAULT_GAMMA_STEP;

	private final SVM modelSVM;

	private boolean done;

	private int iterationCount;

	private boolean started;

	/**
	 * The problem to train for.
	 */
	private final svm_problem problem;

	/**
	 * The number of folds.
	 */
	private int fold = 5;

	/**
	 * Is the network setup.
	 */
	private boolean isSetup;

	private double svmTrain;

	private final MLDataSet training;

	public SVMSearchJob(final SVM svm, final MLDataSet dataSet,
			final StatusReportable report) {
		super(report);
		if (svm.getKernelType() != KernelType.RadialBasisFunction) {
			throw new EncogError(
					"To use SVM search train, the SVM kernel must be RBF.");
		}

		this.problem = EncodeSVMProblem.encode(dataSet, 0);
		this.modelSVM = svm;
		this.training = dataSet;
		this.bestError = Double.MAX_VALUE;
	}

	@Override
	public void addStrategy(final Strategy strategy) {
		throw new EncogError("Not supported.");

	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public void finishTraining() {
		stop();
	}

	private SVM generateSVM() {
		final SVM svm = new SVM(this.modelSVM.getInputCount(),
				this.modelSVM.getSVMType(), this.modelSVM.getKernelType());
		return svm;
	}

	/**
	 * @return the bestConst
	 */
	public final double getBestConst() {
		return this.bestConst;
	}

	/**
	 * @return the bestError
	 */
	public final double getBestError() {
		return this.bestError;
	}

	/**
	 * @return the bestGamma
	 */
	public final double getBestGamma() {
		return this.bestGamma;
	}

	/**
	 * @return the constBegin
	 */
	public final double getConstBegin() {
		return this.constBegin;
	}

	/**
	 * @return the constEnd
	 */
	public final double getConstEnd() {
		return this.constEnd;
	}

	/**
	 * @return the constStep
	 */
	public final double getConstStep() {
		return this.constStep;
	}

	/**
	 * @return the currentConst
	 */
	public final double getCurrentConst() {
		return this.currentConst;
	}

	/**
	 * @return the currentGamma
	 */
	public final double getCurrentGamma() {
		return this.currentGamma;
	}

	@Override
	public double getError() {
		return this.bestError;
	}

	/**
	 * @return the fold
	 */
	public final int getFold() {
		return this.fold;
	}

	/**
	 * @return the gammaBegin
	 */
	public final double getGammaBegin() {
		return this.gammaBegin;
	}

	/**
	 * @return the gammaEnd
	 */
	public final double getGammaEnd() {
		return this.gammaEnd;
	}

	/**
	 * @return the gammaStep
	 */
	public final double getGammaStep() {
		return this.gammaStep;
	}

	@Override
	public TrainingImplementationType getImplementationType() {
		// TODO Auto-generated method stub
		return TrainingImplementationType.Background;
	}

	@Override
	public int getIteration() {
		return this.iterationCount;
	}

	/**
	 * This method creates, and trains, a SVM with the best const and gamma.
	 * @return The best SVM. 
	 */
	@Override
	public MLMethod getMethod() {
		final SVM result = generateSVM();
		result.getParams().C = this.bestConst;
		result.getParams().gamma = this.bestGamma;
		result.setModel(svm.svm_train(this.problem, result.getParams()));
		return result;
	}

	@Override
	public List<Strategy> getStrategies() {
		return new ArrayList<Strategy>();
	}

	@Override
	public MLDataSet getTraining() {
		// TODO Auto-generated method stub
		return this.training;
	}

	/**
	 * @return the done
	 */
	public final boolean isDone() {
		return this.done;
	}

	@Override
	public boolean isTrainingDone() {
		return this.done;
	}

	@Override
	public void iteration() {
		if (!this.started) {
			process();
			this.iterationCount++;
		} else {
			try {
				Thread.sleep(10000);
			} catch (final InterruptedException e) {
			}
		}

		this.iterationCount++;

	}

	@Override
	public void iteration(final int count) {
		iteration();
	}

	@Override
	public int loadWorkload() {
		double d = (this.gammaEnd - this.gammaBegin) / this.gammaStep;
		d += (this.constEnd - this.constBegin) / this.constStep;
		return (int) d;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void performJobUnit(final JobUnitContext context) {
		final SVMJobPackage pack = (SVMJobPackage) context.getJobUnit();
		final double[] target = new double[this.problem.l];

		// set params
		pack.getSvm().getParams().gamma = pack.getGamma();
		pack.getSvm().getParams().C = pack.getC();

		// cross validate it
		svm.svm_cross_validation(this.problem, pack.getSvm().getParams(),
				this.fold, target);

		// determine the error
		final double error = SVMTrain.evaluate(pack.getSvm().getParams(),
				this.problem, target);

		/*System.out.println(pack.getGamma() + "," + pack.getC() + ","
				+ Format.formatPercent(error));*/
		// new best error?
		if (!Double.isNaN(error)) {
			if (error < this.bestError) {
				this.bestConst = pack.getC();
				this.bestGamma = pack.getGamma();
				this.bestError = error;
			}
		}

		// report progress
		final StringBuilder message = new StringBuilder();

		message.append("Current: gamma= ");
		message.append(Format.formatDouble(this.currentGamma, 2));
		message.append("; Const: ");
		message.append(Format.formatDouble(this.currentConst, 2));
		message.append("; Best Error: " + Format.formatPercent(error));

		reportStatus(context, message.toString());

	}

	@Override
	public Object requestNextTask() {
		if (this.done || getShouldStop()) {
			return null;
		}

		final SVM svm = generateSVM();

		// advance
		this.currentConst += this.constStep;
		if (this.currentConst > this.constEnd) {
			this.currentConst = this.constBegin;
			this.currentGamma += this.gammaStep;
			if (this.currentGamma > this.gammaEnd) {
				this.done = true;
			}
		}

		return new SVMJobPackage(svm, this.problem, this.currentConst,
				this.currentGamma, this.fold);
	}

	@Override
	public void resume(final TrainingContinuation state) {
	}

	/**
	 * @param constBegin the constBegin to set
	 */
	public final void setConstBegin(final double constBegin) {
		this.constBegin = constBegin;
	}

	/**
	 * @param constEnd the constEnd to set
	 */
	public final void setConstEnd(final double constEnd) {
		this.constEnd = constEnd;
	}

	/**
	 * @param constStep the constStep to set
	 */
	public final void setConstStep(final double constStep) {
		this.constStep = constStep;
	}

	/**
	 * @param currentConst the currentConst to set
	 */
	public final void setCurrentConst(final double currentConst) {
		this.currentConst = currentConst;
	}

	/**
	 * @param currentGamma the currentGamma to set
	 */
	public final void setCurrentGamma(final double currentGamma) {
		this.currentGamma = currentGamma;
	}

	@Override
	public void setError(final double error) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param fold the fold to set
	 */
	public final void setFold(final int fold) {
		this.fold = fold;
	}

	/**
	 * @param gammaBegin the gammaBegin to set
	 */
	public final void setGammaBegin(final double gammaBegin) {
		this.gammaBegin = gammaBegin;
	}

	/**
	 * @param gammaEnd the gammaEnd to set
	 */
	public final void setGammaEnd(final double gammaEnd) {
		this.gammaEnd = gammaEnd;
	}

	/**
	 * @param gammaStep the gammaStep to set
	 */
	public final void setGammaStep(final double gammaStep) {
		this.gammaStep = gammaStep;
	}

	@Override
	public void setIteration(final int iteration) {
		this.iterationCount = iteration;
	}

}
