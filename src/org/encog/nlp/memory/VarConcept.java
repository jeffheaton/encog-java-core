/*
 * Encog Artificial Intelligence Framework v1.x
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
package org.encog.nlp.memory;

/**
 *
 * @author  jheaton
 */
public class VarConcept extends Concept {
    
    private String contents = "";
    private long serialNumber=-1;
        
    VarConcept(long serialNumber,String contents)
    {
        this.serialNumber = serialNumber;
        this.contents = contents;
    }
    
    public String getContents()
    {
        return contents;
    }
            
    public boolean equals(Object obj)
    {
        if( obj instanceof String )
            return compareSingle(contents,(String)obj);
        
        if( !(obj instanceof VarConcept) )
            return false;
        
        VarConcept concept = (VarConcept)obj;
        
        // handle virtual VarConcepts (with no elements)
        if( (concept.getContents().length()==0) ||
            (concept.getContents().length()==0) )
            return(this.getSerialNumber() == concept.getSerialNumber());
        else        
            return compareSingle(contents,concept.getContents());        
    }   
    
    public boolean isEmpty()
    {
        return(contents.length()==0);
    }
    
    public long getSerialNumber()
    {
        return serialNumber;
    }
    
    public String save()
    {
        StringBuffer result = new StringBuffer();
        result.append("C|");
        result.append(serialNumber);
        result.append("|");
        result.append(getContents());        
        return result.toString();
    }
    
    public String toString()
    {
        return "[Serial:" + getSerialNumber() +  "," + getContents() + "]";
    }                 
}
