/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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
package org.encog.nlp.memory;

/**
 *
 * @author  jheaton
 */
public class Concept {
    
    /** Creates a new instance of Concept */
    public Concept() {
    }
    
    public long getSerialNumber()
    {
        return -1;
    }
    
    public boolean isEmpty()
    {
        return false;
    }
    
    public int hashCode()
    {
        String str = toString();
        return str.hashCode();
    }    
    
    public static boolean compareSingle(String word1,String word2)
    {
        if( word1.equals(word2) )
            return true;
        
       if( word1.endsWith("s") && !word1.equalsIgnoreCase("its") )
            word1 = word1.substring(0,word1.length()-1);
        
        if( word2.endsWith("s") && !word2.equalsIgnoreCase("its") )
            word2 = word2.substring(0,word2.length()-1);       
            
        return word1.equals(word2);
    }
    
    public Concept makeSolid()
    {
        return this;
    }            
}
