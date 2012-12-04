package org.encog.ml.prg.epl.buffered;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.OpCodeHeader;

public class BufferedHolder implements EPLHolder {
	private ByteBuffer code;
	private int maxFrameSize;
	
	public BufferedHolder(int thePopulationSize, int theMaxFrameSize) {
		this.maxFrameSize = theMaxFrameSize;
		code = ByteBuffer.allocateDirect(thePopulationSize*maxFrameSize);
	}
	
	public void writeNode(int individual, int index, short opcode, int param1, short param2 ) {
		int absoluteIndex = (individual*this.maxFrameSize)+(index*EPLHolder.FRAME_SIZE);
		this.code.putShort(absoluteIndex,opcode);
		this.code.putInt(absoluteIndex+2,param1);
		this.code.putShort(absoluteIndex+6,param2);
	}
	
	public void writeDouble(int individual, int index, double value) {
		int absoluteIndex = (individual*this.maxFrameSize)+(index*EPLHolder.FRAME_SIZE);
		this.code.putDouble(absoluteIndex,value);
	}
	
	public void readNodeHeader(int individual, int index, OpCodeHeader header) {
		int absoluteIndex = (individual*this.maxFrameSize)+(index*EPLHolder.FRAME_SIZE);
		header.setOpcode(this.code.getShort(absoluteIndex));
		header.setParam1(this.code.getInt(absoluteIndex+2));
		header.setParam2(this.code.getShort(absoluteIndex+6));
	}

	public double readDouble(int individual, int index) {
		int absoluteIndex = (individual*this.maxFrameSize)+(index*EPLHolder.FRAME_SIZE);
		return this.code.getDouble(absoluteIndex);
	}

	public String readString(int individual, int index, int encodedLength) {
		try {
			int absoluteIndex = (individual*this.maxFrameSize)+(index*EPLHolder.FRAME_SIZE);
			byte[] b = new byte[encodedLength];
			for(int i=0;i<b.length;i++) {
				b[i] = this.code.get(absoluteIndex+i);
			}
			return new String(b,Encog.DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new EncogError(e);
		}
	}

	public void writeByte(int individual, int index, byte[] b) {
		int absoluteIndex = (individual*this.maxFrameSize)+(index*EPLHolder.FRAME_SIZE);
		for(int i=0;i<b.length;i++) {
			this.code.put(absoluteIndex+i,b[i]);
		}
		
	}

	public void deleteSubtree(int individual, int index, int size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(int individual, int index, int size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copySubTree(EPLHolder targetProgram, int sourceIndex, int targetIndex, int size) {
		
		
	}

	@Override
	public String toBase64(int individual, int programLength) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int fromBase64(int individual, String str) {
		// TODO Auto-generated method stub
		return 0;
	}
}
