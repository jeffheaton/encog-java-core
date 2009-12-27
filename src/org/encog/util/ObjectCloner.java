/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.encog.util;

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
				oos.close();
				ois.close();
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
