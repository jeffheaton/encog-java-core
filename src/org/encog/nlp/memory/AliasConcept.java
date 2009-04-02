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
package org.encog.nlp.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An alias concept is a concept holder. This allows all instances of the
 * Concept to be updated when the value of the alias is changed to a tottally
 * new concept.
 * @author  jheaton
 */
public class AliasConcept extends VarConcept {
    private VarConcept targetConcept;
    
    /**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /** Creates a new instance of AliasConcept */
    public AliasConcept() {
        super(-1,"");
    }
    
    public void setTargetConcept(VarConcept targetConcept)
    {
        this.targetConcept = targetConcept;
    }
    
    public String getContents()
    {
        return targetConcept.getContents();
    }
            
    public boolean equals(Object obj)
    {
        if( targetConcept==null )
            return false;
        else
            return targetConcept.equals(obj);
    }   
    
    public boolean isEmpty()
    {
        if( targetConcept==null )
            return true;
        else           
            return targetConcept.isEmpty();
    }
    
    public long getSerialNumber()
    {
        if( targetConcept==null )
            return -1;
        else
            return targetConcept.getSerialNumber();
    }
    
    public String save()
    {       
        if( targetConcept!=null )
            return targetConcept.save();
        else
            return "";
    }        
    
    public Concept makeSolid()
    {
        return targetConcept;
    }   
    
    public String toString()
    {
        if( targetConcept==null )
            return "[AC:null]";
        else
            return "[AC:" + targetConcept.toString() + "]";
    }     
}
