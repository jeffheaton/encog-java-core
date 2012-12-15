package org.encog.ml.prg.util;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class TraverseProgram {
	private final EncogProgram program;
	private final EPLHolder holder;
	private ProgramExtensionTemplate template;
	private int currentIndex;
	private boolean started = false;
	private int opcodesRead;
	private short opcode;

	public TraverseProgram(EncogProgram theProgram) {
		this.program = theProgram;
		this.holder = this.program.getHolder();
	}

	public void begin(int idx) {
		this.currentIndex = idx;
		this.started = false;
		this.opcodesRead = 0;
	}
	
	public short getOpcode() {
		return this.opcode;
	}
	
	public int getParam1() {
		return this.program.getHolder().readHeaderParam1(this.program.getIndividual(), this.currentIndex);
	}
	
	public short getParam2() {
		return this.program.getHolder().readHeaderParam2(this.program.getIndividual(), this.currentIndex);
	}

	private void readCurrent() {
		this.opcode = this.holder.readHeaderOpcode(this.program.getIndividual(), this.currentIndex);
		this.template = this.program.getContext().getFunctions().getOpCode(opcode);
	}

	public boolean next() {
		// if we've already started, then advance to the next one.
		if (started) {
			this.currentIndex += template.getInstructionSize(this.program.getHolder(),this.program.getIndividual(),this.currentIndex);
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
				+ this.template.getInstructionSize(this.holder,this.program.getIndividual(),this.currentIndex);
	}

	public double readDouble() {
		double result = this.holder.readDouble(this.program.getIndividual(),
				this.currentIndex+1);
		return result;
	}

	public String readString() {
		String result = this.holder.readString(this.program.getIndividual(),
				this.currentIndex+1, this.getParam2());
/*		this.programCounter += EPLUtil.roundToFrame(encodedLength)
				/ EPLHolder.FRAME_SIZE;*/
		return result;
	}
	
	
}
