package org.encog.neural.networks.training.nm;

import org.encog.Encog;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.EngineArray;

/**
 * The Nelder-Mead method is a commonly used parameter optimization method that
 * can be used for neural network training. It typically provides a good error
 * rate and is relatively fast.
 * 
 * Nelder-Mead must build a simplex, which is an n*(n+1) matrix of weights. If
 * you have a large number of weights, this matrix can quickly overflow memory.
 * 
 * The biggest enhancement that is needed for this trainer is to make use of
 * multi-threaded code to evaluate the speed evaluations when training on a
 * multi-core.
 * 
 * This implementation is based on the source code provided by John Burkardt
 * (http://people.sc.fsu.edu/~jburkardt/)
 * 
 * http://people.sc.fsu.edu/~jburkardt/c_src/asa047/asa047.c
 */
public class NelderMeadTraining extends BasicTraining {

	/**
	 * The network to be trained.
	 */
	private final BasicNetwork network;

	/**
	 * The best error rate.
	 */
	private double ynewlo;

	/**
	 * True if the network has converged, and no further training is needed.
	 */
	private boolean converged = false;

	/**
	 * Used to calculate the centroid.
	 */
	private final double ccoeff = 0.5;
	private double del;
	private final double ecoeff = 2.0;
	private final double eps = 0.001;
	private int ihi;
	private int ilo;
	private int jcount;
	private int l;
	private final int nn;
	private final double[] p;
	private final double[] p2star;
	private final double[] pbar;
	private final double[] pstar;
	private final double rcoeff = 1.0;
	private final double rq;
	private final double[] y;
	private double y2star;
	private double ylo;
	private double ystar;
	private double z;
	private final double[] start;
	private final double[] trainedWeights;
	private final double[] step;
	private int konvge;

	/**
	 * Construct a Nelder Mead trainer with a step size of 100.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training set to use.
	 */
	public NelderMeadTraining(final BasicNetwork network,
			final MLDataSet training) {
		this(network, training, 100);
	}

	/**
	 * Construct a Nelder Mead trainer with a definable step.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param stepValue
	 *            The step value. This value defines, to some degree the range
	 *            of different weights that will be tried.
	 */
	public NelderMeadTraining(final BasicNetwork network,
			final MLDataSet training, final double stepValue) {
		super(TrainingImplementationType.OnePass);
		this.network = network;
		setTraining(training);

		this.start = NetworkCODEC.networkToArray(network);
		this.trainedWeights = NetworkCODEC.networkToArray(network);

		final int n = this.start.length;

		this.p = new double[n * (n + 1)];
		this.pstar = new double[n];
		this.p2star = new double[n];
		this.pbar = new double[n];
		this.y = new double[n + 1];

		this.nn = n + 1;
		this.del = 1.0;
		this.rq = Encog.DEFAULT_DOUBLE_EQUAL * n;

		this.step = new double[NetworkCODEC.networkSize(network)];
		this.jcount = this.konvge = 500;
		EngineArray.fill(this.step, stepValue);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * Calculate the error for the neural network with a given set of weights.
	 * 
	 * @param weights
	 *            The weights to use.
	 * @return The current error.
	 */
	public double fn(final double[] weights) {
		NetworkCODEC.arrayToNetwork(weights, this.network);
		return this.network.calculateError(getTraining());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLMethod getMethod() {
		return this.network;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTrainingDone() {
		if (this.converged) {
			return true;
		} else {
			return super.isTrainingDone();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void iteration() {

		if (this.converged) {
			return;
		}

		final int n = this.start.length;

		for (int i = 0; i < n; i++) {
			this.p[i + n * n] = this.start[i];
		}
		this.y[n] = fn(this.start);
		for (int j = 0; j < n; j++) {
			final double x = this.start[j];
			this.start[j] = this.start[j] + this.step[j] * this.del;
			for (int i = 0; i < n; i++) {
				this.p[i + j * n] = this.start[i];
			}
			this.y[j] = fn(this.start);
			this.start[j] = x;
		}
		/*
		 * The simplex construction is complete.
		 * 
		 * Find highest and lowest Y values. YNEWLO = Y(IHI) indicates the
		 * vertex of the simplex to be replaced.
		 */
		this.ylo = this.y[0];
		this.ilo = 0;

		for (int i = 1; i < this.nn; i++) {
			if (this.y[i] < this.ylo) {
				this.ylo = this.y[i];
				this.ilo = i;
			}
		}
		/*
		 * Inner loop.
		 */
		for (;;) {
			/*
			 * if (kcount <= icount) { break; }
			 */
			this.ynewlo = this.y[0];
			this.ihi = 0;

			for (int i = 1; i < this.nn; i++) {
				if (this.ynewlo < this.y[i]) {
					this.ynewlo = this.y[i];
					this.ihi = i;
				}
			}
			/*
			 * Calculate PBAR, the centroid of the simplex vertices excepting
			 * the vertex with Y value YNEWLO.
			 */
			for (int i = 0; i < n; i++) {
				this.z = 0.0;
				for (int j = 0; j < this.nn; j++) {
					this.z = this.z + this.p[i + j * n];
				}
				this.z = this.z - this.p[i + this.ihi * n];
				this.pbar[i] = this.z / n;
			}
			/*
			 * Reflection through the centroid.
			 */
			for (int i = 0; i < n; i++) {
				this.pstar[i] = this.pbar[i] + this.rcoeff
						* (this.pbar[i] - this.p[i + this.ihi * n]);
			}
			this.ystar = fn(this.pstar);
			/*
			 * Successful reflection, so extension.
			 */
			if (this.ystar < this.ylo) {
				for (int i = 0; i < n; i++) {
					this.p2star[i] = this.pbar[i] + this.ecoeff
							* (this.pstar[i] - this.pbar[i]);
				}
				this.y2star = fn(this.p2star);
				/*
				 * Check extension.
				 */
				if (this.ystar < this.y2star) {
					for (int i = 0; i < n; i++) {
						this.p[i + this.ihi * n] = this.pstar[i];
					}
					this.y[this.ihi] = this.ystar;
				}
				/*
				 * Retain extension or contraction.
				 */
				else {
					for (int i = 0; i < n; i++) {
						this.p[i + this.ihi * n] = this.p2star[i];
					}
					this.y[this.ihi] = this.y2star;
				}
			}
			/*
			 * No extension.
			 */
			else {
				this.l = 0;
				for (int i = 0; i < this.nn; i++) {
					if (this.ystar < this.y[i]) {
						this.l = this.l + 1;
					}
				}

				if (1 < this.l) {
					for (int i = 0; i < n; i++) {
						this.p[i + this.ihi * n] = this.pstar[i];
					}
					this.y[this.ihi] = this.ystar;
				}
				/*
				 * Contraction on the Y(IHI) side of the centroid.
				 */
				else if (this.l == 0) {
					for (int i = 0; i < n; i++) {
						this.p2star[i] = this.pbar[i] + this.ccoeff
								* (this.p[i + this.ihi * n] - this.pbar[i]);
					}
					this.y2star = fn(this.p2star);
					/*
					 * Contract the whole simplex.
					 */
					if (this.y[this.ihi] < this.y2star) {
						for (int j = 0; j < this.nn; j++) {
							for (int i = 0; i < n; i++) {
								this.p[i + j * n] = (this.p[i + j * n] + this.p[i
										+ this.ilo * n]) * 0.5;
								this.trainedWeights[i] = this.p[i + j * n];
							}
							this.y[j] = fn(this.trainedWeights);
						}
						this.ylo = this.y[0];
						this.ilo = 0;

						for (int i = 1; i < this.nn; i++) {
							if (this.y[i] < this.ylo) {
								this.ylo = this.y[i];
								this.ilo = i;
							}
						}
						continue;
					}
					/*
					 * Retain contraction.
					 */
					else {
						for (int i = 0; i < n; i++) {
							this.p[i + this.ihi * n] = this.p2star[i];
						}
						this.y[this.ihi] = this.y2star;
					}
				}
				/*
				 * Contraction on the reflection side of the centroid.
				 */
				else if (this.l == 1) {
					for (int i = 0; i < n; i++) {
						this.p2star[i] = this.pbar[i] + this.ccoeff
								* (this.pstar[i] - this.pbar[i]);
					}
					this.y2star = fn(this.p2star);
					/*
					 * Retain reflection?
					 */
					if (this.y2star <= this.ystar) {
						for (int i = 0; i < n; i++) {
							this.p[i + this.ihi * n] = this.p2star[i];
						}
						this.y[this.ihi] = this.y2star;
					} else {
						for (int i = 0; i < n; i++) {
							this.p[i + this.ihi * n] = this.pstar[i];
						}
						this.y[this.ihi] = this.ystar;
					}
				}
			}
			/*
			 * Check if YLO improved.
			 */
			if (this.y[this.ihi] < this.ylo) {
				this.ylo = this.y[this.ihi];
				this.ilo = this.ihi;
			}
			this.jcount = this.jcount - 1;

			if (0 < this.jcount) {
				continue;
			}
			/*
			 * Check to see if minimum reached.
			 */
			// if (icount <= kcount)
			{
				this.jcount = this.konvge;

				this.z = 0.0;
				for (int i = 0; i < this.nn; i++) {
					this.z = this.z + this.y[i];
				}
				final double x = this.z / this.nn;

				this.z = 0.0;
				for (int i = 0; i < this.nn; i++) {
					this.z = this.z + Math.pow(this.y[i] - x, 2);
				}

				if (this.z <= this.rq) {
					break;
				}
			}
		}
		/*
		 * Factorial tests to check that YNEWLO is a local minimum.
		 */
		for (int i = 0; i < n; i++) {
			this.trainedWeights[i] = this.p[i + this.ilo * n];
		}
		this.ynewlo = this.y[this.ilo];

		boolean fault = false;

		for (int i = 0; i < n; i++) {
			this.del = this.step[i] * this.eps;
			this.trainedWeights[i] += this.del;
			this.z = fn(this.trainedWeights);
			if (this.z < this.ynewlo) {
				fault = true;
				break;
			}
			this.trainedWeights[i] = this.trainedWeights[i] - this.del
					- this.del;
			this.z = fn(this.trainedWeights);
			if (this.z < this.ynewlo) {
				fault = true;
				break;
			}
			this.trainedWeights[i] += this.del;
		}

		if (!fault) {
			this.converged = true;
		} else {
			/*
			 * Restart the procedure.
			 */
			for (int i = 0; i < n; i++) {
				this.start[i] = this.trainedWeights[i];
			}
			this.del = this.eps;
		}

		setError(this.ynewlo);
		NetworkCODEC.arrayToNetwork(this.trainedWeights, this.network);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {

	}

}
