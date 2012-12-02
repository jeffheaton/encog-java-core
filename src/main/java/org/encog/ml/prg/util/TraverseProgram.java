package org.encog.ml.prg.util;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class TraverseProgram {
	private final OpCodeHeader header = new OpCodeHeader();
	private final EncogProgram program;
	private final EPLHolder holder;
	private ProgramExtensionTemplate template;
	private int currentIndex;
	private int stackSize;
	
	public TraverseProgram(EncogProgram theProgram) {
		this.program = theProgram;
		this.holder = this.program.getHolder();
	}
	
	public void begin(int idx) {
		this.currentIndex = idx;
		program.getHolder().readNodeHeader(this.program.getIndividual(), this.currentIndex, this.header);
		this.template = this.program.getContext().getFunctions().getOpCode(this.header.getOpcode());
		this.stackSize = 1;
	}
	
	public boolean next() {
		if( this.stackSize>0 && this.currentIndex<this.program.getProgramLength() ) {
			this.currentIndex+=template.getInstructionSize(this.header);
			this.holder.readNodeHeader(this.program.getIndividual(), this.currentIndex, this.header);
			this.template = this.program.getContext().getFunctions().getOpCode(this.header.getOpcode());
			if( template.getChildNodeCount()>0 ) {
				this.stackSize-=template.getChildNodeCount();
			} 
				
			this.stackSize++;
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * @return the header
	 */
	public OpCodeHeader getHeader() {
		return header;
	}

	/**
	 * @return the template
	 */
	public ProgramExtensionTemplate getTemplate() {
		return template;
	}

	public int countRemaining() {
		int result = 0;
		while(next()) {
			result++;
		}
		return result;
	}

	public int getCurrentIndex() {
		return this.currentIndex;
	}

	public boolean isLeaf() {
		return this.template.getChildNodeCount()==0;
	}

	public int getNextIndex() {
		return this.getCurrentIndex()+this.template.getInstructionSize(this.header);
	}
}
