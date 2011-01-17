package org.encog.ml;

public interface MLEncodable extends MLMethod {
	int encodedArrayLength();
	void encodeToArray(double[] encoded);
	void decodeFromArray(double[] encoded);
}
