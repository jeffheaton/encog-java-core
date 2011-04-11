package org.encog.app.csv.sort;

import java.util.Comparator;

import org.encog.app.csv.basic.LoadedRow;

public class RowComparator implements Comparator<LoadedRow> {

    /// <summary>
    /// The owner object.
    /// </summary>
    private SortCSV sort;

    /// <summary>
    /// Construct the object.
    /// </summary>
    /// <param name="sort">The owner file.</param>
    public RowComparator(SortCSV sort)
    {
        this.sort = sort;
    }

	/**
	 * Compare two LoadedRow objects.
	 * @param x The first object to compare.
	 * @param y The second object to compare.
	 * @return 0 if the same, <0 x is less, >0 y is less.
	 */
	@Override
	public int compare(LoadedRow x, LoadedRow y) {
		for(SortedField t : this.sort.getSortOrder() )
        {
            int index = t.getIndex();
            String xStr = x.getData()[index];
            String yStr = y.getData()[index];

            switch (t.getSortType())
            {
                case SortDecimal:
                    double xDouble = this.sort.getInputFormat().parse(xStr);
                    double yDouble = this.sort.getInputFormat().parse(yStr);
                    int c = Double.compare(xDouble,yDouble);
                    if (c != 0)
                    {
                        return c;
                    }
                    break;
                case SortInteger:
                    int xInteger = Integer.parseInt(xStr);
                    int yInteger = Integer.parseInt(yStr);
                    int c2 = xInteger-yInteger;
                    if (c2 != 0)
                    {
                        return c2;
                    }
                    break;
                case SortString:
                    int c3 = xStr.compareTo(yStr);
                    if (c3 != 0)
                    {
                        return c3;
                    }
                    break;
            }
        }

        // failing all of this, they are equal
        return 0;
	}



}
