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
public class ObjectCloner {

	/**
	 * Private constructor.
	 */
	private ObjectCloner() {
	}

	/**
	 * Perform a deep copy.
	 * 
	 * @param oldObj
	 *            The old object.
	 * @return The new object.
	 */
	static public Object deepCopy(Object oldObj) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
			oos = new ObjectOutputStream(bos); // B
			// serialize and pass the object
			oos.writeObject(oldObj); // C
			oos.flush(); // D
			ByteArrayInputStream bin = new ByteArrayInputStream(bos
					.toByteArray()); // E
			ois = new ObjectInputStream(bin); // F
			// return the new object
			return ois.readObject(); // G
		} catch (Exception e) {
			throw new EncogError(e);
		} finally {
			try {
				oos.close();
				ois.close();
			} catch (Exception e) {
				throw new EncogError(e);
			}
		}
	}

}
