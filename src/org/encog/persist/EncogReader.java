package org.encog.persist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EncogReader {
	
	public EncogReader(OutputStream os) {
		init(os);
	}
	
	public EncogReader(String name) {
		this(new File(name));
	}
	
	public EncogReader(File file) {
		try {
			init(new FileOutputStream(file));
		} catch(IOException ex) {
			throw new PersistError(ex);
		}
	}
	
	private void init(OutputStream is) {
		
	}
	
	

}
