/*
 * TableVertexView.java
 *
 * Created on 01 June 2007, 23:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.gui.graph;

import java.awt.geom.Point2D;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

public class TableVertexView extends VertexView
{
    public static TableRenderer renderer = new TableRenderer();
    
    public TableVertexView(Object obj)
    {
        super(obj);
    }
    
    public CellViewRenderer getRenderer()
    {
        return renderer;
    }
    
    
    public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p)
    {
        
        return ((TableRenderer) getRenderer()).getPerimeterPointAnd(this, source, p);
    }
}