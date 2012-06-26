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
		int idx = 0;
		while( c<=128) {
			output[idx++] = (this.rule & c)!=0;
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
