package org.encog.ml;

/**
 * This is a convenience interface that combines MLInput and MLOutput.  
 * Together these define a MLMethod that both accepts input and 
 * produces output.
 * 
 * Input and output are defined as a simple array of double values.  
 * Many machine learning methods, such as neural networks and 
 * support vector machines handle input and output in this way, 
 * and thus implement this interface.  Others, such as clustering, 
 * do not.
 *
 */
public interface MLInputOutput extends MLInput, MLOutput {

}
