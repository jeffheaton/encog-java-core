
package org.encog.app.quant.indicators;

import java.util.Map;

import org.encog.app.csv.basic.BaseCachedColumn;
import org.encog.app.csv.basic.FileData;

/**
 * A simple moving average.
 */
public class MovingAverage extends Indicator {

	/**
	 * The name of this indicator.
	 */
    public static final String NAME = "MovAvg";

    /**
     * The number of periods in this indicator.
     */
    public int getPeriods() 
    {
            return this.periods;
    }

    /**
     * The number of periods in this indicator.
     */
    private int periods;

    /**
     * Construct this object.
     * @param periods The number of periods in this indicator.
     * @param output True, if this indicator is predicted.
     */
    public MovingAverage(int periods, boolean output)     
    {
    	super( NAME , false, output );
        this.periods = periods;
        this.setOutput(output);
    }

    /**
     * Calculate this indicator.
     * @param data The data to use.
     * @param length The length to calculate over.
     */
    public void calculate(Map<String, BaseCachedColumn> data, int length)
    {
        Require(data, FileData.CLOSE);
        
        double[] close = data.get(FileData.CLOSE).getData();
        double[] output = this.getData();

        int lookbackTotal = (periods - 1);
        
        int start = lookbackTotal;
        if (start > (periods-1))
        {
            return;
        }

        double periodTotal = 0;
        int trailingIdx = start - lookbackTotal;
        int i = trailingIdx;
        if (periods > 1)
        {
            while (i < start)
                periodTotal += close[i++];
        }

        int outIdx = periods-1;
        do
        {
            periodTotal += close[i++];
            double t = periodTotal;
            periodTotal -= close[trailingIdx++];
            output[outIdx++] = t / periods;
        } while (i < close.length);

        setBeginningIndex( periods - 1);
        setEndingIndex( output.length - 1);

        for ( i = 0; i < periods-1; i++)
        {
            output[i] = 0;
        }
    }	
}
