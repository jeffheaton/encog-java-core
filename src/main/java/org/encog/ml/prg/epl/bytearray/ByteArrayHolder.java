package org.encog.ml.prg.epl.bytearray;

import java.io.UnsupportedEncodingException;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.EPLUtil;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.util.EngineArray;

public class ByteArrayHolder implements EPLHolder {
	private byte[] code;
	private int maxIndividualSize;
	
	public ByteArrayHolder(int thePopulationSize, int theMaxIndividualSize) {
		this.maxIndividualSize = theMaxIndividualSize;
		code = new byte[thePopulationSize*maxIndividualSize];
	}
	
	public void writeNode(int individual, int index, short opcode, int param1, short param2 ) {
		int absoluteIndex = (individual*this.maxIndividualSize)+(index*EPLHolder.FRAME_SIZE);
		EPLUtil.shortToBytes(opcode, this.code, absoluteIndex);
		EPLUtil.intToBytes(param1, this.code, absoluteIndex+2);
		EPLUtil.shortToBytes(param2, this.code, absoluteIndex+6);
	}
	
	public void writeDouble(int individual, int index, double value) {
		int absoluteIndex = (individual*this.maxIndividualSize)+(index*EPLHolder.FRAME_SIZE);
		EPLUtil.doubleToBytes(value, this.code, absoluteIndex);
	}
	
	public void readNodeHeader(int individual, int index, OpCodeHeader header) {
		int absoluteIndex = (individual*this.maxIndividualSize)+(index*EPLHolder.FRAME_SIZE);
		header.setOpcode(EPLUtil.bytesToShort(this.code, absoluteIndex));
		header.setParam1(EPLUtil.bytesToInt(this.code, absoluteIndex+2));
		header.setParam2(EPLUtil.bytesToShort(this.code, absoluteIndex+6));
	}

	public double readDouble(int individual, int index) {
		int absoluteIndex = (individual*this.maxIndividualSize)+(index*EPLHolder.FRAME_SIZE);
		return EPLUtil.bytesToDouble(this.code, absoluteIndex);
	}

	public String readString(int individual, int index, int encodedLength) {
		try {
			int absoluteIndex = (individual*this.maxIndividualSize)+(index*EPLHolder.FRAME_SIZE);
			return new String(this.code,absoluteIndex, encodedLength, Encog.DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new EncogError(e);
		}
	}

	public void writeByte(int individual, int index, byte[] b) {
		int absoluteIndex = (individual*this.maxIndividualSize)+(index*EPLHolder.FRAME_SIZE);
		EngineArray.arrayCopy(b, 0, this.code, absoluteIndex, b.length);
		
	}

	public void deleteSubtree(int individual, int index, int size) {
		int absoluteIndex = (individual*this.maxIndividualSize)+(index*EPLHolder.FRAME_SIZE);
		EngineArray.arrayCopy(
				this.code, 
				absoluteIndex+(size*EPLHolder.FRAME_SIZE), 
				this.code, 
				absoluteIndex, 
				size*EPLHolder.FRAME_SIZE);
		
	}
	
	

	/**
	 * @return the code
	 */
	public byte[] getCode() {
		return code;
	}

	@Override
	public void insert(int individual, int index, int size) {
		int absoluteIndex = (individual*this.maxIndividualSize)+(index*EPLHolder.FRAME_SIZE);
		int target = absoluteIndex+(size*EPLHolder.FRAME_SIZE);
		int remaining = this.maxIndividualSize - target;
		EngineArray.arrayCopy(
				this.code, 
				absoluteIndex, 
				this.code, 
				target, 
				remaining);
	}

	@Override
	public void copySubTree(EPLHolder targetProgram, int sourceIndex, int targetIndex, int size) {
		EngineArray.arrayCopy(
				this.code,
				sourceIndex*EPLHolder.FRAME_SIZE,
				((ByteArrayHolder)targetProgram).getCode(),
				targetIndex*EPLHolder.FRAME_SIZE,
				size*EPLHolder.FRAME_SIZE);
				
	}
}
