package org.encog.neural.data.external;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.buffer.BufferedNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.persistors.generic.GenericPersistor;
import org.encog.util.simple.EncogUtility;

public class ExternalDataSource implements EncogPersistedObject, NeuralDataSet, Indexable {

	private String name;
	private String description;
	private String type = TYPE_FILE;
	private String link;
	private File tempBinary;
	private BufferedNeuralDataSet binary;
	
	@EGIgnore
	private int inputCount;
	
	@EGIgnore
	private int idealCount;
	
	public static final String TYPE_FILE = "FILE";
	
	@Override
	public Persistor createPersistor() {
		return new GenericPersistor(ExternalDataSource.class);
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	public void setLink(File file) {
		this.link = file.getAbsolutePath();		
	}

	@Override
	public void add(NeuralData data1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(NeuralData inputData, NeuralData idealData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(NeuralDataPair inputData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIdealSize() {
		return this.idealCount;
	}

	@Override
	public int getInputSize() {
		return this.inputCount;
	}

	public Iterator<NeuralDataPair> iterator() {
		return this.binary.iterator();
	}	
	
	public void init(BasicNetwork network)
	{
		try
		{
			this.tempBinary = File.createTempFile("encog", "tmp");
			EncogUtility.convertCSV2Binary(new File(this.link), this.tempBinary, network.getInputCount(), network.getOutputCount(), false);
			this.binary = new BufferedNeuralDataSet(this.tempBinary);
			this.inputCount = network.getInputCount();
			this.idealCount = network.getOutputCount();
		}
		catch(IOException ex)
		{
			throw new NeuralNetworkError(ex);
		}
	}
	
	public void dispose()
	{
		if( this.binary!=null )
		{
			this.binary.close();
			this.tempBinary.delete();
			this.tempBinary = null;
			this.binary = null;
		}
	}
	
	/**
	 * @return True if this training data is supervised.
	 */
	@Override
	public boolean isSupervised() {
		return this.idealCount>0;
	}

	@Override
	public void getRecord(long index, NeuralDataPair pair) {
		this.binary.getRecord(index, pair);		
	}

	@Override
	public long getRecordCount() {
		return this.binary.getRecordCount();
	}

	@Override
	public Indexable openAdditional() {
		return this.binary.openAdditional();
	}

}
