package org.encog.ml.ea.opp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.mathutil.randomize.RandomChoice;
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
}
