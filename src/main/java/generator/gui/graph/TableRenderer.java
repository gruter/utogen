/*
 * Some of the bits here have come straight away from the dbViz project
 */
package generator.gui.graph;

import generator.db.DBTable;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

public class TableRenderer extends JPanel implements CellViewRenderer
{

    /** The default foreground color = Color.black */
    public static final Color DEFAULT_FOREGROUND = Color.black;
    public static final Color DEFAULT_BACKGROUND = Color.yellow;
    protected Color defaultForeground,  defaultBackground,  bordercolor;
    protected int borderWidth;
    protected boolean hasFocus,  selected,  preview,  opaque;
    protected JGraph graph;
    DBTable table;
    final int OFFSET_LEFT = 15;
    final int OFFSET_COLUMN_2 = 40;
    final int OFFSET_TOP = 18;
    int x, y;

    public Component getRendererComponent(JGraph graph, CellView cellview, boolean sel, boolean focus, boolean preview)
    {
        if (cellview instanceof TableVertexView)
        {
            TableVertexView view = (TableVertexView) cellview;

            DBGraphCell myCell = (DBGraphCell) view.getCell();
            table = (DBTable) myCell.obj;

            this.graph = graph;
            this.hasFocus = focus;
            this.selected = sel;
            this.preview = preview;

            setForeground(DEFAULT_FOREGROUND);
            setBackground(DEFAULT_BACKGROUND);

            setComponentOrientation(graph.getComponentOrientation());

            return this;
        }
        return null;
    }

    public void paint(Graphics g)
    {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        Dimension d = getSize();
        g.drawRect(0, 0, d.width - 1, d.height - 1);

        Font font = new Font("Verdana", 1, 11);
        g.setFont(font);
        g.setColor(Color.black);

        FontMetrics fm = getFontMetrics(font);
        g.drawString(table.getName(), (d.width - fm.stringWidth(table.getName())) / 2, OFFSET_TOP);
        g.drawLine(0, OFFSET_TOP + 5, d.width - 1, OFFSET_TOP + 5);
        int y = OFFSET_TOP + 20;
        int max = getMaxWidth(null, table);

        for (int i = 0; i < table.getNumFields(); i++)
        {
            g.drawString(table.fieldNameAt(i), OFFSET_LEFT, y);
            g.drawString(table.fieldTypeAt(i), max + OFFSET_COLUMN_2, y);
            y += fm.getHeight();
        }
        x = (int) d.width - 1;
        y = (int) d.height - 1;

        if (selected || hasFocus)
        {
            // Draw selection/highlight border
            ((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
            if (hasFocus)
            {
                g.setColor(graph.getGridColor());
            } else if (selected)
            {
                g.setColor(graph.getHighlightColor());
            }
            d = getSize();
            g.drawRect(0, 0, d.width - 1, d.height - 1);
        }
    }//paint graphics

    public int getMaxWidth(Font font, DBTable table)
    {
        int width, max = 0;

        if (font == null)
        {
            font = new Font("Verdana", 1, 11);
        }

        FontMetrics fm = getFontMetrics(font);
        for (int i = 0; i < table.getNumFields(); i++)
        {
            String s = table.fieldNameAt(i);
            width = fm.stringWidth(s);
            if (width > max)
            {
                max = width;
            }
        }
        return max;
    }

    public Point2D getPerimeterPointAnd(VertexView view, Point2D source, Point2D p)
    {
        Rectangle2D bounds = view.getBounds();
        double x = bounds.getX();
        double y = bounds.getY();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        double xCenter = x + width / 2;
        double yCenter = y + height / 2;
        double dx = p.getX() - xCenter; // Compute Angle
        double dy = p.getY() - yCenter;
        double alpha = Math.atan2(dy, dx);
        double xout = 0, yout = 0;
        double pi = Math.PI;
        double pi2 = Math.PI / 2.0;
        double beta = pi2 - alpha;
        double t = Math.atan2(height, width);
        if (alpha < -pi + t || alpha > pi - t)
        { // Left edge
            xout = x;
            yout = yCenter - width * Math.tan(alpha) / 2;
        } else if (alpha < -t)
        { // Top Edge
            yout = y;
            xout = xCenter - height * Math.tan(beta) / 2;
        } else if (alpha < t)
        { // Right Edge
            xout = x + width;
            yout = yCenter + width * Math.tan(alpha) / 2;
        } else
        { // Bottom Edge
            yout = y + height;
            xout = xCenter + height * Math.tan(beta) / 2;
        }
        return new Point2D.Double(xout, yout);
    }
}//eoc AnotherRenderer
