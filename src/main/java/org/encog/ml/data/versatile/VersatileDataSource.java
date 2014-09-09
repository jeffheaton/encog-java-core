package org.encog.ml.data.versatile;

public interface VersatileDataSource {
	String[] readLine();
	void rewind();
}
