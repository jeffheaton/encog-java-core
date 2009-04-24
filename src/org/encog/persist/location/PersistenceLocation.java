package org.encog.persist.location;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A persistence location specifies how the persistence collection is stored. 
 * @author jheaton
 *
 */
public interface PersistenceLocation {
	
	/**
	 * @return A new input stream to read data from.
	 */
	InputStream createInputStream();
	
	/**
	 * @return A new output stream to write data to.
	 */
	OutputStream createOutputStream();
	
	/**
	 * @return True if this location exists.
	 */
	boolean exists();
	
	/**
	 * Delete the location, if possible.
	 */
	void delete();
	
	/**
	 * Attempt to rename this location. Mainly for file locations.
	 * @param toLocation The new name.
	 */
	void renameTo(PersistenceLocation toLocation);
}
