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
	
	public ByteArrayHolder(int theMaxIndividualFrames) {
		this.maxIndividualFrames = theMaxIndividualFrames;
		this.maxIndividualSize = this.maxIndividualFrames*EPLHolder.FRAME_SIZE;
		code = new byte[maxIndividualSize];
	}
	
	public void writeNode(int index, short opcode, int param1, short param2 ) {
		int absoluteIndex = (index*EPLHolder.FRAME_SIZE);
		EPLUtil.shortToBytes(opcode, this.code, absoluteIndex);
		EPLUtil.intToBytes(param1, this.code, absoluteIndex+2);
		EPLUtil.shortToBytes(param2, this.code, absoluteIndex+6);
	}
	
	public void writeDouble(int index, double value) {
		int absoluteIndex = (index*EPLHolder.FRAME_SIZE);
		EPLUtil.doubleToBytes(value, this.code, absoluteIndex);
	}
	
	public void readNodeHeader(int index, OpCodeHeader header) {
		int absoluteIndex = (index*EPLHolder.FRAME_SIZE);
		header.setOpcode(EPLUtil.bytesToShort(this.code, absoluteIndex));
		header.setParam1(EPLUtil.bytesToInt(this.code, absoluteIndex+2));
		header.setParam2(EPLUtil.bytesToShort(this.code, absoluteIndex+6));
	}

	public double readDouble(int index) {
		int absoluteIndex = (index*EPLHolder.FRAME_SIZE);
		return EPLUtil.bytesToDouble(this.code, absoluteIndex);
	}

	public String readString(int index, int encodedLength) {
		try {
			int absoluteIndex = (index*EPLHolder.FRAME_SIZE);
			return new String(this.code,absoluteIndex, encodedLength, Encog.DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new EncogError(e);
		}
	}

	public void writeByte(int index, byte[] b) {
		int absoluteIndex = (index*EPLHolder.FRAME_SIZE);
		EngineArray.arrayCopy(b, 0, this.code, absoluteIndex, b.length);
		
	}

	public void deleteSubtree(int index, int size) {
		int targetIndex = (index*EPLHolder.FRAME_SIZE);
		int sourceIndex = targetIndex+(size*EPLHolder.FRAME_SIZE);
		int remaining = this.maxIndividualSize - sourceIndex;
		EngineArray.arrayCopy(
				this.code, 
				sourceIndex, 
				this.code, 
				targetIndex, 
				remaining);
		
	}
	
	

	/**
	 * @return the code
	 */
	public byte[] getCode() {
		return code;
	}

	@Override
	public void insert(int index, int size) {
		int sourceIndex = (index*EPLHolder.FRAME_SIZE);
		int targetIndex = sourceIndex+(size*EPLHolder.FRAME_SIZE);
		int remaining = this.maxIndividualSize - targetIndex;
		EngineArray.arrayCopy(
				this.code, 
				sourceIndex, 
				this.code, 
				targetIndex, 
				remaining);
	}

	@Override
	public String toBase64(int programLength) {
		return Base64.encodeBytes(this.code, 0, programLength*EPLHolder.FRAME_SIZE);
	}

	@Override
	public int fromBase64(String str) {
		try {
			byte[] b = Base64.decode(str);
			if( b.length>this.maxIndividualSize) {
				throw new EncogError("Can't decode program, it is too large.  Set the max individual size higher.");
			}
			EngineArray.arrayCopy(b, 0, this.code, 0, b.length);
			return b.length/EPLHolder.FRAME_SIZE;
		} catch (IOException e) {
			throw new EncogError(e);
		}
	}
	
	public void copy(EPLHolder sourceHolder,int sourceIndex, int targetIndex, int size) {
		int absoluteSourceIndex = (sourceIndex*EPLHolder.FRAME_SIZE);
		int absoluteTargetIndex = (targetIndex*EPLHolder.FRAME_SIZE);
		EngineArray.arrayCopy(
				((ByteArrayHolder)sourceHolder).getCode(), 
				absoluteSourceIndex, 
				this.code, 
				absoluteTargetIndex, 
				size*EPLHolder.FRAME_SIZE);
	}
}
