/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.obj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Load or save an object using Java serialization.
 */
public final class SerializeObject {

	/**
	 * Load an object.
	 * 
	 * @param filename
	 *            The filename.
	 * @return The loaded object.
	 * @throws IOException
	 *             An IO error occurred.
	 * @throws ClassNotFoundException
	 *             The specified class can't be found.
	 */
	public static Serializable load(final File filename) throws IOException,
			ClassNotFoundException {
		Serializable object;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		fis = new FileInputStream(filename);
		in = new ObjectInputStream(fis);
		object = (Serializable) in.readObject();
		in.close();
		return object;
	}

	/**
	 * Save the specified object.
	 * 
	 * @param filename
	 *            The filename to save.
	 * @param object
	 *            The object to save.
	 * @throws IOException
	 *             An IO error occurred.
	 */
	public static void save(final File filename, final Serializable object)
			throws IOException {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(object);
		out.close();
	}

	/**
	 * Private constructor.
	 */
	private SerializeObject() {
	}

}
