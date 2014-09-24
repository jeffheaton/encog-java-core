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
package org.encog.ca.program.generic;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import org.encog.ca.program.basic.BasicProgram;
import org.encog.ca.program.basic.Movement;
import org.encog.ca.universe.ContinuousCell;
import org.encog.ca.universe.Universe;
import org.encog.ca.universe.UniverseCell;
import org.encog.mathutil.randomize.RangeRandomizer;

public class GenericCA extends BasicProgram implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[] physics;
	private Set<Trans> stepTrans = new TreeSet<Trans>();
	private Set<Trans> finalTrans = new TreeSet<Trans>();
	private Universe sourceUniverse;
	private Universe targetUniverse;
	private int ruleCount;

	public GenericCA() {
		super(Movement.MOVE_8WAY);
	}
	
	public GenericCA(Universe theSourceUniverse, int count) {
		super(Movement.MOVE_8WAY);
		this.sourceUniverse = theSourceUniverse;
		int countPer = (sourceUniverse.getCellFactory().size() * 3) + 1;
		this.physics = new double[countPer * count * 2];
		this.ruleCount = count;
	}

	public void iteration() {
		int height = this.sourceUniverse.getRows();
		int width = this.targetUniverse.getColumns();

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				processCell(row, col);
			}
		}
	}

	public Trans findTrans(Set<Trans> s, UniverseCell c) {
		double a = c.getAvg();
		Trans last = null;

		for (Trans t : s) {
			if (a < t.getLimit()) {
				return t;
			}
			last = t;
		}

		return last;

	}

	public void processCell(int row, int col) {
		Movement[] movements = getMovements();

		UniverseCell acc = this.sourceUniverse.getCellFactory().factor();
		UniverseCell thisCell = this.sourceUniverse.get(row, col);
		UniverseCell targetCell = this.targetUniverse.get(row, col);
		Trans trans = findTrans(stepTrans, thisCell);

		for (Movement movement : movements) {
			int otherRow = row + movement.getRowMovement();
			int otherCol = col + movement.getColumnmMovement();
			if (this.sourceUniverse.isValid(otherRow, otherCol)) {
				UniverseCell otherCell = this.sourceUniverse.get(otherRow,
						otherCol);
				UniverseCell tp = trans.calculate(otherCell);
				((ContinuousCell) acc).add(tp);
			}
		}

		Trans trans2 = findTrans(finalTrans, acc);
		acc = trans2.calculate(acc);
		((ContinuousCell) acc).clamp(-0.1, 1.1);
		targetCell.copy(acc);

	}

	@Override
	public void randomize() {
		int countPer = (sourceUniverse.getCellFactory().size() * 3) + 1;
		double[] d = new double[countPer * this.ruleCount * 2];

		for (int i = 0; i < this.physics.length; i++) {
			d[i] = RangeRandomizer.randomize(-1, 1);
		}

		setPhysics(d);
	}

	public void setPhysics(double[] d) {
		this.finalTrans.clear();
		this.stepTrans.clear();
		this.physics = d.clone();
		int cellSize = this.sourceUniverse.getCellFactory().size();
		int countPer = (cellSize * 3) + 1;
		int c = physics.length / countPer / 2;
		int idx1 = 0;
		int idx2 = c * countPer;
		for (int i = 0; i < c; i++) {
			this.stepTrans.add(new Trans(this.sourceUniverse.getCellFactory(),
					idx1, physics));
			this.finalTrans.add(new Trans(this.sourceUniverse.getCellFactory(),
					idx2, physics));
			idx1 += countPer;
			idx2 += countPer;
		}
	}

	/**
	 * @return the sourceUniverse
	 */
	public Universe getSourceUniverse() {
		return sourceUniverse;
	}

	/**
	 * @param sourceUniverse the sourceUniverse to set
	 */
	public void setSourceUniverse(Universe sourceUniverse) {
		this.sourceUniverse = sourceUniverse;
	}

	/**
	 * @return the targetUniverse
	 */
	public Universe getTargetUniverse() {
		return targetUniverse;
	}

	/**
	 * @param targetUniverse the targetUniverse to set
	 */
	public void setTargetUniverse(Universe targetUniverse) {
		this.targetUniverse = targetUniverse;
	}

}
