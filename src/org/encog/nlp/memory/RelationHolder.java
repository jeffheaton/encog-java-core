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

import java.util.*;

/**
 *
 * @author  jheaton
 */
public class RelationHolder {
    private List<Relation> relations = new ArrayList<Relation>();
    
    /** Creates a new instance of RelationHolder */
    public RelationHolder() {
    }
    
    public void add(Relation relation)
    {
        relations.add(relation);
    }    
    
    public void clear()
    {
        relations.clear();
    }
    
    public List<Relation> getRelations()
    {
        return relations;
    }
    
    public void add(RelationHolder holder)
    {
        relations.addAll(holder.getRelations());
    }
    
    public boolean contains(Relation find)
    {
    	for(Relation relation:relations)
    	{
            if( relation.equals(find) )
                return true;
        }
        return false;
    }
    
    public void dump()
    {    
    	for(Relation relation:relations)
    	{    	
            System.out.println( relation.toString() );
        }    
    }

    
    public List<Relation> searchSource(Concept source)
    {
        List<Relation> result = new ArrayList<Relation>();
        
        if( source!=null )
        {      
        	for(Relation relation:relations)
        	{
                if( source.equals(relation.getSource())  )
                    result.add(relation);
            }
        }
        
        return result;        
    }    
    
    public List<Relation> searchSourceType(Concept source,Concept type)
    {
        List<Relation> result = new ArrayList<Relation>();

        for(Relation relation:relations)
    	{
            if( relation.getSource().equals(source) &&
                relation.getType().equals(type) )
                result.add(relation);
        }
        
        return result;        
    }
    
    public List<Relation> searchSourceTypeBase(Concept source,Concept type)
    {
        List<Relation> result = new ArrayList<Relation>();

        for(Relation relation:relations)
    	{
            if( relation.getSource().equals(source) &&
                relation.getType().equals(type) )
            {
                List<Relation> l = searchSourceTypeBase(relation.getTarget(),type);
                result.add(relation);
                result.addAll(l);
            }
        }
        
        return result;        
    }
    
    

    public List searchTypeTarget(Concept type,Concept target)
    {
        List<Relation> result = new ArrayList<Relation>();
        
        for(Relation relation:relations)
    	{
            if( relation.getType().equals(type) &&
                relation.getTarget().equals(target) )
                result.add(relation);
        }
        
        return result;        
    }
    
    
    public int countTargets(Concept concept,Concept type)
    {
        int count = 0;
        
        if( concept==null )
            return 0;

        for(Relation relation:relations)
    	{
            if( (type!=null) && !relation.getType().equals(type) )
                continue;
            if( relation.isNodeEmpty(Relation.RELATION_NODE_TARGET) )
                continue;
            if( concept.equals(relation.getTarget()) )
                count++;            
        }
        return count;
    }
    
    public Collection getBaseConcepts()
    {
        Set<Concept> result = new HashSet<Concept>();

        for(Relation relation:relations)
    	{
            //if( relation.getType().equals( ConstConcept.CONCEPT_ATTRIBUTE ) )
              //  continue;
            if( countTargets( relation.getSource(), null ) >0 )
                continue;
            result.add(relation.getSource());            
        }     
        
        // if NONE are found, just pick the first one
        if( (result.size()==0) && (relations.size()>0) )
        {
            Relation relation = (Relation)relations.get(0);
            result.add(relation.getSource());
        }
        
        return result;
    }
    
    public Collection<Relation> getBaseRelations()
    {
        List<Relation> result = new ArrayList<Relation>();

        for(Relation relation:relations)
    	{
            if( countTargets( relation.getSource(),null ) >0 )
                continue;
            result.add(relation);            
        }
        
        return result;
    }   
    
    public Relation createRelation()
    {
        Relation relation = new Relation();
        add(relation);
        return relation;
    }
    
    private boolean isNull(Concept concept)
    {
        if( concept==null )
            return true;
        
        if( concept instanceof VarConcept )
        {
            VarConcept var = (VarConcept)concept;
            if( var.isEmpty() )
                return true;
        }
            
        return false;
    }
    
    public int countNull()
    {
        int result = 0;
        
        for(Relation relation:relations)
    	{
            if( isNull(relation.getSource()) )
                result++;
            if( isNull(relation.getType()) )
                result++;
            if( isNull(relation.getTarget()) )
                result++;
        }
        
        return result;
    }   
    
    public int findFirstEmpty()
    {
        int result = Relation.RELATION_NODE_SOURCE;
        while( result!=Relation.RELATION_NODE_NONE )
        {
        	for(Relation relation:relations)
        	{
                if( relation.isNodeEmpty(result) )
                    return result;
            }
            result = Relation.forward(result);
        }
        return result;
    }
    
    public void clearShouldReplace()
    {
    	for(Relation relation:relations)
    	{
            relation.setShouldReplace(Relation.RELATION_NODE_NONE);            
        }
    }
    
    public int countContents(int target)
    {
        int result = 0;
        for(Relation relation:relations)
    	{
            if( !relation.isNodeEmpty(target) )
                result++;
        }                  
        return result;
    }    
    
    public void replaceRelations(List<Relation> list)
    {
        this.relations = list;
    }
    
    
}
