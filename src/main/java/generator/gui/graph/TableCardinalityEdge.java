/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package generator.gui.graph;

import org.jgraph.graph.DefaultEdge;

/**
 *
 * @author michael
 */
public class TableCardinalityEdge extends DefaultEdge
{
    //the object can be a domain or a junction

    Object obj;

    public TableCardinalityEdge()
    {
        
    }

    //object o is stored in the graph
    public TableCardinalityEdge(Object o)
    {
        //IMPORTANT: Call super constructor!
        super(o);
        obj = o;
    }

    public void setCardinality(Object o)
    {
        obj = o;
    }

    public Object getUserObject()
    {
        return obj;
    }

    public DBCardinalityGenerator getCardinality()
    {
        return (DBCardinalityGenerator)obj;
    }
    

    public String toString()
    {
        if(obj instanceof DBCardinalityGenerator)
        {
            DBCardinalityGenerator dbCard = (DBCardinalityGenerator) obj;
            if(dbCard!=null)
            {
                return dbCard.getRelation();
            }
            else
            {
                return "CARD?";
            }
        }
        else
        {
            return "?:?";
        }
    }
}
