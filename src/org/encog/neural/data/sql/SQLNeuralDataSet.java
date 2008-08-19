package org.encog.neural.data.sql;

import java.sql.SQLException;
import java.util.Iterator;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.util.db.RepeatableConnection;
import org.encog.util.db.RepeatableStatement;
import org.encog.util.db.RepeatableStatement.Results;

public class SQLNeuralDataSet implements NeuralDataSet {
	
	public final static String ADD_NOT_SUPPORTED = "Adds are not supported with this dataset, it is read only.";
	
	private String filename;
	private int inputSize;
	private int idealSize;
	private RepeatableConnection connection;
	private RepeatableStatement statement;

	
	public class SQLNeuralIterator implements Iterator<NeuralDataPair>
	{
		private boolean dataReady;
		private Results results;
		
		public SQLNeuralIterator()
		{
			this.results = statement.executeQuery();
		}
		
		
		public boolean hasNext() {
				
			if( dataReady )
				return true;
			
			try
			{
				if( results.getResultSet().next() )
				{
					dataReady = true;
					return true;
				}
				else 
				{
					dataReady = false;
					return false;
				}
			}
			catch(SQLException e)
			{
				throw new NeuralDataError(e);
			}
			
		}

		public NeuralDataPair next()  {
				try {		
			NeuralData input = new BasicNeuralData(inputSize);
			NeuralData ideal = null;
			
			for(int i=1;i<=inputSize;i++)
			{				
				input.setData(i-1, results.getResultSet().getDouble(i));
			}
			
			if( idealSize>0 )
			{
				ideal = new BasicNeuralData(idealSize);
				for(int i=1;i<=idealSize;i++)
				{
					ideal.setData(i-1, results.getResultSet().getDouble(i+inputSize));
				}
				 
			}
			
			this.dataReady = false;		
			return new BasicNeuralDataPair(input,ideal);
				}
				catch(SQLException e)
				{
				throw new NeuralDataError(e);
				}
		}

		public void remove() {
			results.close();			
		}	
	}
	
	public SQLNeuralDataSet(String sql, int inputSize,int idealSize, String driver, String url, String uid,String pwd)
	{
		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.connection = new RepeatableConnection(driver, url, uid, pwd);
		this.statement = this.connection.createStatement(sql);
		this.connection.open();
	}

	public void add(NeuralData inputData, NeuralData idealData) {
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
		
	}

	public void add(NeuralDataPair inputData) {
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	public int getIdealSize() {
		return this.idealSize;
	}

	public int getInputSize() {
		return this.inputSize;
	}

	public Iterator<NeuralDataPair> iterator() {
		return new SQLNeuralIterator();
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}


	public void add(NeuralData data1) {
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);		
	}
	
	public void close()
	{
		if( this.statement!=null )
			this.statement.close();
		
		if( this.connection!=null )
			this.connection.close();
	}

}
