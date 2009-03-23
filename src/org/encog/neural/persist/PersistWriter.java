package org.encog.neural.persist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.encog.Encog;
import org.encog.neural.persist.persistors.PersistorUtil;
import org.encog.util.xml.XMLWrite;

public class PersistWriter {
	
	private XMLWrite out;
	private OutputStream fileOutput;
	
	public PersistWriter(File filename)
	{
		try {
			this.fileOutput = new FileOutputStream(filename);
			this.out = new XMLWrite(this.fileOutput);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void writeHeader()
	{
		this.out.beginTag("Header");
		this.out.addProperty("platform", "Java");
		this.out.addProperty("fileVersion", Encog.ENCOG_FILE_VERSION);
		this.out.addProperty("encogVersion", Encog.ENCOG_VERSION);		
		this.out.endTag();
	}
	
	public void begin()
	{
		this.out.beginDocument();
		this.out.beginTag("Document");
	}
	
	public void end()
	{
		this.out.endTag();
		this.out.endDocument();
	}
	
	public void beginObjects()
	{
		this.out.beginTag("Objects");
	}
	
	public void endObjects()
	{
		this.out.endTag();
	}

	public void close() {
		
		try {
			this.out.close();
			this.fileOutput.close();
		} catch (IOException e) {
			throw new PersistError(e);
		}
		
	}

	public void writeObject(EncogPersistedObject obj) {
		Persistor persistor = obj.createPersistor();
		persistor.save(obj, this.out);
	}
	
}
