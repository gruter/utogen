/*
 * DBPopup.java
 *
 * Created on 08 June 2007, 18:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package generator.gui.graph;

import generator.db.DBTableGenerator;
import generator.gui.TableAssignGeneratorPanel;
import generator.gui.db.DBCardinalityPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.jgraph.graph.Port;

/**
 *
 * @author mmichag
 */
/**
 * Handles the pop-up menu when the user right clicks on some element on the graph
 * Depending on the selected menu, different options are enabled
 */
public class DBPopup
{

    MyActionListener myActionListener;
    JPopupMenu menu = new JPopupMenu();
    JMenuItem mnuView;
    JMenuItem mnuEdit;
    JMenuItem mnuDel;
    DBWizardEREditor dbEditor;
    int selectedObjectType;
    Object selectedObject;

    //constructor
    public DBPopup(DBWizardEREditor dbWizardEditor)
    {
        this.dbEditor = dbWizardEditor;    //first this
        myActionListener = new MyActionListener(); //...and then this, we need the dbEditor reference.

        mnuView = new JMenuItem("View", KeyEvent.VK_V);
        mnuView.addActionListener(myActionListener);

        mnuEdit = new JMenuItem("Edit", KeyEvent.VK_E);
        mnuEdit.addActionListener(myActionListener);

        mnuDel = new JMenuItem("Delete", KeyEvent.VK_D);
        mnuDel.addActionListener(myActionListener);

        menu.add(mnuView);
        menu.add(mnuEdit);
        menu.addSeparator();
        menu.add(mnuDel);
    }

    public JPopupMenu getMenu()
    {
        return menu;
    }

    public void showGeneratorAssigner(DBTableGenerator dbTable)
    {
        TableAssignGeneratorPanel pnlGeneratorAssign = new TableAssignGeneratorPanel();
        JDialog dlg = new JDialog();
        int width, height, posX, posY;

        width = dbEditor.getWidth();
        height = dbEditor.getHeight();

        pnlGeneratorAssign.setTableInfo(dbTable);

        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.getContentPane().add(pnlGeneratorAssign, BorderLayout.CENTER);
        dlg.setLocationRelativeTo(dbEditor);

        dlg.setTitle("Table generator: " + dbTable.getName());
        dlg.pack();

        posX = dbEditor.getTopLevelAncestor().getX() + (width - dlg.getWidth()) / 2;
        posY = dbEditor.getTopLevelAncestor().getY() + (height - dlg.getHeight()) / 3;


        dlg.setLocation(posX, posY);
        dlg.setResizable(true);
        dlg.setModal(true);
        dlg.setVisible(true);

        System.out.println("Values have been assigned");
        for (int i = 0; i < dbTable.getNumFields(); i++)
        {
            System.out.println(dbTable.fieldNameAt(i) + " ->" + dbTable.getGenerator(i));
        }
    //dbEditor.updateSelectedTable(dbTable);
    }

    public void showCardinalityEditor(DBCardinalityGenerator oCardinality)
    {
        DBCardinalityPanel pnlCardinality = new DBCardinalityPanel();
        JDialog dlg = new JDialog();
        int width, height, posX, posY;

        width = dbEditor.getWidth();
        height = dbEditor.getHeight();

        pnlCardinality.setCardinality(oCardinality);

        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.getContentPane().add(pnlCardinality, BorderLayout.CENTER);
        dlg.setLocationRelativeTo(dbEditor);

        dlg.setTitle("Cardinality Editor: ");// + dbTable.getName());
        dlg.pack();

        posX = dbEditor.getTopLevelAncestor().getX() + (width - dlg.getWidth()) / 2;
        posY = dbEditor.getTopLevelAncestor().getY() + (height - dlg.getHeight()) / 3;


        dlg.setLocation(posX, posY);
        dlg.setResizable(true);
        dlg.setModal(true);
        dlg.setVisible(true);
    }

    public void setSelectedInfo(int objType, Object o)
    {
        this.selectedObjectType = objType;
        this.selectedObject = o;
    }

    class MyActionListener implements ActionListener
    {
        //need a reference to the common functions,
        //handles deletion

        public void actionPerformed(ActionEvent e)
        {
            String cmd = e.getActionCommand();

            if (cmd.equalsIgnoreCase("View"))
            {
//                if (selectedObjType == 1)
//                {
//                    dbEditor.editDomainDialog.setTitle("View Domain");
//                    dbEditor.editDomainDialog.getEditDomain().setModeView(true);
//                    dbEditor.editDomainDialog.show();
//                } else if (selectedObjType == 2)
//                {
//                    dbEditor.editConduitDialog.setTitle("View Conduit");
//                    dbEditor.editConduitDialog.getPnlEditConduit().setModeView(true);
//                    dbEditor.editConduitDialog.show();
//                }
            }


            if (cmd.equalsIgnoreCase("Edit"))
            {
                //...get the selected object, this should be the selected Table
                if (selectedObjectType == 1)
                {
                    DBTableGenerator oSelectedTable = (DBTableGenerator) selectedObject;
                    showGeneratorAssigner(oSelectedTable);
                } else if (selectedObjectType == 2)
                {
                    DBCardinalityGenerator oSelectedCardinality = (DBCardinalityGenerator) selectedObject;
                    showCardinalityEditor(oSelectedCardinality);
                    Object source = dbEditor.getPort(oSelectedCardinality.fromTable);
                    Object target = dbEditor.getPort(oSelectedCardinality.toTable);
                    dbEditor.connect((Port) source, (Port) target, oSelectedCardinality);
                }
            }


            if (cmd.equalsIgnoreCase("Delete"))
            {
                int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected object?", "Question", JOptionPane.YES_NO_OPTION);
                if (answer == 0)
                {
                    Object[] cells = dbEditor.getSelectedCells();
                    cells = dbEditor.getDescendants(cells);
                    for (int i = 0; i < cells.length; i++)
                    {
                        if (cells[i] instanceof DBGraphCell)
                        {
                            Object obj = ((DBGraphCell) cells[i]).getUserObject();
                            if (obj instanceof DBTableGenerator)
                            {
                                dbEditor.removeTableGenerator(((DBTableGenerator) obj).getName());
                            }
                            if (obj instanceof DBCardinalityGenerator)
                            {
                                DBCardinalityGenerator dbCE = (DBCardinalityGenerator) obj;
                                DBTableGenerator dbT = dbCE.getToTable();
                                dbT.clearForeignKeys();
                            }
                        }
                    }
                    dbEditor.removeCells(cells);
                }
            }//delete
        }
    }//MyButtonListener
}
