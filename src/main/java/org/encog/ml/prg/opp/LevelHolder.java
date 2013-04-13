package org.encog.ml.prg.opp;

import java.util.List;

import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

public class LevelHolder {

	public static boolean compatibleTypes(final List<ValueType> parentTypes,
			final List<ValueType> childTypes) {
		for (final ValueType childType : childTypes) {
			if (!parentTypes.contains(childType)) {
				return false;
			}
		}
		return true;
	}

	private int currentLevel;
	private ProgramNode nodeFound;

	private List<ValueType> types;

	public LevelHolder(final int currentLevel) {
		super();
		this.currentLevel = currentLevel;
	}

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
