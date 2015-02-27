package org.encog.ml.data.versatile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.normalizers.strategies.BasicNormalizationStrategy;
import org.encog.util.SerializeRoundTrip;
import org.junit.Assert;
import org.junit.Test;

public class TestNormalizationHelper { 
	@Test
	public void testDefaultSerial() throws ClassNotFoundException, IOException {
		NormalizationHelper out = new NormalizationHelper();
		NormalizationHelper in = (NormalizationHelper) SerializeRoundTrip.roundTrip( out );
		
		Assert.assertNotNull( in );
		smokeTestEqual( out, in );
	}
	
	@Test
	public void testColumnsSerial() throws ClassNotFoundException, IOException {
		NormalizationHelper out = new NormalizationHelper();
		
		List<ColumnDefinition> inputColumns = new ArrayList<ColumnDefinition>();
		List<ColumnDefinition> outputColumns = new ArrayList<ColumnDefinition>();
		List<ColumnDefinition> sourceColumns = new ArrayList<ColumnDefinition>();
		
		inputColumns.add( new ColumnDefinition( "a", ColumnType.continuous ));
		inputColumns.add( new ColumnDefinition( "b", ColumnType.continuous ));
		outputColumns.add( new ColumnDefinition("out", ColumnType.continuous ));
		sourceColumns.addAll( inputColumns );
		sourceColumns.addAll( outputColumns );

		out.setSourceColumns( sourceColumns );
		out.setInputColumns( inputColumns );
		out.setOutputColumns( outputColumns );
		
		NormalizationHelper in = (NormalizationHelper) SerializeRoundTrip.roundTrip( out );
		
		Assert.assertNotNull( in );
		smokeTestEqual( out, in );
	}

	@Test
	public void testNormStrategySerial() throws ClassNotFoundException, IOException {
		NormalizationHelper out = new NormalizationHelper();
		out.setNormStrategy( new BasicNormalizationStrategy( -1, 1, -1, 1 ));
		
		NormalizationHelper in = (NormalizationHelper) SerializeRoundTrip.roundTrip( out );
		
		Assert.assertNotNull( in );
		smokeTestEqual( out, in );
	}

	/** Smoke-tests that some of the properties of the two objects are the same.
	 *  A complete check would require implementing equals() or adding accessors 
	 *  in the tested code.
	 * @param a  Null or a {@link NormalizationHelper}.
	 * @param b  Null or a {@link NormalizationHelper}.
	 */
	private void smokeTestEqual( 
			NormalizationHelper a, 
			NormalizationHelper b ) 
	{
		if ( a != b ) {
			// Incomplete test of equality.
			Assert.assertFalse( (a == null) || (b == null) ); // Not equal if exactly one is null.
			Assert.assertEquals( a.toString(), b.toString() );
			Assert.assertEquals( a.getSourceColumns(),  b.getSourceColumns());
			Assert.assertEquals( a.getInputColumns(),  b.getInputColumns());
			Assert.assertEquals( a.getOutputColumns(), b.getOutputColumns());
			Assert.assertEquals( a.getNormStrategy(),  b.getNormStrategy());
			
			// Additional checks may be added here.
		}
		// else they're equal.
	}
}
