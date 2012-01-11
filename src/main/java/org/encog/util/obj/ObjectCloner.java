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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.encog.EncogError;

/**
 * A simple Object cloner that uses serialization. Actually works really well
 * for the somewhat complex nature of BasicNetwork. Performs a deep copy without
 * all the headache of programming a custom clone.
 * 
 * Original by Dave Miller here:
 * http://www.javaworld.com/javaworld/javatips/jw-javatip76.html?page=2
 */
public final class ObjectCloner {

	/**
	 * Perform a deep copy.
	 * 
	 * @param oldObj
	 *            The old object.
	 * @return The new object.
	 */
	public static Object deepCopy(final Object oldObj) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
			oos = new ObjectOutputStream(bos); // B
			// serialize and pass the object
			oos.writeObject(oldObj); // C
			oos.flush(); // D
			final ByteArrayInputStream bin = new ByteArrayInputStream(bos
					.toByteArray()); // E
			ois = new ObjectInputStream(bin); // F
			// return the new object
			return ois.readObject(); // G
		} catch (final Exception e) {
			throw new EncogError(e);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (final Exception e) {
				throw new EncogError(e);
			}
		}
	}

	/**
	 * Private constructor.
	 */
	private ObjectCloner() {
	}

}
