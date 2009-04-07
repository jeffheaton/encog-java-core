/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.encog.neural.persist.persistors;

import java.util.StringTokenizer;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.PersistError;
import org.encog.neural.persist.Persistor;
import org.encog.util.ReadCSV;
import org.encog.util.xml.XMLElement;
import org.encog.util.xml.XMLRead;
import org.encog.util.xml.XMLWrite;
import org.encog.util.xml.XMLElement.XMLElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class BasicNeuralDataSetPersistor implements Persistor {

	public enum State
	{
		OnTraining,
		OnItem,
		OnInput,
		OnIdeal,
		Done
	}
	
	public final static String TAG_TRAINING = "TrainingData";
	public final static String TAG_ITEM = "Item";
	public final static String TAG_INPUT = "Input";
	public final static String TAG_IDEAL = "Ideal";
	public final static String ATTRIBUTE_NAME = "name";
	public final static String ATTRIBUTE_DESCRIPTION = "description";
	
	private State state;
	private NeuralData currentInput;
	private NeuralData currentIdeal;
	private BasicNeuralDataSet currentDataSet;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	private void handleStartTag(final XMLElement node)
	{
		if( node.getText().equals(TAG_ITEM) )
		{
			handleStartItem();
		}
		else if( node.getText().equals(TAG_INPUT))
		{
			handleStartInput();
		}
		else if( node.getText().equals(TAG_IDEAL))
		{
			handleStartIdeal();
		}
		else
			EncogPersistedCollection.throwError(node.getText());
	}
	
	private void handleStartItem()
	{
		if( this.state!=State.OnTraining)
			EncogPersistedCollection.throwError(TAG_ITEM);
		this.state = State.OnItem;
	}
	
	private void handleStartInput()
	{
		if( this.state!=State.OnItem)
			EncogPersistedCollection.throwError(TAG_INPUT);
		this.state = State.OnInput;
	}
	
	private void handleStartIdeal()
	{
		if( this.state!=State.OnItem)
			EncogPersistedCollection.throwError(TAG_IDEAL);
		this.state = State.OnIdeal;
	}
	
	private void handleEndTag(final XMLElement node)
	{
		if( node.getText().equals(TAG_TRAINING) )
		{
			handleEndTraining();
		}
		else if( node.getText().equals(TAG_ITEM) )
		{
			handleEndItem();
		}
		else if( node.getText().equals(TAG_INPUT))
		{
			handleEndInput();
		}
		else if( node.getText().equals(TAG_IDEAL))
		{
			handleEndIdeal();
		}
		else
			EncogPersistedCollection.throwError(node.getText());
	}
	
	private void handleEndTraining()
	{
		if( state==State.OnTraining)
		{
			this.state = State.Done;
		}
		else
			EncogPersistedCollection.throwError(TAG_TRAINING);
	}
	
	private void handleEndItem()
	{
		if( this.state==State.OnItem)
		{
			if( this.currentInput !=null )
			{
				NeuralDataPair pair;
				
				
				if( this.currentIdeal==null)
				{
					// unsupervised
					pair = new BasicNeuralDataPair(this.currentInput);
				}
				else
				{
					// supervised
					pair = new BasicNeuralDataPair(this.currentInput,this.currentIdeal);
				}
				this.currentDataSet.add(pair);	
			}
			this.state = State.OnTraining;
			
			// ready to read the next one
			this.currentInput = null;
			this.currentInput = null;
		}
		else
			EncogPersistedCollection.throwError(TAG_ITEM);
	}
	
	public void handleEndInput()
	{
		this.state = State.OnItem;
	}
	
	public void handleEndIdeal()
	{
		this.state = State.OnItem;
	}
	
	private void handleText(final XMLElement node)
	{
		double[] temp;
		
		switch(state)
		{
			case OnInput:
				temp = ReadCSV.fromCommas(node.getText());
				this.currentInput = new BasicNeuralData(temp); 
				break;
			case OnIdeal:
				temp = ReadCSV.fromCommas(node.getText());
				this.currentIdeal = new BasicNeuralData(temp);
				break;			
		}
	}
	
	public EncogPersistedObject load(XMLElement node,XMLRead in) {
				
		String name = node.getAttributes().get(ATTRIBUTE_NAME);
		String description = node.getAttributes().get(ATTRIBUTE_DESCRIPTION);
			
		this.currentDataSet = new BasicNeuralDataSet();
		currentDataSet.setName(name);
		currentDataSet.setDescription(description);
		
		this.state = State.OnTraining;
				
		XMLElement current;
		
		while( ((current = in.get())!=null) && (this.state!=State.Done) )
		{
			switch(current.getType())
			{
				case start:
					handleStartTag(current);
					break;
				case end:
					handleEndTag(current);
					break;
				case text:
					handleText(current);
					break;
			}
		}
		
		return this.currentDataSet;
	}
		
	public void save(EncogPersistedObject obj, XMLWrite out) {
		PersistorUtil.beginEncogObject(TAG_TRAINING, out, obj, true);
		NeuralDataSet set = (NeuralDataSet)obj;
		StringBuilder builder = new StringBuilder();
		
		for(NeuralDataPair pair: set)
		{
			out.beginTag(TAG_ITEM);
			
			ReadCSV.toCommas(builder,pair.getInput().getData());
			out.addProperty(TAG_INPUT, builder.toString());
			
			if( pair.getIdeal()!=null )
			{
				ReadCSV.toCommas(builder,pair.getIdeal().getData());
				out.addProperty(TAG_IDEAL, builder.toString());
			}
			out.endTag();
		}
		out.endTag();
		
	}

}
