/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.mathutil.randomize.RandomChoice;

/**
 * This class is used to choose between several objects with a specified probability.
 * @param <T> The type of object to choose from.
 */
public class ChooseObject<T> implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The objects that we are choosing from.
	 */
	private final List<ObjectHolder<T>> list = new ArrayList<ObjectHolder<T>>();
	
	/**
	 * The random choose.
	 */
	private RandomChoice chooser;
	
	/**
	 * Finalize the structure and set the probabilities.
	 */
	public void finalizeStructure() {
		double[] d = new double[size()];
		for(int i=0;i<size();i++) {
			d[i] = list.get(i).getProbability();
		}
		
		this.chooser = new RandomChoice(d);
	}
	
	/**
	 * Add an object.
	 * @param probability The probability to choose this object.
	 * @param opp The object to add.
	 */
	public void add(double probability, T opp) {
		list.add(new ObjectHolder<T>(opp,probability));
	}
	
	/**
	 * @return The number of objects added.
	 */
	public int size() {
		return list.size();
	}
	
	/**
	 * Choose a random object.
	 * @param theGenerator
	 * @return The random choice.
	 */
	public T pick(Random theGenerator) {
		int index = this.chooser.generate(theGenerator);
		return this.list.get(index).getObj();
	}
	
	/**
	 * @return The object to choose from.
	 */
	public List<ObjectHolder<T>> getList() {
		return this.list;
	}

	/**
	 * CLear all objects from the collection.
	 */
	public void clear() {
		this.list.clear();
	}

	/**
	 * @return The first object in the list.
	 */
	public T pickFirst() {
		return this.list.get(0).getObj();
	}
}
