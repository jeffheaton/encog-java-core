package org.encog.util.file;

import java.io.InputStream;

import org.encog.EncogError;

public class ResourceInputStream  {

	/**
	 * Construct a location to read from the specified resource.
	 * 
	 * An example of the format for a file stored this way is:
	 * 
	 * org/encog/data/classes.txt
	 * 
	 * @param resource
	 *            The resource to read from.
	 */
	public static InputStream openResourceInputStream(final String resource) {
		
		final ClassLoader loader = ResourceInputStream.class.getClassLoader();
		InputStream result = loader.getResourceAsStream(resource);

		if (result == null) {
			throw new EncogError("Can't open resource: " + resource);
		}		
		
		return result;
	}

}
