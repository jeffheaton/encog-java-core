package org.encog.ml;

/**
 * Defines a MLMethod that produces output.  Input is defined as a simple 
 * array of double values.  Many machine learning methods, such as neural 
 * networks and support vector machines produce output this way, and thus 
 * implement this interface.  Others, such as clustering, do not.
 */
public interface MLOutput extends MLMethod {
	int getOutputCount();
}
