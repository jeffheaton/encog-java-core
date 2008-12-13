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
public class RomMemory {
    
    /** Creates a new instance of RomMemory */
    public RomMemory() {
    }
    
    public static void load(LongTermMemory memory)
    {
        Concept base = memory.getConcepts().create();
        memory.addRelation(base,ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_ACTION );
        memory.addRelation("produces", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("produce", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("producing", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("produced", ConstConcept.CONCEPT_SUBTYPE, base );
        
        base = memory.getConcepts().create();
        memory.addRelation(base,ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_ACTION );
        memory.addRelation("lives", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("live", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("living", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("lived", ConstConcept.CONCEPT_SUBTYPE, base );
        
        base = memory.getConcepts().create();
        memory.addRelation(base,ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_ACTION );
        memory.addRelation("jumps", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("jump", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("jumping", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("jumped", ConstConcept.CONCEPT_SUBTYPE, base );          
        
        base = memory.getConcepts().create();
        memory.addRelation(base,ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_ACTION );
        memory.addRelation("hits", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("hit", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("hitting", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("hit", ConstConcept.CONCEPT_SUBTYPE, base );          
        
        base = memory.getConcepts().create();
        memory.addRelation(base,ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_ACTION );
        memory.addRelation("begets", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("beget", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("begetting", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("begat", ConstConcept.CONCEPT_SUBTYPE, base );         
        
        base = memory.getConcepts().create();
        memory.addRelation(base,ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_ACTION );
        memory.addRelation("owns", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("own", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("owning", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("owned", ConstConcept.CONCEPT_SUBTYPE, base );          
        
        base = memory.getConcepts().create();
        memory.addRelation(base,ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_ACTION );
        memory.addRelation("brings", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("bring", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("bringing", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("brought", ConstConcept.CONCEPT_SUBTYPE, base );            
        
        base = memory.getConcepts().create();
        memory.addRelation(base,ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_SPLIT );
        memory.addRelation("and", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("or", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("nor", ConstConcept.CONCEPT_SUBTYPE, base );        
        
        base = memory.getConcepts().create();
        memory.addRelation(base,ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_QUESTION_EMBED );
        memory.addRelation("is", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("are", ConstConcept.CONCEPT_SUBTYPE, base );
        
        base = memory.getConcepts().create();
        memory.addRelation(base,ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_QUESTION_SIMPLE );
        memory.addRelation("does", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("do", ConstConcept.CONCEPT_SUBTYPE, base );
        memory.addRelation("will", ConstConcept.CONCEPT_SUBTYPE, base );                
        memory.addRelation("has", ConstConcept.CONCEPT_SUBTYPE, base );
        
        memory.addRelation("you",ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_NODE);
        memory.addRelation("he",ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_NODE);
        memory.addRelation("she",ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_NODE);
        memory.addRelation("it",ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_NODE);
        memory.addRelation("they",ConstConcept.CONCEPT_SUBTYPE, ConstConcept.CONCEPT_WTYPE_NODE);
        
        
        
        // word.equals("are") || word.equals("is") || word.equals("has")|| word.equals("to")
        
    }
    
}
