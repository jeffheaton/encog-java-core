package org.encog.util.kmeans;


public interface Centroid<O>
{    
    public void add(O e, int s);
    
    public void remove(O o, int sz);
    
    public double distance(O o);
}
