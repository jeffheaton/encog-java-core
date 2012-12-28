package org.encog.ml.prg.util;

import java.io.Serializable;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class TraverseProgram implements Serializable {
	private final OpCodeHeader header = new OpCodeHeader();
	private final EncogProgram program;
	private final EPLHolder holder;
	private ProgramExtensionTemplate template;
	private int currentIndex;
	private boolean started = false;
	private int opcodesRead;

	public TraverseProgram(TraverseProgram trav) {
		this(trav.program);
		begin(trav.getFrameIndex());
	}
	
	public TraverseProgram(EncogProgram theProgram) {
		this.program = theProgram;
		this.holder = this.program.getHolder();
	}

	public void begin(int idx) {
		this.currentIndex = idx;
		this.started = false;
		this.opcodesRead = 0;
	}

	private void readCurrent() {
		program.getHolder().readNodeHeader(this.program.getIndividual(),
				this.currentIndex, this.header);
		this.template = this.program.getContext().getFunctions()
				.getOpCode(this.header.getOpcode());
	}

	public boolean next() {
		// if we've already started, then advance to the next one.
		if (started) {
			this.currentIndex += template.getInstructionSize(this.header);
			this.opcodesRead++;
		}
		started = true;

		if (this.currentIndex < this.program.getProgramLength()) {
			readCurrent();
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
		while (next()) {
			result++;
		}
		return result;
	}

	public int getFrameIndex() {
		return this.currentIndex;
	}

	public boolean isLeaf() {
		return this.template.getChildNodeCount() == 0;
	}

	/**
	 * @return the opcodesRead
	 */
	public int getOpcodesRead() {
		return opcodesRead;
	}

	public int getNextIndex() {
		return this.getFrameIndex()
				+ this.template.getInstructionSize(this.header);
	}

	public double readDouble() {
		double result = this.holder.readDouble(this.program.getIndividual(),
				this.currentIndex+1);
		return result;
	}

	public EncogProgram getProgram() {
		return this.program;
	}
}
