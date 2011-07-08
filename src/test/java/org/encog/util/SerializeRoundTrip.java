package org.encog.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeRoundTrip {
	public static Object roundTrip(Object obj) throws IOException, ClassNotFoundException {
		
		// first serialize to memory
		ByteArrayOutputStream memory = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(memory);
		out.writeObject(obj);
		out.close();
		memory.close();
		
		// now reload
		ByteArrayInputStream memory2 = new ByteArrayInputStream(memory.toByteArray());
		ObjectInputStream in = new ObjectInputStream(memory2);
		Object result = in.readObject();
		in.close();
		memory2.close();
		
		// return the result;
		return result;
	}
}
