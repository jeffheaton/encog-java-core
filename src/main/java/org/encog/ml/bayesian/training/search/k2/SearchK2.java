package org.encog.ml.bayesian.training.search.k2;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.EncogMath;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.query.enumerate.EnumerationQuery;
import org.encog.ml.bayesian.training.TrainBayesian;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

public class SearchK2 implements BayesSearch {

	private MLDataSet data;
	private BayesianNetwork network;
	private TrainBayesian train;
	private double lastCalculatedP;
	private final List<BayesianEvent> nodeOrdering = new ArrayList<BayesianEvent>();
	
	@Override
	public void init(TrainBayesian theTrainer,BayesianNetwork theNetwork, MLDataSet theData) {
		this.network = theNetwork;
		this.data = theData;
		this.train = theTrainer;
		orderNodes();
	}
	
	/**
	 * Basically the goal here is to get the classification target, if it exists,
	 * to go first. This will greatly enhance K2's effectiveness.
	 */
	private void orderNodes() {
		this.nodeOrdering.clear();
		
		// is there a classification target?
		if( this.network.getClassificationTarget()!=-1 ) {
			this.nodeOrdering.add(this.network.getClassificationTargetEvent());
		}
		
		
		// now add the others
		for(BayesianEvent event: this.network.getEvents()) {
			if( !this.nodeOrdering.contains(event) ) {
				this.nodeOrdering.add(event);
			}
		}
	}
	
	private BayesianEvent findZ(BayesianEvent event, int n, double old) {
		BayesianEvent result = null;
		double maxChildP = Double.NEGATIVE_INFINITY;
		//System.out.println("Finding parent for: " + event.toString());
		for(int i=0;i<n;i++) {
			BayesianEvent trialParent = this.nodeOrdering.get(i);
			List<BayesianEvent> parents = new ArrayList<BayesianEvent>();
			parents.addAll(event.getParents());
			parents.add(trialParent);
			//System.out.println("Calculating adding " + trialParent.toString() + " to " + event.toString());
			this.lastCalculatedP = this.calculateG(network, event, parents);
			//System.out.println("lastP:" + this.lastCalculatedP);
			//System.out.println("old:" + old);
			if( this.lastCalculatedP>old && this.lastCalculatedP>maxChildP ) {
				result = trialParent;
				maxChildP = this.lastCalculatedP;
				//System.out.println("Current best is: " + result.toString());
			}			
		}
		
		this.lastCalculatedP = maxChildP;
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
	 * @param desiredValue The desired value.
	 * @return The value N. 
	 */
	public int calculateN(BayesianNetwork network, BayesianEvent event,
			List<BayesianEvent> parents, int[] parentInstance, int desiredValue) {
		int result = 0;
		int eventIndex = network.getEventIndex(event);

		for (MLDataPair pair : this.data) {
			int[] d = this.network.determineClasses(pair.getInput());

			if ( d[eventIndex] == desiredValue) {
				boolean reject = false;

				for (int i = 0; i < parentInstance.length; i++) {
					BayesianEvent parentEvent = parents.get(i);
					int parentIndex = network.getEventIndex(parentEvent);
					if (parentInstance[i] != d[parentIndex]) {
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
			int[] d = this.network.determineClasses( pair.getInput());

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
		int r = event.getChoices().size();

		int[] args = new int[parents.size()];
		
		do {
			double n = EncogMath.factorial(r - 1);
			double d = EncogMath.factorial(calculateN(network, event,
					parents, args) + r - 1);
			double p1 = n/d;
			
			double p2 = 1;
			for(int k = 0; k<event.getChoices().size(); k++) {
				p2 *= EncogMath.factorial(calculateN(network,event,parents,args,k));
			}						
			
			result*=p1*p2;
		} while(EnumerationQuery.roll(parents, args));
		
		return result;
	}

	
	@Override
	public void iteration() {
		orderNodes();
		
		for(int i = 0; i<this.data.getInputSize();i++) {
			BayesianEvent event = this.nodeOrdering.get(i);
			double oldP = this.calculateG(network, event, event.getParents());

			while(  event.getParents().size()<this.train.getMaximumParents() ) {
				BayesianEvent z = findZ(event,i,oldP);
				if(z!=null) {
					//System.out.println("Before: " + this.network.toString());
					this.network.createDependancy(z, event);
					//System.out.println("After: " + this.network.toString());
					oldP = this.lastCalculatedP;
				} else {
					break;
				}
			}
		}
	}
	
}

