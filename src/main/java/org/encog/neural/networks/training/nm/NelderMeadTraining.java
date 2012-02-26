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
 * The Nelder–Mead method is a commonly use parameter optimization method that 
 * can be used for neural network training.
 * 
 * This implementation is based on the source code provided by 
 * John Burkardt (http://people.sc.fsu.edu/~jburkardt/)
 * 
 * http://people.sc.fsu.edu/~jburkardt/c_src/asa047/asa047.c
 */
public class NelderMeadTraining extends BasicTraining {

	private int icount;
	private int numres;
	private BasicNetwork network;
	private double ynewlo;
	private boolean converged = false;

	private double ccoeff = 0.5;
	private double del;
	private double ecoeff = 2.0;
	private double eps = 0.001;
	private int ihi;
	private int ilo;
	private int jcount;
	private int l;
	private int nn;
	private double[] p;
	private double[] p2star;
	private double[] pbar;
	private double[] pstar;
	private double rcoeff = 1.0;
	private double rq;
	private double[] y;
	private double y2star;
	private double ylo;
	private double ystar;
	private double z;
	private double[] start;
	private double[] trainedWeights;
	private double[] step;
	private int konvge;

	/**
	 * Construct the LMA object.
	 * 
	 * @param network
	 *            The network to train. Must have a single output neuron.
	 * @param training
	 *            The training data to use. Must be indexable.
	 */
	public NelderMeadTraining(final BasicNetwork network,
			final MLDataSet training) {
		super(TrainingImplementationType.OnePass);
		this.network = network;
		setTraining(training);

		this.start = NetworkCODEC.networkToArray(network);
		this.trainedWeights = NetworkCODEC.networkToArray(network);

		int n = this.start.length;

		p = new double[n * (n + 1)];
		pstar = new double[n];
		p2star = new double[n];
		pbar = new double[n];
		y = new double[(n + 1)];

		icount = 0;
		numres = 0;

		nn = n + 1;
		del = 1.0;
		rq = Encog.DEFAULT_DOUBLE_EQUAL * n;

		step = new double[NetworkCODEC.networkSize(network)];
		jcount = this.konvge = 500;
		EngineArray.fill(step, 100);

	}

	@Override
	public void iteration() {

		if (this.converged)
			return;

		int n = start.length;

		for (int i = 0; i < n; i++) {
			p[i + n * n] = start[i];
		}
		y[n] = fn(start);
		icount++;

		for (int j = 0; j < n; j++) {
			double x = start[j];
			start[j] = start[j] + step[j] * del;
			for (int i = 0; i < n; i++) {
				p[i + j * n] = start[i];
			}
			y[j] = fn(start);
			icount++;
			start[j] = x;
		}
		/*                 
		  The simplex construction is complete.
		                    
		  Find highest and lowest Y values.  YNEWLO = Y(IHI) indicates
		  the vertex of the simplex to be replaced.
		*/
		ylo = y[0];
		ilo = 0;

		for (int i = 1; i < nn; i++) {
			if (y[i] < ylo) {
				ylo = y[i];
				ilo = i;
			}
		}
		/*
		  Inner loop.
		*/
		for (;;) {
			/*if (kcount <= icount) {
				break;
			}*/
			ynewlo = y[0];
			ihi = 0;

			for (int i = 1; i < nn; i++) {
				if (ynewlo < y[i]) {
					ynewlo = y[i];
					ihi = i;
				}
			}
			/*
			  Calculate PBAR, the centroid of the simplex vertices
			  excepting the vertex with Y value YNEWLO.
			*/
			for (int i = 0; i < n; i++) {
				z = 0.0;
				for (int j = 0; j < nn; j++) {
					z = z + p[i + j * n];
				}
				z = z - p[i + ihi * n];
				pbar[i] = z / n;
			}
			/*
			  Reflection through the centroid.
			*/
			for (int i = 0; i < n; i++) {
				pstar[i] = pbar[i] + rcoeff * (pbar[i] - p[i + ihi * n]);
			}
			ystar = fn(pstar);
			icount++;
			/*
			  Successful reflection, so extension.
			*/
			if (ystar < ylo) {
				for (int i = 0; i < n; i++) {
					p2star[i] = pbar[i] + ecoeff * (pstar[i] - pbar[i]);
				}
				y2star = fn(p2star);
				icount++;
				/*
				  Check extension.
				*/
				if (ystar < y2star) {
					for (int i = 0; i < n; i++) {
						p[i + ihi * n] = pstar[i];
					}
					y[ihi] = ystar;
				}
				/*
				  Retain extension or contraction.
				*/
				else {
					for (int i = 0; i < n; i++) {
						p[i + ihi * n] = p2star[i];
					}
					y[ihi] = y2star;
				}
			}
			/*
			  No extension.
			*/
			else {
				l = 0;
				for (int i = 0; i < nn; i++) {
					if (ystar < y[i]) {
						l = l + 1;
					}
				}

				if (1 < l) {
					for (int i = 0; i < n; i++) {
						p[i + ihi * n] = pstar[i];
					}
					y[ihi] = ystar;
				}
				/*
				  Contraction on the Y(IHI) side of the centroid.
				*/
				else if (l == 0) {
					for (int i = 0; i < n; i++) {
						p2star[i] = pbar[i] + ccoeff
								* (p[i + ihi * n] - pbar[i]);
					}
					y2star = fn(p2star);
					icount++;
					/*
					  Contract the whole simplex.
					*/
					if (y[ihi] < y2star) {
						for (int j = 0; j < nn; j++) {
							for (int i = 0; i < n; i++) {
								p[i + j * n] = (p[i + j * n] + p[i + ilo * n]) * 0.5;
								this.trainedWeights[i] = p[i + j * n];
							}
							y[j] = fn(this.trainedWeights);
							icount++;
						}
						ylo = y[0];
						ilo = 0;

						for (int i = 1; i < nn; i++) {
							if (y[i] < ylo) {
								ylo = y[i];
								ilo = i;
							}
						}
						continue;
					}
					/*
					  Retain contraction.
					*/
					else {
						for (int i = 0; i < n; i++) {
							p[i + ihi * n] = p2star[i];
						}
						y[ihi] = y2star;
					}
				}
				/*
				  Contraction on the reflection side of the centroid.
				*/
				else if (l == 1) {
					for (int i = 0; i < n; i++) {
						p2star[i] = pbar[i] + ccoeff * (pstar[i] - pbar[i]);
					}
					y2star = fn(p2star);
					icount++;
					/*
					  Retain reflection?
					*/
					if (y2star <= ystar) {
						for (int i = 0; i < n; i++) {
							p[i + ihi * n] = p2star[i];
						}
						y[ihi] = y2star;
					} else {
						for (int i = 0; i < n; i++) {
							p[i + ihi * n] = pstar[i];
						}
						y[ihi] = ystar;
					}
				}
			}
			/*
			  Check if YLO improved.
			*/
			if (y[ihi] < ylo) {
				ylo = y[ihi];
				ilo = ihi;
			}
			jcount = jcount - 1;

			if (0 < jcount) {
				continue;
			}
			/*
			  Check to see if minimum reached.
			*/
			//if (icount <= kcount) 
			{
				jcount = konvge;

				z = 0.0;
				for (int i = 0; i < nn; i++) {
					z = z + y[i];
				}
				double x = z / nn;

				z = 0.0;
				for (int i = 0; i < nn; i++) {
					z = z + Math.pow(y[i] - x, 2);
				}

				if (z <= rq) {
					break;
				}
			}
		}
		/*
		  Factorial tests to check that YNEWLO is a local minimum.
		*/
		for (int i = 0; i < n; i++) {
			this.trainedWeights[i] = p[i + ilo * n];
		}
		ynewlo = y[ilo];

		boolean fault = false;

		for (int i = 0; i < n; i++) {
			del = step[i] * eps;
			this.trainedWeights[i] += del;
			z = fn(this.trainedWeights);
			icount++;
			if (z < ynewlo) {
				fault = true;
				break;
			}
			this.trainedWeights[i] = this.trainedWeights[i] - del - del;
			z = fn(this.trainedWeights);
			icount++;
			if (z < ynewlo) {
				fault = true;
				break;
			}
			this.trainedWeights[i] += del;
		}

		if (!fault) {
			this.converged = true;
		} else {
			/*
			  Restart the procedure.
			*/
			for (int i = 0; i < n; i++) {
				start[i] = this.trainedWeights[i];
			}
			del = eps;
			numres++;
		}

		setError(ynewlo);
		NetworkCODEC.arrayToNetwork(this.trainedWeights, network);
	}

	@Override
	public boolean canContinue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
		// TODO Auto-generated method stub

	}

	@Override
	public MLMethod getMethod() {
		return this.network;
	}

	public double fn(double[] weights) {
		NetworkCODEC.arrayToNetwork(weights, this.network);
		return network.calculateError(getTraining());
	}

	public boolean isTrainingDone() {
		if (this.converged)
			return true;
		else
			return super.isTrainingDone();
	}

}
