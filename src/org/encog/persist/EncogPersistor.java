package org.encog.persist;

import java.io.InputStream;
import java.io.OutputStream;

public interface EncogPersistor {
	
	String getPersistClassString();
	Object read(InputStream is);
	void save(OutputStream os, Object obj);
	int getFileVersion();
}
