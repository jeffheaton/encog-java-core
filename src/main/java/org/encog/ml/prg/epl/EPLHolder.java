package org.encog.ml.prg.epl;


public interface EPLHolder {
	public static final int FRAME_SIZE = 8;
	
	void writeNode(int individual, int index, short opcode, int param1, short param2 );
	
	void writeDouble(int individual, int index, double value);
	
	void readNodeHeader(int individual, int index, OpCodeHeader header);

	double readDouble(int individual, int index);

	String readString(int individual, int index, int encodedLength);


	void writeByte(int individual, int index, byte[] b);

	void deleteSubtree(int individual, int index, int size);

	void insert(int individual, int index, int size);

	String toBase64(int individual, int programLength);

	int fromBase64(int individual, String str);
	
	void copy(EPLHolder sourceHolder, int sourceIndividual, int sourceIndex, int targetIndividual, int targetIndex, int size);
	
	int getMaxIndividualSize();
	
	int getPopulationSize();

	int getMaxIndividualFrames();
	
	short readHeaderOpcode(int individual, int index);
	int readHeaderParam1(int individual, int index);
	short readHeaderParam2(int individual, int index);
}
