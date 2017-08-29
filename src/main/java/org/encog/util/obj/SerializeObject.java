/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
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

import org.encog.EncogError;

import java.io.*;

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

	public static Serializable serializeClone(Serializable source) {
		try {
			ByteArrayOutputStream store = new ByteArrayOutputStream();
			ObjectOutputStream serializeOut = new ObjectOutputStream(store);
			serializeOut.writeObject(source);
			serializeOut.close();

			ByteArrayInputStream readStore = new ByteArrayInputStream(store.toByteArray());
			ObjectInputStream serializeIn = new ObjectInputStream(readStore);
			Serializable result = (Serializable) serializeIn.readObject();
			serializeIn.close();
			return result;
		} catch (IOException ex) {
			throw new EncogError(ex);
		} catch (ClassNotFoundException ex) {
			throw new EncogError(ex);
		}
	}

	/**
	 * Private constructor.
	 */
	private SerializeObject() {
	}

}
