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
package org.encog.ca.program.elementary;

import org.encog.ca.program.basic.BasicProgram;
import org.encog.ca.program.basic.Movement;
import org.encog.ca.universe.Universe;

public class ElementaryCA extends BasicProgram {

	private boolean[] output = new boolean[8];
	private int currentRow;
	private int rule;
	private Universe sourceUniverse;
	private Universe targetUniverse;
	
	public ElementaryCA(Universe theUniverse, int theRule) {
		super(Movement.MOVE_2WAY);
		this.sourceUniverse = theUniverse;
		this.targetUniverse = theUniverse;
		this.rule = theRule;
		this.currentRow = 1;
		
		theUniverse.get(0, theUniverse.getColumns()/2).set(0, 1.0); // seed
		
		int c = 1;
		int idx = 7;
		while( idx>0) {
			output[idx--] = (this.rule & c)!=0;
			c*=2;
		}
	}

	@Override
	public void iteration() {
		int prevRow = this.currentRow-1;
		
		for(int i=0;i<this.sourceUniverse.getColumns()-2;i++) {
			boolean result = false;
			boolean a = this.sourceUniverse.get(prevRow, i).get(0)>0;
			boolean b = this.sourceUniverse.get(prevRow, i+1).get(0)>0;
			boolean c = this.sourceUniverse.get(prevRow, i+2).get(0)>0;
			
			if( a && b && c ) {
				result = this.output[0];
			}
			else if( a && b && !c ) {
				result = this.output[1];
			}
			else if( a && !b && c ) {
				result = this.output[2];
			}
			else if( a && !b && !c ) {
				result = this.output[3];
			}
			else if( !a && b && c ) {
				result = this.output[4];
			}
			else if( !a && b && !c ) {
				result = this.output[5];
			}
			else if(! a && !b && c ) {
				result = this.output[6];
			}
			else if( !a && !b && !c ) {
				result = this.output[7];
			}
			
			this.targetUniverse.get(this.currentRow, i+1).set(0, result?1.0:0.0);
		}
		
		this.currentRow++;
		
	}

	@Override
	public void randomize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Universe getSourceUniverse() {
		return this.sourceUniverse;
	}

	@Override
	public void setSourceUniverse(Universe theSourceUniverse) {
		this.sourceUniverse = theSourceUniverse;
		
	}

	@Override
	public Universe getTargetUniverse() {
		return this.targetUniverse;
	}

	@Override
	public void setTargetUniverse(Universe theTargetUniverse) {
		this.targetUniverse = theTargetUniverse;
		
	}
	
}
