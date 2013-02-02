package org.encog.ml.ea.opp;

import java.util.Random;

import org.encog.util.obj.ChooseObject;
import org.encog.util.obj.ObjectHolder;

public class OperationList extends ChooseObject<EvolutionaryOperator>  {

	public int maxOffspring() {
		int result = 0;
		for(ObjectHolder<EvolutionaryOperator> holder: getList()) {
			result = Math.max(result, holder.getObj().offspringProduced());
		}
		return result;
	}

	public int maxParents() {
		int result = Integer.MIN_VALUE;
		for(ObjectHolder<EvolutionaryOperator> holder: getList()) {
			result = Math.max(result, holder.getObj().parentsNeeded());
		}
		return result;
	}
	
	public EvolutionaryOperator pickMaxParents(Random rnd, int maxParents) {
		
		double total = 0;
		for(ObjectHolder<EvolutionaryOperator> holder: getList()) {
			if( holder.getObj().parentsNeeded()<=maxParents ) {
				total+=holder.getProbability();
			}
		}
		
		double r = rnd.nextDouble() * total;
		double current = 0;
		for(ObjectHolder<EvolutionaryOperator> holder: getList()) {
			if( holder.getObj().parentsNeeded()<=maxParents ) {
				current+=holder.getProbability();
				if( r<current ) {
					return holder.getObj();
				}
			}
		}
		
		return null;
	}
	
}
