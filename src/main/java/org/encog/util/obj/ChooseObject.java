package org.encog.util.obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.randomize.RandomChoice;

public class ChooseObject<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final List<ObjectHolder<T>> list = new ArrayList<ObjectHolder<T>>();
	private RandomChoice chooser;
	
	public void finalizeStructure() {
		double[] d = new double[size()];
		for(int i=0;i<size();i++) {
			d[i] = list.get(i).getProbability();
		}
		
		this.chooser = new RandomChoice(d);
	}
	
	public void add(double probability, T opp) {
		list.add(new ObjectHolder<T>(opp,probability));
	}
	
	public int size() {
		return list.size();
	}
	
	public T pick(Random theGenerator) {
		int index = this.chooser.generate(theGenerator);
		return this.list.get(index).getObj();
	}
	
	public List<ObjectHolder<T>> getList() {
		return this.list;
	}

	public void clear() {
		this.list.clear();
	}

	public T pickFirst() {
		return this.list.get(0).getObj();
	}
}
