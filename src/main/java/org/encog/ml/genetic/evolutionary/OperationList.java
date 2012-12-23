package org.encog.ml.genetic.evolutionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.mathutil.randomize.RandomChoice;

public class OperationList {
	private final List<OperationHolder> list = new ArrayList<OperationHolder>();
	private RandomChoice chooser;
	
	public void finalizeStructure() {
		double[] d = new double[size()];
		for(int i=0;i<size();i++) {
			d[i] = list.get(i).getProbability();
		}
		
		this.chooser = new RandomChoice(d);
	}
	
	public void add(double probability, EvolutionaryOperator opp) {
		list.add(new OperationHolder(opp,probability));
	}
	
	public int size() {
		return list.size();
	}
	
	public EvolutionaryOperator pickOperator(Random theGenerator) {
		int index = this.chooser.generate(theGenerator);
		return this.list.get(index).getOpp();
	}

	public int maxOffspring() {
		int result = Integer.MIN_VALUE;
		for(OperationHolder holder: this.list) {
			result = Math.max(result, holder.getOpp().offspringProduced());
		}
		return result;
	}

	public int maxParents() {
		int result = Integer.MIN_VALUE;
		for(OperationHolder holder: this.list) {
			result = Math.max(result, holder.getOpp().parentsNeeded());
		}
		return result;
	}
}
