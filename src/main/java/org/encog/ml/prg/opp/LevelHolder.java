package org.encog.ml.prg.opp;

import java.util.List;

import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

public class LevelHolder {
	
	private int currentLevel;
	private ProgramNode nodeFound; 
	private List<ValueType> types;
	
	public LevelHolder(int currentLevel) {
		super();
		this.currentLevel = currentLevel;
	}
	/**
	 * @return the currentLevel
	 */
	public int getCurrentLevel() {
		return currentLevel;
	}
	/**
	 * @param currentLevel the currentLevel to set
	 */
	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
	/**
	 * @return the nodeFound
	 */
	public ProgramNode getNodeFound() {
		return nodeFound;
	}
	/**
	 * @param nodeFound the nodeFound to set
	 */
	public void setNodeFound(ProgramNode nodeFound) {
		this.nodeFound = nodeFound;
	}
	/**
	 * @return the types
	 */
	public List<ValueType> getTypes() {
		return types;
	}
	/**
	 * @param types the types to set
	 */
	public void setTypes(List<ValueType> types) {
		this.types = types;
	}
	public void decreaseLevel() {
		this.currentLevel--;
	}
	public static boolean compatibleTypes(List<ValueType> parentTypes,
			List<ValueType> childTypes) {
		for(ValueType childType: childTypes) {
			if( !parentTypes.contains(childType) ) {
				return false;
			}
		}
		return true;
	}
	
	
	
}
