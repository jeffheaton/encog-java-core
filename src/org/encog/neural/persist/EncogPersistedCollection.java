package org.encog.neural.persist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.encog.neural.NeuralNetworkException;
import org.encog.neural.data.xml.SAXHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class EncogPersistedCollection {
	private List<EncogPersistedObject> list = new ArrayList<EncogPersistedObject>();
	
	
	public void add(EncogPersistedObject obj)
	{
		this.list.add(obj);
	}
		
	public void save(String filename) throws NeuralNetworkException 
	{
		try
		{
		OutputStream os = new FileOutputStream(filename);
		save(os);
		os.close();
		}
		catch(IOException e)
		{
			throw new NeuralNetworkException(e);
		}
	}
	
	public void save(OutputStream os) throws NeuralNetworkException 
	{
		try
		{
		 StreamResult streamResult = new StreamResult(os);
		 SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		 // SAX2.0 ContentHandler.
		 TransformerHandler hd = tf.newTransformerHandler();
		 Transformer serializer = hd.getTransformer();
		 serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
		 //serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"enocg.dtd");
		 serializer.setOutputProperty(OutputKeys.INDENT,"yes");
		 hd.setResult(streamResult);
		 hd.startDocument();
		 AttributesImpl atts = new AttributesImpl();
		 // USERS tag.
		 hd.startElement("","","Document",atts);

		 saveHeader(hd);
		 saveObjects(hd);
		 
		 hd.endElement("","","Document");
		 hd.endDocument();
		}
		catch(TransformerConfigurationException e)
		{
			throw new NeuralNetworkException(e);
		}
		catch(SAXException e)
		{
			throw new NeuralNetworkException(e);
		}
		catch(ClassNotFoundException e)
		{
			throw new NeuralNetworkException(e);
		}
		catch(InstantiationException e)
		{
			throw new NeuralNetworkException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new NeuralNetworkException(e);
		}		
	}
	
	private void saveHeader(TransformerHandler hd) throws SAXException {
		String data;
		AttributesImpl atts = new AttributesImpl();
		 hd.startElement("","","Header",atts);
		 
		 // platform
		hd.startElement("","","platform",atts);
		data = "Java";
		hd.characters(data.toCharArray(),0,data.length());
		hd.endElement("","","platform");
		
		 // platform
		hd.startElement("","","fileVersion",atts);
		data = "0";
		hd.characters(data.toCharArray(),0,data.length());
		hd.endElement("","","fileVersion");
		
		
		// platform
		hd.startElement("","","encogVersion",atts);
		data = "1.0.0";
		hd.characters(data.toCharArray(),0,data.length());
		hd.endElement("","","encogVersion");
		
			
		 
		 hd.endElement("","","Header");
	}

	private void saveObjects(TransformerHandler hd) throws SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, NeuralNetworkException
	{
		AttributesImpl atts = new AttributesImpl();
		 hd.startElement("","","objects",atts);
		 
		 for(EncogPersistedObject obj: this.list)
		 {
			 atts.clear(); 
			 atts.addAttribute("","","native","CDATA",obj.getClass().getName());
			 atts.addAttribute("","","name","CDATA",obj.getName());
			 hd.startElement("","","EncogObject",atts);
			 
			 String name = obj.getClass().getName()+"Persistor";
			 Class c = Class.forName(name);
			 Persistor persistor = (Persistor)c.newInstance();
			 persistor.save(obj,hd);
			 
			 hd.endElement("","","EncogObject");
		 }
		 
		 hd.endElement("","","objects");
	}
	
	public void clear()
	{
		list.clear();
	}
}
