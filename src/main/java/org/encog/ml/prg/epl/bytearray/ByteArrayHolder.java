package org.encog.ml.prg.epl.bytearray;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.EPLUtil;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.util.EngineArray;
import org.encog.util.text.Base64;

public class ByteArrayHolder implements EPLHolder, Serializable {
	private byte[] code;
	private int maxIndividualFrames;
	private int maxIndividualSize;
	
	public int getMaxIndividualSize() {
		return this.maxIndividualSize;
	}
	
	public int getMaxIndividualFrames() {
		return this.maxIndividualFrames;
	}
	
	public int getPopulationSize() {
		return this.code.length/this.maxIndividualSize;
	}
	
	public ByteArrayHolder(int thePopulationSize, int theMaxIndividualFrames) {
		this.maxIndividualFrames = theMaxIndividualFrames;
		this.maxIndividualSize = this.maxIndividualFrames*EPLHolder.FRAME_SIZE;
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
		int individualIndex = (individual*this.maxIndividualSize); 
		int targetIndex = (index*EPLHolder.FRAME_SIZE);
		int sourceIndex = targetIndex+(size*EPLHolder.FRAME_SIZE);
		int remaining = this.maxIndividualSize - sourceIndex;
		EngineArray.arrayCopy(
				this.code, 
				individualIndex+sourceIndex, 
				this.code, 
				individualIndex+targetIndex, 
				remaining);
		
	}
	
	

	/**
	 * @return the code
	 */
	public byte[] getCode() {
		return code;
	}

	@Override
	public void insert(int individual, int index, int size) {
		int individualIndex = (individual*this.maxIndividualSize);
		int sourceIndex = (index*EPLHolder.FRAME_SIZE);
		int targetIndex = sourceIndex+(size*EPLHolder.FRAME_SIZE);
		int remaining = this.maxIndividualSize - targetIndex;
		EngineArray.arrayCopy(
				this.code, 
				individualIndex+sourceIndex, 
				this.code, 
				individualIndex+targetIndex, 
				remaining);
	}

	@Override
	public String toBase64(int individual, int programLength) {
		int absoluteIndex = (individual*this.maxIndividualSize);
		return Base64.encodeBytes(this.code, absoluteIndex, programLength*EPLHolder.FRAME_SIZE);
	}

	@Override
	public int fromBase64(int individual, String str) {
		try {
			int absoluteIndex = (individual*this.maxIndividualSize);
			byte[] b = Base64.decode(str);
			if( b.length>this.maxIndividualSize) {
				throw new EncogError("Can't decode program, it is too large.  Set the max individual size higher.");
			}
			EngineArray.arrayCopy(b, 0, this.code, absoluteIndex, b.length);
			return b.length/EPLHolder.FRAME_SIZE;
		} catch (IOException e) {
			throw new EncogError(e);
		}
	}
	
	public void copy(EPLHolder sourceHolder, int sourceIndividual, int sourceIndex, int targetIndividual, int targetIndex, int size) {
		int absoluteSourceIndex = (sourceIndividual*sourceHolder.getMaxIndividualSize())+(sourceIndex*EPLHolder.FRAME_SIZE);
		int absoluteTargetIndex = (targetIndividual*this.maxIndividualSize)+(targetIndex*EPLHolder.FRAME_SIZE);
		EngineArray.arrayCopy(
				((ByteArrayHolder)sourceHolder).getCode(), 
				absoluteSourceIndex, 
				this.code, 
				absoluteTargetIndex, 
				size*EPLHolder.FRAME_SIZE);
	}
}
