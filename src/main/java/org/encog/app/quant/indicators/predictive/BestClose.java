package org.encog.app.quant.indicators.predictive;

import java.util.Map;

import org.encog.app.csv.basic.BaseCachedColumn;
import org.encog.app.csv.basic.FileData;
import org.encog.app.quant.indicators.Indicator;

/**
 * Get the best close.
 */
public class BestClose extends Indicator {
	
	/**
	 * The name of this indicator.
	 */
    public static final String NAME = "PredictBestClose";

    /**
     * The number of periods this indicator is for.
     */
    private int periods;

    /**
     * The number of periods.
     */
    public int getPeriods()
    {
       return periods;       
    }

    /**
     * Construct the object.
     * @param periods The number of periods.
     * @param output True, if this indicator is to be predicted.
     */
    public BestClose(int periods, boolean output)        
    {
    	super(NAME,false,output);
        this.periods = periods;
        setOutput(output);
    }

    /**
     * Calculate the indicator.
     * @param data The data available to the indicator.
     * @param length The length available to the indicator.
     */
    public void calculate(Map<String, BaseCachedColumn> data, int length)
    {
        double[] close = data.get(FileData.CLOSE).getData();
        double[] output = this.getData();

        int stop = length - periods;
        for (int i = 0; i < stop; i++)
        {
            double bestClose = Double.MIN_VALUE;
            for (int j = 1; j <= periods; j++)
            {
                bestClose = Math.max(close[i + j], bestClose);
            }
            output[i] = bestClose;
        }

        for (int i = length - periods; i < length; i++)
        {
            output[i] = 0;
        }

        this.setBeginningIndex( 0 );
        this.setEndingIndex( length - periods-1 );
    }
}
