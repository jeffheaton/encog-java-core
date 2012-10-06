package org.encog.ml;

/**
 * This interface defines the fact that a class, or object, is having the
 * ability to generate an Encog factory code from the objects instanciated
 * state.
 * 
 */
public interface MLFactory {
	/**
	 * @return The Encog factory type code.
	 */
	String getFactoryType();
	
	/**
	 * @return The Encog architecture code.
	 */
	String getFactoryArchitecture();
}
