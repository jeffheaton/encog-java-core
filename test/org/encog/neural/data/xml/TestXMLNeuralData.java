package org.encog.neural.data.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.encog.neural.networks.XOR;

import junit.framework.TestCase;

public class TestXMLNeuralData extends TestCase {
	public static final String FILENAME = "test.xml";
	
	private void generateXML() throws FileNotFoundException
	{
		PrintStream ps = new PrintStream(new FileOutputStream(TestXMLNeuralData.FILENAME));
		ps.println("<DataSet>");
		ps.println("<pair><input><value>0</value><value>0</value></input><ideal><value>0</value></ideal></pair>");
		ps.println("<pair><input><value>1</value><value>0</value></input><ideal><value>1</value></ideal></pair>");
		ps.println("<pair><input><value>0</value><value>1</value></input><ideal><value>1</value></ideal></pair>");
		ps.println("<pair><input><value>1</value><value>1</value></input><ideal><value>0</value></ideal></pair>");
		ps.println("</DataSet>");
		ps.close();
	}
	
	public void testXMLNeuralData() throws Exception
	{
		generateXML();
		XMLNeuralDataSet set = new XMLNeuralDataSet(
				TestXMLNeuralData.FILENAME, 
				"pair", 
				"input", 
				"ideal", 
				"value");
		
		TestCase.assertTrue(set.getInputSize()==2);
		TestCase.assertTrue(set.getIdealSize()==1);
		XOR.testXORDataSet(set);
		
		new File(TestXMLNeuralData.FILENAME).delete();
	}
}
