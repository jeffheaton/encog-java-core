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
package org.encog.ml.prg.opp;

import java.util.List;

import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

/**
 * The level holder class is passed down as a tree is mutated. The level holder
 * class is initially given the desired output of the program and tracks the
 * desired output for each of the nodes. This allows for type-safe crossovers
 * and mutations.
 */
public class LevelHolder {
	/**
	 * Determine if the specified child types are compatible with the parent types.
	 * @param parentTypes The parent types.
	 * @param childTypes The child types.
	 * @return True, if compatible.
	 */
	public static boolean compatibleTypes(final List<ValueType> parentTypes,
			final List<ValueType> childTypes) {
		for (final ValueType childType : childTypes) {
			if (!parentTypes.contains(childType)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * The current level in the tree.
	 */
	private int currentLevel;
	
	/**
	 * The current node, or node found.  This will be the mutation or crossover point.
	 */
	private ProgramNode nodeFound;

	/**
	 * The types we are expecting at this l.
	 */
	private List<ValueType> types;

	/**
	 * Construct the level holder.
	 * @param currentLevel The level to construct the holder for.
	 */
	public LevelHolder(final int currentLevel) {
		super();
		this.currentLevel = currentLevel;
	}

	/**
	 * Decrease the level.
	 */
	public void decreaseLevel() {
		this.currentLevel--;
	}

	/**
	 * @return the currentLevel
	 */
	public int getCurrentLevel() {
		return this.currentLevel;
	}

	/**
	 * @return the nodeFound
	 */
	public ProgramNode getNodeFound() {
		return this.nodeFound;
	}

	/**
	 * @return the types
	 */
	public List<ValueType> getTypes() {
		return this.types;
	}

	/**
	 * @param currentLevel
	 *            the currentLevel to set
	 */
	public void setCurrentLevel(final int currentLevel) {
		this.currentLevel = currentLevel;
	}

	/**
	 * @param nodeFound
	 *            the nodeFound to set
	 */
	public void setNodeFound(final ProgramNode nodeFound) {
		this.nodeFound = nodeFound;
	}

	/**
	 * @param types
	 *            the types to set
	 */
	public void setTypes(final List<ValueType> types) {
		this.types = types;
	}

}
