package org.encog.persist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EncogWriter {
	
	public EncogWriter(InputStream is) {
		init(is);
	}
	
	public EncogWriter(String name) {
		this(new File(name));
	}
	
	public EncogWriter(File file) {
		try {
			init(new FileInputStream(file));
		} catch(IOException ex) {
			throw new PersistError(ex);
		}
	}
	
	private void init(InputStream is) {
		
	}
	
	

}
