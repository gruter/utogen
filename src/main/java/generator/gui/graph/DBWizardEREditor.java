/*
 * DBWizardEREditor.java
 *
 * Created on 29 May 2007, 02:12
 */
package generator.gui.graph;

import generator.gui.db.*;
import generator.db.DBTableGenerator;
import generator.db.DBGeneratorDefinition;
import generator.extenders.RandomiserInstance;
import generator.misc.ApplicationContext;
import generator.misc.RandomiserType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.apache.log4j.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphUndoManager;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

/**
 *
 * The graph editor for the table relations....
 */
public class DBWizardEREditor extends javax.swing.JPanel implements GraphSelectionListener, IWizardStep
{

    private List<DBTableGenerator> alDBTableGenerator;
    private HashMap<String, DBTableGenerator> hmDBTableGenerator;
    private HashMap hmPorts;
    private Logger logger = Logger.getLogger(DBWizardEREditor.class);
    private JGraph graph;
    private DBPopup popupDB;
    private Vector<RandomiserInstance> vRI;
    private Vector<RandomiserType> vRT;
    private DBGeneratorDefinition dbGeneratorDefinition;
    // Actions which Change State
    protected Action undo,  redo,  remove,  group,  ungroup,  tofront,  toback,  cut,  copy,  paste;
    // Undo Manager
    protected GraphUndoManager undoManager;
    protected JToolBar toolbar;

    /** Creates new form DBWizardEREditor */
    public DBWizardEREditor()
    {
        initComponents();

        vRI = ApplicationContext.getInstance().getRandomiserInstances();
        vRT = ApplicationContext.getInstance().getRandomiserTypes();
        alDBTableGenerator = new ArrayList();
        hmDBTableGenerator = new HashMap();
        hmPorts = new HashMap();

        DefaultCellViewFactory dv;


        GraphModel model = new DefaultGraphModel();

        GraphLayoutCache view = new GraphLayoutCache(model, new DBGraphCellViewFactory());
        //GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory());
        graph = new JGraph(model, view);

        graph.setSizeable(true);
        graph.setMarqueeHandler(createMarqueeHandler());

        // Make Ports Visible by Default
        graph.setPortsVisible(true);
        // Use the Grid (but don't make it Visible)
        graph.setGridEnabled(true);
        // Set the Grid Size to 10 Pixel
        graph.setGridSize(10);
        // Set the Tolerance to 2 Pixel
        graph.setTolerance(2);
        // Accept edits if click on background
        graph.setInvokesStopCellEditing(true);
        // Allows control-drag
        graph.setCloneable(true);
        // Jump to default port on connect
        graph.setJumpToDefaultPort(true);

        setLayout(new BorderLayout());
        toolbar = createToolBar();
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(graph), BorderLayout.CENTER);
        popupDB = new DBPopup(this);
    }

    public void valueChanged(GraphSelectionEvent graphSelectionEvent)
    {
    }

    // Hook for subclassers
    protected BasicMarqueeHandler createMarqueeHandler()
    {
        return new MyMarqueeHandler();
    }

    public Object getPort(Object key)
    {
        return hmPorts.get(key);
    }

    public Object[] getSelectedCells()
    {
        Object[] cells = graph.getSelectionCells();
        return cells;
    }

    public void removeTableGenerator(String tableName)
    {
        hmDBTableGenerator.remove(tableName);
    }

    public void removeCells(Object cells[])
    {
        graph.getModel().remove(cells);
    }

    public Object[] getDescendants(Object cells[])
    {
        return graph.getDescendants(cells);
    }

    // Insert a new Edge between source and target
    public void connect(Port source, Port target, DBCardinalityGenerator dbCardinality)
    {
        // Construct Edge with no label
        DefaultEdge edge = createDefaultEdge(dbCardinality);
        if (graph.getModel().acceptsSource(edge, source) && graph.getModel().acceptsTarget(edge, target))
        {
            // Create a Map thath holds the attributes for the edge
            edge.getAttributes().applyMap(createEdgeAttributes());
            // Insert the Edge and its Attributes
            graph.getGraphLayoutCache().insertEdge(edge, source, target);
        }
    }

    // Hook for subclassers
    private Map createEdgeAttributes()
    {
        Map map = new Hashtable();
        // Add a Line End Attribute
        GraphConstants.setLineEnd(map, GraphConstants.ARROW_SIMPLE);
        // Add a label along edge attribute
        GraphConstants.setLabelAlongEdge(map, true);
        return map;
    }
    // Hook for subclassers

    private DefaultEdge createDefaultEdge(DBCardinalityGenerator dbCardinality)
    {
        return new TableCardinalityEdge(dbCardinality);
    }

    private JToolBar createToolBar()
    {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        Action action;
        URL url;

        // Remove
        URL removeUrl = getClass().getClassLoader().getResource(
                "resources/images/graph/delete.gif");
        ImageIcon removeIcon = new ImageIcon(removeUrl);
        remove = new AbstractAction("", removeIcon)
        {

            public void actionPerformed(ActionEvent e)
            {
                if (!graph.isSelectionEmpty())
                {
                    int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected object?", "Question", JOptionPane.YES_NO_OPTION);
                    if (answer == 0)
                    {
                        Object[] cells = graph.getSelectionCells();
                        cells = graph.getDescendants(cells);
                        for (int i = 0; i < cells.length; i++)
                        {
                            if (cells[i] instanceof DBGraphCell)
                            {
                                Object obj = ((DBGraphCell) cells[i]).getUserObject();
                                if (obj instanceof DBTableGenerator)
                                {
                                    hmDBTableGenerator.remove(((DBTableGenerator) obj).getName());
                                }
                                if (obj instanceof DBCardinalityGenerator)
                                {
                                    DBCardinalityGenerator dbCE = (DBCardinalityGenerator) obj;
                                    DBTableGenerator dbT = dbCE.getToTable();
                                    dbT.clearForeignKeys();
                                }
                            }
                        }
                        graph.getModel().remove(cells);
                    }
                }
            }
        };
        remove.setEnabled(true);
        toolbar.add(remove);

        // To Front
        toolbar.addSeparator();
        URL toFrontUrl = getClass().getClassLoader().getResource(
                "resources/images/graph/tofront.gif");
        ImageIcon toFrontIcon = new ImageIcon(toFrontUrl);
        tofront = new AbstractAction("", toFrontIcon)
        {

            public void actionPerformed(ActionEvent e)
            {
                if (!graph.isSelectionEmpty())
                {
                    toFront(graph.getSelectionCells());
                }
            }
        };
        tofront.setEnabled(true);
        toolbar.add(tofront);

        // To Back
        URL toBackUrl = getClass().getClassLoader().getResource(
                "resources/images/graph/toback.gif");
        ImageIcon toBackIcon = new ImageIcon(toBackUrl);
        toback = new AbstractAction("", toBackIcon)
        {

            public void actionPerformed(ActionEvent e)
            {
                if (!graph.isSelectionEmpty())
                {
                    toBack(graph.getSelectionCells());
                }
            }
        };
        toback.setEnabled(true);
        toolbar.add(toback);

        // Zoom Std
        toolbar.addSeparator();
        URL zoomUrl = getClass().getClassLoader().getResource(
                "resources/images/graph/zoom.gif");
        ImageIcon zoomIcon = new ImageIcon(zoomUrl);
        toolbar.add(new AbstractAction("", zoomIcon)
        {

            public void actionPerformed(ActionEvent e)
            {
                graph.setScale(1.0);
            }
        });
        // Zoom In
        URL zoomInUrl = getClass().getClassLoader().getResource(
                "resources/images/graph/zoomin.gif");
        ImageIcon zoomInIcon = new ImageIcon(zoomInUrl);
        toolbar.add(new AbstractAction("", zoomInIcon)
        {

            public void actionPerformed(ActionEvent e)
            {
                graph.setScale(2 * graph.getScale());
            }
        });
        // Zoom Out
        URL zoomOutUrl = getClass().getClassLoader().getResource(
                "resources/images/graph/zoomout.gif");
        ImageIcon zoomOutIcon = new ImageIcon(zoomOutUrl);
        toolbar.add(new AbstractAction("", zoomOutIcon)
        {

            public void actionPerformed(ActionEvent e)
            {
                graph.setScale(graph.getScale() / 2);
            }
        });



        return toolbar;
    }

    // Brings the Specified Cells to Front
    public void toFront(Object[] c)
    {
        graph.getGraphLayoutCache().toFront(c);
    }

    // Sends the Specified Cells to Back
    private void toBack(Object[] c)
    {
        graph.getGraphLayoutCache().toBack(c);
    }

    //get the dimensions of a node
    private Dimension getDimensions(Font font, DBTableGenerator table)
    {
        Dimension d = new Dimension();
        FontMetrics fm;
        int height, maxWidth1, maxWidth2, charsWidth1, charsWidth2;
        String fieldname, fieldType;

        if (font == null)
        {
            font = new Font("Verdana", 1, 11);
        }

        fm = getFontMetrics(font);

        height = (table.getNumFields() * fm.getHeight()) + (fm.getHeight() * 2);
        maxWidth1 = 0;
        maxWidth2 = 0;
        for (int i = 0; i < table.getNumFields(); i++)
        {
            fieldname = table.fieldNameAt(i);
            fieldType = table.fieldTypeAt(i);
            charsWidth1 = fm.stringWidth(fieldname);
            charsWidth2 = fm.stringWidth(fieldType);
            if (charsWidth1 > maxWidth1)
            {
                maxWidth1 = charsWidth1;
            }
            if (charsWidth2 > maxWidth2)
            {
                maxWidth2 = charsWidth2;
            }

        }
        d.setSize(maxWidth1 + maxWidth2 + 55, height);
        return d;
    }

    private void createVertex(DefaultGraphCell cell, double x, double y, double w, double h)
    {
        // Set bounds
        GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(x, y, w, h));

        GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createRaisedBevelBorder());

        GraphConstants.setEditable(cell.getAttributes(), false);
        GraphConstants.setSizeable(cell.getAttributes(), true);
        GraphConstants.setAutoSize(cell.getAttributes(), false);
        GraphConstants.setRouting(cell.getAttributes(), GraphConstants.ROUTING_SIMPLE);
        GraphConstants.setBendable(cell.getAttributes(), true);

        // Add a Floating Port
        hmPorts.put(cell.getUserObject(), cell.addPort());
    }

    // Hook for subclassers
    protected DefaultGraphCell createGroupCell()
    {
        return new DefaultGraphCell();
    }

    // This will change the source of the actionevent to graph.
    private class EventRedirector extends AbstractAction
    {

        protected Action action;

        // Construct the "Wrapper" Action
        public EventRedirector(Action a)
        {
            super("", (ImageIcon) a.getValue(Action.SMALL_ICON));
            this.action = a;
        }

        // Redirect the Actionevent
        public void actionPerformed(ActionEvent e)
        {
            e = new ActionEvent(graph, e.getID(), e.getActionCommand(), e.getModifiers());
            action.actionPerformed(e);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 717, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 594, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    public boolean isWizardStepValid()
    {
        return true;
    }

    public void setDBGeneratorDefinition(DBGeneratorDefinition dbGeneratorDefinition)
    {
        this.dbGeneratorDefinition = dbGeneratorDefinition;
        this.alDBTableGenerator = dbGeneratorDefinition.getDBTableGenerators();
        insertTables();
    }

    public DBGeneratorDefinition getDBGeneratorDefinition()
    {
        this.dbGeneratorDefinition.setDBTableGenerators(alDBTableGenerator);

        return dbGeneratorDefinition;
    }


    //called by the wizard step when caller sets the DBDefinition
    private void insertTables()
    {
        Dimension d;
        DBTableGenerator dbTableGen;

        ArrayList<DefaultGraphCell> cells = new ArrayList();
        DefaultGraphCell cellFirst = null;

        //insert the tables, but only the ones that do not exist in the graph
        for (int i = 0; i < alDBTableGenerator.size(); i++)
        {
            dbTableGen = alDBTableGenerator.get(i);
            d = getDimensions(null, dbTableGen);
            DefaultGraphCell cell = new DBGraphCell(dbTableGen);

            createVertex(cell, 100 + (i * 10), 100 + (i * 10), d.getWidth(), d.getHeight());
            logger.debug("Inserted:" + dbTableGen.getName());
            if (hmDBTableGenerator.containsKey(dbTableGen.getName()) == false)
            {
                cells.add(cell);
                hmDBTableGenerator.put(dbTableGen.getName(), dbTableGen);
            }
        }
        if (cells.size() > 0)
        {
            //graph.getGraphLayoutCache().insert(cells.toArray());
            graph.getModel().insert(cells.toArray(), null, null, null, null);
        }
    }

//public void insert(Object[] arg0, Map arg1, ConnectionSet arg2, ParentMap arg3, UndoableEdit[] arg4)
    private DBCardinalityGenerator createCardinality(DBTableGenerator fromTable, DBTableGenerator toTable)
    {
        DBCardinalityPanel pnlCardinality = new DBCardinalityPanel();
        JDialog dlg = new JDialog();
        int width, height, posX, posY;

        width = this.getWidth();
        height = this.getHeight();

        DBCardinalityGenerator dbCardinality = new DBCardinalityGenerator(fromTable, toTable);
        pnlCardinality.setCardinality(dbCardinality);

        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.getContentPane().add(pnlCardinality, BorderLayout.CENTER);
        dlg.setLocationRelativeTo(this);

        dlg.setTitle("Cardinality Editor: ");// + dbTable.getName());
        dlg.pack();

        posX = this.getTopLevelAncestor().getX() + (width - dlg.getWidth()) / 2;
        posY = this.getTopLevelAncestor().getY() + (height - dlg.getHeight()) / 3;


        dlg.setLocation(posX, posY);
        dlg.setResizable(true);
        dlg.setModal(true);
        dlg.setVisible(true);

        return dbCardinality;
    }

    /**
     * returns the type of the selected object in the graph
     * 1: Selected object is of type DBTableGenerator
     * 2: DBCardinalityGenerator
     * 0: Something else!
     */
    private int getSelectedObjectType()
    {
        Object oSelectedObject;

        if (!graph.isSelectionEmpty())
        {
            oSelectedObject = ((DefaultGraphCell) graph.getSelectionCell()).getUserObject();

            if (oSelectedObject != null)
            {
                if (oSelectedObject instanceof DBTableGenerator)
                {
                    return 1;
                }
                if (oSelectedObject instanceof DBCardinalityGenerator)
                {
                    return 2;
                }
            }
        }
        return 0;
    }

    // MarqueeHandler that Connects Vertices and Displays PopupMenus
    private class MyMarqueeHandler extends BasicMarqueeHandler
    {

        // Holds the Start and the Current Point
        protected Point2D start,  current;
        // Holds the First and the Current Port
        protected PortView port,  firstPort;

        // Override to Gain Control (for PopupMenu and ConnectMode)
        public boolean isForceMarqueeEvent(MouseEvent e)
        {
            if (e.isShiftDown())
            {
                return false;
            }

            // If Right Mouse Button we want to Display the PopupMenu
            if (SwingUtilities.isRightMouseButton(e))
            {
                // Return Immediately
                return true;
            }
            // Find and Remember Port
            port = getSourcePortAt(e.getPoint());
            // If Port Found and in ConnectMode (=Ports Visible)
            if (port != null && graph.isPortsVisible())
            {
                return true;
            }
            // Else Call Superclass
            return super.isForceMarqueeEvent(e);
        }


        // Display PopupMenu or Remember Start Location and First Port
        public void mousePressed(final MouseEvent e)
        {
            int selectedObjectType = getSelectedObjectType();

            logger.debug("Selected object is of type: " + selectedObjectType);

            // If Right Mouse Button, we need the pop up
            if (SwingUtilities.isRightMouseButton(e))
            {
                // Find Cell in Model Coordinates
                Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());
                if (selectedObjectType > 0)
                {
                    DefaultGraphCell myCell = (DefaultGraphCell) graph.getSelectionModel().getSelectionCell();
                    // Display PopupMenu

                    popupDB.setSelectedInfo(getSelectedObjectType(), myCell.getUserObject());
                    popupDB.getMenu().show(graph, e.getX(), e.getY());

                    graph.getSelectionModel().setSelectionCell(myCell);
                }

            // Else if in ConnectMode and Remembered Port is Valid
            } else if (port != null && graph.isPortsVisible())
            {
                // Remember Start Location
                start = graph.toScreen(port.getLocation());
                // Remember First Port
                firstPort = port;
            } else
            {
                // Call Superclass
                super.mousePressed(e);
            }
        }

        // Find Port under Mouse and Repaint Connector
        public void mouseDragged(MouseEvent e)
        {
            // If remembered Start Point is Valid
            if (start != null)
            {
                // Fetch Graphics from Graph
                Graphics g = graph.getGraphics();
                // Reset Remembered Port
                PortView newPort = getTargetPortAt(e.getPoint());
                // Do not flicker (repaint only on real changes)
                if (newPort == null || newPort != port)
                {
                    // Xor-Paint the old Connector (Hide old Connector)
                    paintConnector(Color.black, graph.getBackground(), g);
                    // If Port was found then Point to Port Location
                    port = newPort;
                    if (port != null)
                    {
                        current = graph.toScreen(port.getLocation());
                    } // Else If no Port was found then Point to Mouse Location
                    else
                    {
                        current = graph.snap(e.getPoint());
                    }
                    // Xor-Paint the new Connector
                    paintConnector(graph.getBackground(), Color.black, g);
                }
            }
            // Call Superclass
            super.mouseDragged(e);
        }

        public PortView getSourcePortAt(Point2D point)
        {
            // Disable jumping
            graph.setJumpToDefaultPort(false);
            PortView result;
            try
            {
                // Find a Port View in Model Coordinates and Remember
                result = graph.getPortViewAt(point.getX(), point.getY());
            } finally
            {
                graph.setJumpToDefaultPort(true);
            }
            return result;
        }

        // Find a Cell at point and Return its first Port as a PortView
        protected PortView getTargetPortAt(Point2D point)
        {
            // Find a Port View in Model Coordinates and Remember
            return graph.getPortViewAt(point.getX(), point.getY());
        }

        // Connect the First Port and the Current Port in the Graph or Repaint
        public void mouseReleased(MouseEvent e)
        {
            // If Valid Event, Current and First Port
            if (e != null && port != null && firstPort != null && firstPort != port)
            {
                // Then Establish Connection
                logger.debug("About to connect (Source, target): " + firstPort + ", " + port);
                if (firstPort.getParentView().getCell() instanceof DBGraphCell &&
                        port.getParentView().getCell() instanceof DBGraphCell)
                {
                    DBTableGenerator fromTable = (DBTableGenerator) ((DBGraphCell) firstPort.getParentView().getCell()).getUserObject();
                    DBTableGenerator toTable = (DBTableGenerator) ((DBGraphCell) port.getParentView().getCell()).getUserObject();

                    logger.debug("Connecting: " + fromTable.getName() + ", " + toTable.getName());
                    DBCardinalityGenerator dbCardinality = createCardinality(fromTable, toTable);

                    connect((Port) firstPort.getCell(), (Port) port.getCell(), dbCardinality);
                }
                e.consume();
            // Else Repaint the Graph
            } else
            {
                graph.repaint();
            }
            // Reset Global Vars
            firstPort = port = null;
            start = current = null;
            // Call Superclass
            super.mouseReleased(e);
        }

        // Show Special Cursor if Over Port
        public void mouseMoved(MouseEvent e)
        {
            // Check Mode and Find Port
            if (e != null && getSourcePortAt(e.getPoint()) != null && graph.isPortsVisible())
            {
                // Set Cusor on Graph (Automatically Reset)
                graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
                // Consume Event
                // Note: This is to signal the BasicGraphUI's
                // MouseHandle to stop further event processing.
                e.consume();
            } else
            // Call Superclass
            {
                super.mouseMoved(e);
            }
        }

        // Use Xor-Mode on Graphics to Paint Connector
        protected void paintConnector(Color fg, Color bg, Graphics g)
        {
            // Set Foreground
            g.setColor(fg);
            // Set Xor-Mode Color
            g.setXORMode(bg);
            // Highlight the Current Port
            paintPort(graph.getGraphics());
            // If Valid First Port, Start and Current Point
            if (firstPort != null && start != null && current != null)
            // Then Draw A Line From Start to Current Point
            {
                g.drawLine((int) start.getX(), (int) start.getY(),
                        (int) current.getX(), (int) current.getY());
            }
        }

        // Use the Preview Flag to Draw a Highlighted Port
        protected void paintPort(Graphics g)
        {
            // If Current Port is Valid
            if (port != null)
            {
                // If Not Floating Port...
                boolean o = (GraphConstants.getOffset(port.getAllAttributes()) != null);
                // ...Then use Parent's Bounds
                Rectangle2D r = (o) ? port.getBounds() : port.getParentView().getBounds();
                // Scale from Model to Screen
                r = graph.toScreen((Rectangle2D) r.clone());
                // Add Space For the Highlight Border
                r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6);
                // Paint Port in Preview (=Highlight) Mode
                graph.getUI().paintCell(g, port, r, true);
            }
        }
    } // End of Editor.MyMarqueeHandler
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
