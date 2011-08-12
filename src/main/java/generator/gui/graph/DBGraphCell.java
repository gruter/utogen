/*
 * MyCell.java
 *
 * Created on 01 June 2007, 23:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.gui.graph;

import generator.db.DBTableGenerator;
import org.jgraph.graph.DefaultGraphCell;

public class DBGraphCell extends DefaultGraphCell
{
    //the object can be a domain or a junction
    Object obj;
    
    //object o is stored in the graph
    public DBGraphCell(Object o)
    {
        //IMPORTANT: Call super constructor!
        super(o);
        obj  = o;
    }
    
    public Object getUserObject()
    {
        return obj;
    }
    
    public String toString()
    {
        return obj.toString();
    }

    public DBTableGenerator getDBTableGenerator()
    {
        return (DBTableGenerator)obj;
    }

    public int hashCode()
    {
        return (obj.hashCode());
    }


    public boolean equals( Object o )
    {
        if( o == this ) {
            return (true);
        } else if( o instanceof DBGraphCell ) {
            DBGraphCell graphCell = (DBGraphCell)o;
            return (obj.equals( graphCell.obj ));
        } else {
            return (false);
        }
    }
}//