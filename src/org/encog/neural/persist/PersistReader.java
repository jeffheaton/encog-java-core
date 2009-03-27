package org.encog.neural.persist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.encog.neural.persist.persistors.PersistorUtil;
import org.encog.util.xml.XMLElement;
import org.encog.util.xml.XMLRead;
import org.encog.util.xml.XMLWrite;
import org.encog.util.xml.XMLElement.XMLElementType;

public class PersistReader {

	private XMLRead in;
	private InputStream fileInput;

	public PersistReader(String filename) {
		this(new File(filename));
	}

	public PersistReader(File filename) {
		try {
			this.fileInput = new FileInputStream(filename);
			this.in = new XMLRead(this.fileInput);
		} catch (FileNotFoundException e) {
			throw new PersistError(e);
		}
	}

	public XMLElement advance(String name) {
		XMLElement element;

		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			if (type == XMLElementType.start
					&& element.getText().equals("Objects")) {
				return advanceObjects(name);
			}
		}

		return null;
	}

	private XMLElement advanceObjects(String name) {
		XMLElement element;

		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			if (type == XMLElementType.start) {
				String elementName = element.getAttributes().get("name");

				if (elementName != null && elementName.equals(name))
					return element;
				else
					this.in.skipObject(element);
			}
		}

		return null;
	}

	public EncogPersistedObject readObject(String name) {
		XMLElement element = advance(name);
		// did we find the object?
		if (element != null) {
			String objectType = element.getText();
			Persistor persistor = PersistorUtil.createPersistor(objectType);
			if (persistor == null) {
				throw new PersistError("Do now know how to load: " + objectType);
			}
			return persistor.load(element, this.in);
		} else
			return null;
	}

	public XMLElement readNextTag(String name) {
		XMLElement element;

		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			if (type == XMLElementType.start) {
				if( element.getText().equals(name))
				{
					return element;
				}
				else
					this.in.skipObject(element);
			}
		}
		return null;
	}
	
	public String readNextText(XMLElement start)
	{
		StringBuilder result = new StringBuilder();
		XMLElement element;

		while ((element = this.in.get()) != null) {
			XMLElementType type = element.getType();
			if (type == XMLElementType.end) {
				if( element.getText().equals(start.getText()))
				{
					break;
				}
			}
			else if( type==XMLElementType.text)
			{
				result.append(element.getText());
			}
		}
		return result.toString();
	}

	public String readValue(String name) {
		XMLElement element = null;
		StringTokenizer tok = new StringTokenizer(name, ".");
		while(tok.hasMoreTokens())
		{
			String subName = tok.nextToken();
			element = readNextTag(subName);
			if( element==null )
				return null;
		}
		
		if( element==null )
			return null;
		else
			return readNextText(element);
	}

}
