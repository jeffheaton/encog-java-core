package org.encog.ml.prg.epl;

public interface EPLHolder {
	public static final int FRAME_SIZE = 8;

	void copy(EPLHolder sourceHolder, int sourceIndividual, int sourceIndex,
			int targetIndividual, int targetIndex, int size);

	void deleteSubtree(int individual, int index, int size);

	int fromBase64(int individual, String str);

	int getMaxIndividualFrames();

	int getMaxIndividualSize();

	int getPopulationSize();

	void insert(int individual, int index, int size);

	double readDouble(int individual, int index);

	void readNodeHeader(int individual, int index, OpCodeHeader header);

	String readString(int individual, int index, int encodedLength);

	String toBase64(int individual, int programLength);

	void writeByte(int individual, int index, byte[] b);

	void writeDouble(int individual, int index, double value);

	void writeNode(int individual, int index, short opcode, int param1,
			short param2);
}
