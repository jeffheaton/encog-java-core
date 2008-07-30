package org.encog.neural.networks.feedforward;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkException;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class FeedforwardNetworkPersistor implements Persistor {

	public void save(EncogPersistedObject object,TransformerHandler hd) throws NeuralNetworkException 
	{
		try
		{
			FeedforwardNetwork network = (FeedforwardNetwork)object;
			AttributesImpl atts = new AttributesImpl();
			hd.startElement("","","layers",atts);
			for(FeedforwardLayer layer: network.getLayers())
			{
				saveLayer(hd,layer);
			}
			hd.endElement("","","layers");
		}
		catch(SAXException e)
		{
			throw new NeuralNetworkException(e);
		}
		
	}
	
	private void saveLayer(TransformerHandler hd,FeedforwardLayer layer) throws SAXException
	{
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("","","neuronCount","CDATA",""+layer.getNeuronCount());
		hd.startElement("","","Layer",atts);
		
		atts.clear();
		atts.addAttribute("","","native","CDATA",layer.getActivationFunction().getClass().getName());
		atts.addAttribute("","","name","CDATA",layer.getActivationFunction().getName());
		hd.startElement("","","activation",atts);		
		hd.endElement("","","activation");
		
		if( layer.hasMatrix() )
		{
			atts.clear();
			hd.startElement("","","weightMatrix",atts);
			saveMatrix(hd,layer.getMatrix());
			hd.endElement("","","weightMatrix");
		}
		
		hd.endElement("","","Layer");
	}

	private void saveMatrix(TransformerHandler hd, Matrix matrix) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("","","rows","CDATA",""+matrix.getRows());
		atts.addAttribute("","","cols","CDATA",""+matrix.getCols());
		hd.startElement("","","Matrix",atts);
		for(int row=0;row<matrix.getRows();row++)
		{
			atts.clear();
			for(int col=0;col<matrix.getCols();col++)
			{
				atts.addAttribute("","","col"+col,"CDATA",""+matrix.get(row, col));				
			}
			
			hd.startElement("","","row",atts);
			hd.endElement("","","row");
		}
		hd.endElement("","","Matrix");
	}

}
