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

import java.util.*;

/**
 *
 * @author  jheaton
 */
public class ConstConcept extends Concept {
    //public static final Concept CONCEPT_ATTRIBUTE = new ConstConcept("[ATTRIBUTE]",-10);
    public static final Concept CONCEPT_SUBTYPE = new ConstConcept("[SUBTYPE]",-11);
    public static final Concept CONCEPT_SUPERTYPE = new ConstConcept("[SUPERTYPE]",-12);
    public static final Concept CONCEPT_HAS = new ConstConcept("[HAS]",-13);
    public static final Concept CONCEPT_OF = new ConstConcept("[OF]",-14);
    public static final Concept CONCEPT_CREATE = new ConstConcept("[CREATE]",-15);
    
    public static final Concept CONCEPT_WTYPE_ACTION = new ConstConcept("[TYPE:Action]", -100);
    public static final Concept CONCEPT_WTYPE_SPLIT = new ConstConcept("[TYPE:Split]", -101);
    public static final Concept CONCEPT_WTYPE_QUESTION_SIMPLE = new ConstConcept("[TYPE:QuestionSimple]", -102);
    public static final Concept CONCEPT_WTYPE_QUESTION_EMBED = new ConstConcept("[TYPE:QuestionEmbed]", -103);
    public static final Concept CONCEPT_WTYPE_NODE = new ConstConcept("[TYPE:Node]", -100);
    
    
    private String key;
    private static List<ConstConcept> list;
    
    /** Creates a new instance of ConstConcept */
    private ConstConcept(String key,long serialNumber) {
        this.key = key;
        if( list==null )
            list = new ArrayList<ConstConcept>();
        list.add(this);
    }
    
    public String toString()
    {
        return key;
    }
    
    public boolean equals(Object obj)
    {
        boolean result;
        
        result = (obj==this);
        return result;            
    }
    
    public static ConstConcept find(long id)
    {
        ConstConcept result = null;
        
        for(ConstConcept concept:list)
        {
            if( concept.getSerialNumber() == id )
                result = concept;
        }
        
        if( result==null )
            throw new ConceptNotFoundError("Can't find concept with serial number: " + id );
        
        return result;
    }
    
}
