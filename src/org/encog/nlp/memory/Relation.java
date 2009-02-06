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
public class Relation {
    public static final int RELATION_NODE_NONE = 0;
    public static final int RELATION_NODE_SOURCE = 1;
    public static final int RELATION_NODE_TYPE = 2;
    public static final int RELATION_NODE_TARGET = 3;
    
    private Concept source;
    private Concept type;
    private Concept target;
    private int shouldReplace = RELATION_NODE_NONE;
    
    public void setShouldReplace(int shouldReplace)
    {
        this.shouldReplace = shouldReplace;
    }
    
    public int getShouldReplace()
    {
        return shouldReplace;
    }    
    
    /** Creates a new instance of Relation */
    public Relation() {
    }
    
    public Relation(Concept source,Concept type,Concept target) {
        this.setSource(source);
        this.setType(type);
        this.setTarget(target);
    }
    
    public void setSource(Concept source)
    {
        if( (this.source instanceof AliasConcept) && (source instanceof VarConcept) )
            ((AliasConcept)this.source).setTargetConcept((VarConcept)source);
        else          
            this.source = source;
    }
    
    public void setType(Concept type)
    {
        if( (this.type instanceof AliasConcept) && (type instanceof VarConcept) )
            ((AliasConcept)this.type).setTargetConcept((VarConcept)type);
        else        
            this.type = type;
    }
    
    public void setTarget(Concept target)
    {
        if( (this.target instanceof AliasConcept) && (target instanceof VarConcept) )
            ((AliasConcept)this.target).setTargetConcept((VarConcept)target);
        else
            this.target = target;
    }
    
    public Concept getSource()
    {      
        return source;
    }
    
    public Concept getType()
    {
        return type;
    }
    
    public Concept getTarget()
    {
        return target;
    }
    
    public String toString()
    {
        String str = "";
        
        str+= (source==null)?"null":source.toString();
        str+="--(";
        str+=(type==null)?"null":type.toString();
        str+=")-->";
        str+= (target==null)?"null":target.toString();
        return str;
    }
    
    public boolean equals(Object obj)
    {
        if( !(obj instanceof Relation) )
            return false;
        Relation relation = (Relation)obj;
        
        if( relation.getSource()==null && getSource()!=null )            
            return false;
        
        if( relation.getTarget()==null && getTarget()!=null )
            return false;
        
        if( relation.getType()==null && getType()!=null )
            return false;        
        
        if( relation.getSource().equals(getSource()) &&
            relation.getTarget().equals(getTarget()) &&
            relation.getType().equals(getType()) )
            return true;
        else
            return false;
    }
    
    public String save()
    {
        StringBuffer result = new StringBuffer();
        result.append("R|");
        result.append( (source==null)?"null":""+source.getSerialNumber() );
        result.append('|');
        result.append( (type==null)?"null":""+type.getSerialNumber() );
        result.append('|');
        result.append( (target==null)?"null":""+target.getSerialNumber() );
        
        return result.toString();
    }
    
    public Concept getNode(int target)
    {
        switch(target)
        {
            case RELATION_NODE_SOURCE:return getSource();
            case RELATION_NODE_TYPE:return getType();
            case RELATION_NODE_TARGET:return getTarget();
        }
        return null;
    }
    
    public void setNode(int target,Concept concept)
    {
        switch(target)
        {
            case RELATION_NODE_SOURCE:setSource(concept);break;
            case RELATION_NODE_TYPE:setType(concept);break;
            case RELATION_NODE_TARGET:setTarget(concept);break;
        }
    }    
    
    public boolean isNodeEmpty(int target)
    {
        if( shouldReplace == target )
            return true;
                
        Concept concept = getNode(target);
        if( concept==null )
            return true;
        if( !(concept instanceof VarConcept) )
            return false;
        
        VarConcept var = (VarConcept)concept;
        if( var.isEmpty() )
            return true;
        else
            return false;        
    }
    
    public int getNextNode()
    {
        if( isNodeEmpty(Relation.RELATION_NODE_SOURCE) )
            return Relation.RELATION_NODE_SOURCE;
        else if( isNodeEmpty(Relation.RELATION_NODE_TYPE) )
            return Relation.RELATION_NODE_TYPE;
        else if( isNodeEmpty(Relation.RELATION_NODE_TARGET) )
            return Relation.RELATION_NODE_TARGET;
        else return Relation.RELATION_NODE_NONE;
    }
    
    public void setNode(Concept concept)
    {
        setNode(getNextNode(), concept);
    }
    
    public static int forward(int node)
    {
			if( node==RELATION_NODE_SOURCE )
				return RELATION_NODE_TYPE;
      else if( node==RELATION_NODE_TYPE )
				return RELATION_NODE_TARGET;      
      else
        return RELATION_NODE_NONE;
    }
}
