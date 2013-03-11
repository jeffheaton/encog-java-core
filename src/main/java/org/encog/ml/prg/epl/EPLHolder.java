package org.encog.ml.prg.epl;

public interface EPLHolder {
	public static final int FRAME_SIZE = 8;

	void copy(EPLHolder sourceHolder, int sourceIndex,
			int targetIndex, int size);

	void deleteSubtree(int index, int size);

	int fromBase64(String str);

	int getMaxIndividualFrames();

	int getMaxIndividualSize();

	int getPopulationSize();

	void insert(int index, int size);

	boolean isInvalid();

	double readDouble(int index);

	void readNodeHeader(int index, OpCodeHeader header);

	String readString(int index, int encodedLength);

	String toBase64(int programLength);

	void writeByte(int index, byte[] b);

	void writeDouble(int index, double value);
	
	void writeNode(int index, short opcode, int param1,
			short param2);
}
