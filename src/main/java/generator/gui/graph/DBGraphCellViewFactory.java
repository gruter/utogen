/*
 * MyCellViewFactory.java
 *
 * Created on 01 June 2007, 23:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.gui.graph;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;

public class DBGraphCellViewFactory extends DefaultCellViewFactory
{
    
    public CellView createView(GraphModel model, Object cell)
    {
        CellView view = null;
        
        if (model.isPort(cell))
            view = createPortView(cell);
        else if (model.isEdge(cell))
            view = createEdgeView(cell);
        else
            view = createVertexView(cell);
        return view;
    }
    
    protected VertexView createVertexView(Object cell)
    {
        return new TableVertexView(cell);
    }
    protected EdgeView createEdgeView(Object cell)
    {
        return new EdgeView(cell);
    }
    protected PortView createPortView(Object cell)
    {
        return new PortView(cell);
    }
}