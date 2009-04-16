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
package org.encog.nlp.reason;

import java.util.ArrayList;
import java.util.List;

import org.encog.nlp.EncogNLP;
import org.encog.nlp.memory.AliasConcept;
import org.encog.nlp.memory.Concept;
import org.encog.nlp.memory.ConstConcept;
import org.encog.nlp.memory.Relation;
import org.encog.nlp.memory.VarConcept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * 
 * Note: This class is part of the Encog Natural Language Processing(NLP)
 * package.  This package is still under heavy construction, and will not 
 * be considered stable until Encog 3.0.
 * 
 * @author jheaton
 *
 */
public class ParseActions {
    
    private EncogNLP context;
    
    /**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /** Creates a new instance of ParseActions */
    public ParseActions(EncogNLP context) {
        this.context = context;
    }
    
    private Relation createRelation()
    {
        return context.getEvaluate().getShortTermMemory().createRelation();
    }    
    
    public void extend(Concept type)
    {
        int location = context.getEvaluate().getLocation();
        List<Relation> newRelations = new ArrayList<Relation>();
        for(Relation oldRelation:context.getEvaluate().getLastRelation().getRelations())
    	{
            Concept extensionSource = oldRelation.getNode(location);
            Relation newRelation = createRelation();
            newRelation.setType(type);           
            newRelation.setSource(extensionSource);
            newRelations.add(newRelation);
        }
        
        context.getEvaluate().replaceLastRelation (newRelations);
        

    }
    
    public void side(int source,Concept type,Concept target)
    {
    	for(Relation oldRelation:context.getEvaluate().getLastRelation().getRelations())
    	{        
            Relation relation = createRelation();
            relation.setType(type);
            relation.setTarget(target);
            AliasConcept ac = new AliasConcept();
            relation.setSource(ac);
            oldRelation.setNode(ac);            
        }
    }
    
    public void split()
    {
        int location = context.getEvaluate().getLocation();
        
        if( location==Relation.RELATION_NODE_TYPE  )
        {
            Relation relation = createRelation ();
            context.getEvaluate().addRelation(relation);
            //parse.setTarget(Relation.RELATION_NODE_SOURCE);
        }
        else
        {            
            List<Relation> newRelations = new ArrayList<Relation>();
            boolean first = true;
            for(Relation oldRelation:context.getEvaluate().getLastRelation().getRelations())
        	{
                newRelations.add(oldRelation);
                
                if( first )
                {// for chained or's... i.e. this or that or something or ...
                    Relation newRelation = createRelation();
                    newRelation.setSource(oldRelation.getSource());
                    newRelation.setType(oldRelation.getType());                
                    newRelations.add(newRelation);                        
                    int last = context.getEvaluate().getLastRelation().findFirstEmpty();
                    if( last!=Relation.RELATION_NODE_NONE )
                        newRelation.setShouldReplace(Relation.RELATION_NODE_SOURCE);   
                    first = false;
                }
            }
            //parse.setTarget(Relation.RELATION_NODE_TARGET);
            context.getEvaluate().replaceLastRelation (newRelations);
        }
    }
    
    public void processBasicWord(String word)
    {
        // bound the location into either sorce or target
        // type is handled elsewhere
        int location = context.getEvaluate().getLocation();
        if( (location!=Relation.RELATION_NODE_SOURCE) && (location!=Relation.RELATION_NODE_TARGET) )
        {
            if( location==Relation.RELATION_NODE_TYPE )
                location = Relation.RELATION_NODE_SOURCE;
            else
                location = Relation.RELATION_NODE_TARGET;
        }
        
        // now add the new word to either source or target
        if( context.getEvaluate().getLastRelation().countContents (location)>=context.getEvaluate().getLastRelation().getRelations().size() )   
        {
            makeAttribute(location,word);
        }
        else
            lastRelationAdd (word);

        
        // now clear any replace flags
        context.getEvaluate().getShortTermMemory().clearShouldReplace();
    }
    
    public void makeAttribute(int target,String word)
    {
        VarConcept concept = context.getMemory().getConcepts().create(word);
        
        for(Relation relation:context.getEvaluate().getLastRelation().getRelations())
        {            
            // see if old relation needs to be made an alias
            Concept out = relation.getNode(target);
            if( !(out instanceof AliasConcept) )
            {
                AliasConcept ac = new AliasConcept();
                ac.setTargetConcept(concept);
                relation.setNode(target,ac);
            }
            else
            {
                AliasConcept ac = (AliasConcept)out;
                out = out.makeSolid();
                ac.setTargetConcept(concept);
            }
            
            // create new relation
            Relation newRelation = context.getEvaluate().getShortTermMemory().createRelation();
            newRelation.setSource(relation.getNode(target));
            newRelation.setType(ConstConcept.CONCEPT_SUBTYPE);
            newRelation.setTarget(out);
        }                               
    }
        
    public void beginQuestion(String word)
    {
        lastRelationAdd( Relation.RELATION_NODE_TYPE,  context.getTypeConcept(word) );
        context.getEvaluate().setType( EvaluateText.TYPE_YESNO );
    }  
    
    
    public void lastRelationAdd(Concept concept)
    {
        int target = context.getEvaluate().getLastRelation().findFirstEmpty();
        for(Relation relation:context.getEvaluate().getLastRelation().getRelations())
        {
            if( relation.isNodeEmpty(target) )
                relation.setNode(target,concept);
        }          
    }    
    
    public void lastRelationAdd(String word)
    {
        lastRelationAdd(context.getMemory().getConcepts().create(word));
    }
    
    public void lastRelationAdd(int target,Concept concept)
    {
        for(Relation relation:context.getEvaluate().getLastRelation().getRelations())
        {
            if( relation.isNodeEmpty(target) )
                relation.setNode(target,concept);
        }          
    }    
    
    public void lastRelationAdd(int target,String word)
    {
        lastRelationAdd(target,context.getMemory().getConcepts().create(word));
    }   
}
