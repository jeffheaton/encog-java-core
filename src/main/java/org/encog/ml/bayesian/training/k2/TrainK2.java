package org.encog.ml.bayesian.training.k2;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.EncogMath;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.query.enumerate.EnumerationQuery;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public class TrainK2 extends BasicTraining {

	private final MLDataSet data;
	private final BayesianNetwork network;
	private final int maximumParents;
	private double lastCalculatedP;

	public TrainK2(BayesianNetwork theNetwork, MLDataSet theData, int theMaximumParents) {
		super(TrainingImplementationType.Iterative);
		this.network = theNetwork;
		this.data = theData;
		this.maximumParents = theMaximumParents;
		this.network.removeAllRelations();
	}

	@Override
	public void iteration() {
		for(int i = 0; i<this.data.getInputSize();i++) {
			BayesianEvent event = this.network.getEvents().get(i);
			double oldP = this.calculateG(network, event, event.getParents());

			while(  event.getParents().size()<this.maximumParents ) {
				BayesianEvent z = findZ(event,i,oldP);
				if(z!=null) {
					this.network.createDependancy(z, event);
					oldP = this.lastCalculatedP;
				} else {
					break;
				}
			}
		}

	}
	
	private BayesianEvent findZ(BayesianEvent event, int n, double old) {
		BayesianEvent result = null;
		double maxChildP = Double.NEGATIVE_INFINITY;
		
		for(int i=0;i<n;i++) {
			BayesianEvent trialChild = this.network.getEvents().get(i);
			List<BayesianEvent> parents = new ArrayList<BayesianEvent>();
			parents.addAll(event.getParents());
			parents.add(trialChild);
			this.lastCalculatedP = this.calculateG(network, event, parents);
			if( this.lastCalculatedP>old && this.lastCalculatedP>maxChildP ) {
				result = trialChild;
				maxChildP = this.lastCalculatedP;
			}			
		}
		
		this.lastCalculatedP = maxChildP;
		return result;
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {

	}

	@Override
	public MLMethod getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Calculate the value N, which is the number of cases, from the training data, where the
	 * desiredValue matches the training data.  Only cases where the parents match the specifed
	 * parent instance are considered.
	 * @param network The network to calculate for.
	 * @param event The event we are calculating for. (variable i)
	 * @param parents The parents of the specified event we are considering.
	 * @param parentInstance The parent instance we are looking for.
	 * @param desiredValue The desired value.
	 * @return The value N. 
	 */
	public int calculateN(BayesianNetwork network, BayesianEvent event,
			List<BayesianEvent> parents, int[] parentInstance, int desiredValue) {
		int result = 0;
		int eventIndex = network.getEventIndex(event);

		for (MLDataPair pair : this.data) {
			double[] d = pair.getInputArray();

			if (((int) d[eventIndex]) == desiredValue) {
				boolean reject = false;

				for (int i = 0; i < parentInstance.length; i++) {
					BayesianEvent parentEvent = parents.get(i);
					int parentIndex = network.getEventIndex(parentEvent);
					if (parentInstance[i] != ((int) d[parentIndex])) {
						reject = true;
						break;
					}
				}

				if (!reject) {
					result++;
				}
			}
		}
		return result;
	}

	/**
	 * Calculate the value N, which is the number of cases, from the training data, where the
	 * desiredValue matches the training data.  Only cases where the parents match the specifed
	 * parent instance are considered.
	 * @param network The network to calculate for.
	 * @param event The event we are calculating for. (variable i)
	 * @param parents The parents of the specified event we are considering.
	 * @param parentInstance The parent instance we are looking for.
	 * @return The value N. 
	 */
	public int calculateN(BayesianNetwork network, BayesianEvent event,
			List<BayesianEvent> parents, int[] parentInstance) {
		int result = 0;

		for (MLDataPair pair : this.data) {
			double[] d = pair.getInputArray();

			boolean reject = false;

			for (int i = 0; i < parentInstance.length; i++) {
				BayesianEvent parentEvent = parents.get(i);
				int parentIndex = network.getEventIndex(parentEvent);
				if (parentInstance[i] != ((int) d[parentIndex])) {
					reject = true;
					break;
				}
			}

			if (!reject) {
				result++;
			}
		}
		return result;
	}

	public double calculateG(BayesianNetwork network,
			BayesianEvent event, List<BayesianEvent> parents) {
		double result = 1.0;
		int r = event.getChoices().length;

		int[] args = new int[parents.size()];
		
		do {
			double n = EncogMath.factorial(r - 1);
			double d = EncogMath.factorial(calculateN(network, event,
					parents, args) + r - 1);
			double p1 = n/d;
			
			double p2 = 1;
			for(int k = 0; k<event.getChoices().length; k++) {
				p2 *= EncogMath.factorial(calculateN(network,event,parents,args,k));
			}						
			
			result*=p1*p2;
		} while(EnumerationQuery.roll(parents, args));
		
		return result;
	}

	/**
	 * @return the network
	 */
	public BayesianNetwork getNetwork() {
		return network;
	}

	/**
	 * @return the maximumParents
	 */
	public int getMaximumParents() {
		return maximumParents;
	}
	
	
}

