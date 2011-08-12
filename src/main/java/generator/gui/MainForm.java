/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


/*
 * FrmMain.java
 *
 * Created on 26 January 2007, 00:20
 */

package generator.gui;

import generator.extenders.RandomiserInstance;
import generator.extenders.RandomiserPanel;
import generator.gui.db.DBDriversPanel;
import generator.gui.db.DBWizardContainerPanel;
import generator.misc.ApplicationContext;
import generator.misc.DBDriverInfo;
import generator.misc.DBFileDefinition;
import generator.misc.DataFileDefinition;
import generator.misc.RandomiserType;
import generator.misc.Utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.apache.log4j.Logger;

/**
 *
 * @author  Michael
 */
@SuppressWarnings("serial")
public class MainForm extends JFrame
{
    private Logger logger = Logger.getLogger(MainForm.class);
    
    //store randomiser types, instances and text file definitions
    Vector<RandomiserType> vRandomiserTypes;
    Vector<RandomiserInstance> vRandomiserInstances;
    Vector<DataFileDefinition> vDataFileDefinitions;
    Vector<DBFileDefinition> vDBFileDefinitions;
    Vector<DBDriverInfo> vDBDriversInfo;
    
    //all randomiser-tuning panels are RandomTypePanel
    RandomTypePanel pnlRandomTypeContainer;
    
    //A custom renderer for the tree on the left
    MyRenderer treeRenderer;
    
    MyButtonHandler buttonHandler;
    
    /** Creates new form FrmMain */
    public MainForm()
    {
        initComponents();
        
        logger.debug("LOADING RANDOMISER TYPES");
        ApplicationContext.getInstance().refreshRandomiserTypes();
        
        logger.debug("LOADING RANDOMISER INSTANCES");
        ApplicationContext.getInstance().refreshRandomiserInstances();
        
        logger.debug("LOADING FILE DEFINITIONS");
        ApplicationContext.getInstance().getInstance().refreshFileDefinitions();
        
        //logger.debug("LOADING DB DEFINITIONS");
        //ApplicationContext.getInstance().refreshDBDefinitions();
        
        //logger.debug("LOADING DRIVERS INFORMATION");
        //ApplicationContext.getInstance().refreshDriverInfo();
        //vDBDriversInfo = ApplicationContext.getInstance().getDriverInfo();


        //logger.debug("LOADING JAVA-SQL MAPPINGS");
        //ApplicationContext.getInstance().refreshSQLJavaMappings();
        
        buttonHandler = new MyButtonHandler();
        
        //set up the toolbar buttons: Help, Exit
        tlbMainToolbar.addSeparator();
        tlbMainToolbar.addSeparator();
        
        btnHelp = new JButton();
        btnHelp.setToolTipText("Displays the help contents");
        btnHelp.addActionListener(buttonHandler);
        tlbMainToolbar.add(btnHelp);
        
        btnExit = new JButton();
        btnExit.setToolTipText("Exits the application");
        btnExit.addActionListener(buttonHandler);
        tlbMainToolbar.add(btnExit);
        
        //loads the data from the xml files into the tree
        setTreeDataBrowser();
        
        //loads the icons for the form and buttons
        loadIconImages();
        
        //now that the tree is ready, create the renderer and asscociate it with the tree
        treeRenderer = new MyRenderer(vDataFileDefinitions);
        treeDataBrowser.setCellRenderer(treeRenderer);
    }
    
    
    class MyButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.toString().indexOf("door_out.png")>0)
                System.exit(0);
        }
    }
    
    /**
     * loads the icons for the main form, menus and buttons
     */
    private void loadIconImages()
    {
        URL urlIcon = this.getClass().getClassLoader().getResource("resources/images/generate-data-small.png");
        URL urlText = this.getClass().getClassLoader().getResource("resources/images/new-text-file-small.png");
        URL urlDB   = this.getClass().getClassLoader().getResource("resources/images/new-db-file-small.png");
        URL urlXML  = this.getClass().getClassLoader().getResource("resources/images/new-xml-small.png");
        URL urlRefresh = this.getClass().getClassLoader().getResource("resources/images/view-refresh.png");
        URL urlDrivers = this.getClass().getClassLoader().getResource("resources/images/database_connect.png");
        URL urlExit = this.getClass().getClassLoader().getResource("resources/images/door_out.png");
        URL urlHelp = this.getClass().getClassLoader().getResource("resources/images/help.png");
        
        Image img = Toolkit.getDefaultToolkit().getImage(urlIcon);
        setIconImage( img );
        
        btnTextfile.setIcon( new ImageIcon(urlText) );
        btnRefresh.setIcon( new ImageIcon(urlRefresh) );
        btnExit.setIcon( new ImageIcon(urlExit) );
        btnHelp.setIcon( new ImageIcon(urlHelp) );
        
        mnuDefineText.setIcon( new ImageIcon(urlText));
        mnuDefineDB.setIcon( new ImageIcon(urlDB) );
        mnuDefineXML.setIcon( new ImageIcon(urlXML) );
        mnuRefreshTree.setIcon( new ImageIcon(urlRefresh) );
        
        mnuDriversManager.setIcon( new ImageIcon(urlDrivers) );
        
        mnuTopicsHelp.setIcon( new ImageIcon(urlHelp));
        mnuAbout.setIcon( new ImageIcon(urlIcon));
        mnuExit.setIcon( new ImageIcon(urlExit));
        
    }
    
    
    
    /**
     * Loads the data from the xml files to the treeDataBrowser (left component).
     */
    private void setTreeDataBrowser()
    {
        TreeModel treeModel;
        
        //top node is dgMaster
        DefaultMutableTreeNode top =  new DefaultMutableTreeNode("dgMaster");
        
        //load the randomiser-type definitions from file
        vRandomiserTypes = ApplicationContext.getInstance().getRandomiserTypes();
        
        //...and now load the vector into the tree
        DefaultMutableTreeNode nodeRandomTypes = new DefaultMutableTreeNode("Built-in generators");
        loadTreeData(nodeRandomTypes, vRandomiserTypes);
        top.add(nodeRandomTypes);
        
        
        //load the randomiser-instance definitions from the file
        vRandomiserInstances = ApplicationContext.getInstance().getRandomiserInstances();
        DefaultMutableTreeNode nodeRandomInstances = new DefaultMutableTreeNode("My generators");
        loadTreeData(nodeRandomInstances,vRandomiserInstances);
        top.add(nodeRandomInstances);
        
        
        //load the text-file definitions
        vDataFileDefinitions = ApplicationContext.getInstance().getDFD();
        DefaultMutableTreeNode nodeFiles = new DefaultMutableTreeNode("Text files");
        top.add(nodeFiles);
        loadTreeData(nodeFiles, vDataFileDefinitions);
        
        
        //load the XML-file definitions [*] Pending implementation
        /*
        DefaultMutableTreeNode category3 = new DefaultMutableTreeNode("XML files");
        top.add(category3);
        */
        
        //load the DB-file definitions
        /*
        vDBFileDefinitions = ApplicationContext.getInstance().getDBD();
        DefaultMutableTreeNode nodeDBDefinitions = new DefaultMutableTreeNode("Databases");
        top.add(nodeDBDefinitions);
        loadTreeData(nodeDBDefinitions, vDBFileDefinitions);
        */
        
        //create a tree model and associate it with the tree data browser
        treeModel = new DefaultTreeModel(top);
        treeDataBrowser.setModel(treeModel);
    }
    
    
    /**
     * Loads the vector of data-types in a specific tree node.
     *
     * <p>Preconditions: a) category parameter is not null, b) vData parameter is not null.
     * <p>Post-effects:  Though this method does not return any obj. reference,
     *                   the category parameter is modified (it now new nodesnames).
     *
     * @param category a DefaultMutableTreeNode reference to which data will be appended
     * @param vData a Vector of Objects
     */
    public void loadTreeData(DefaultMutableTreeNode category, Vector<? extends Object> vData)
    {
        Object rt;
        
        for(int i=0; i<vData.size(); i++)
        {
            rt = vData.elementAt(i);
            DefaultMutableTreeNode nodeRT = new DefaultMutableTreeNode(rt);
            category.add(nodeRT);
        }
    }
    
    
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tlbMainToolbar = new javax.swing.JToolBar();
        btnRefresh = new javax.swing.JButton();
        btnTextfile = new javax.swing.JButton();
        spltPane = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeDataBrowser = new javax.swing.JTree();
        pnlHolder = new javax.swing.JPanel();
        mnuMenubar = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuExit = new javax.swing.JMenuItem();
        mnuRandomisers = new javax.swing.JMenu();
        mnuDefineText = new javax.swing.JMenuItem();
        mnuDefineXML = new javax.swing.JMenuItem();
        mnuDefineDB = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuRefreshTree = new javax.swing.JMenuItem();
        mnuTools = new javax.swing.JMenu();
        mnuDriversManager = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mnuTopicsHelp = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mnuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("dgMaster: Simple, open source, extensible Data Generator");

        tlbMainToolbar.setFloatable(false);
        tlbMainToolbar.setRollover(true);

        btnRefresh.setToolTipText("Refreshes the generators browser");
        btnRefresh.setFocusPainted(false);
        btnRefresh.setMaximumSize(new java.awt.Dimension(30, 30));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        tlbMainToolbar.add(btnRefresh);

        btnTextfile.setToolTipText("Defines a new text file ");
        btnTextfile.setFocusPainted(false);
        btnTextfile.setMaximumSize(new java.awt.Dimension(30, 30));
        btnTextfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTextfileActionPerformed(evt);
            }
        });
        tlbMainToolbar.add(btnTextfile);

        spltPane.setDividerLocation(145);
        spltPane.setOneTouchExpandable(true);

        jScrollPane1.setAutoscrolls(true);

        treeDataBrowser.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeDataBrowserValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(treeDataBrowser);

        spltPane.setLeftComponent(jScrollPane1);

        org.jdesktop.layout.GroupLayout pnlHolderLayout = new org.jdesktop.layout.GroupLayout(pnlHolder);
        pnlHolder.setLayout(pnlHolderLayout);
        pnlHolderLayout.setHorizontalGroup(
            pnlHolderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 679, Short.MAX_VALUE)
        );
        pnlHolderLayout.setVerticalGroup(
            pnlHolderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 636, Short.MAX_VALUE)
        );

        spltPane.setRightComponent(pnlHolder);

        mnuFile.setText("File");

        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuExit);
        mnuExit.getAccessibleContext().setAccessibleName("");

        mnuMenubar.add(mnuFile);

        mnuRandomisers.setText("Randomisers");

        mnuDefineText.setLabel("Define text file...");
        mnuDefineText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDefineTextActionPerformed(evt);
            }
        });
        mnuRandomisers.add(mnuDefineText);
        
        /*
        mnuDefineXML.setText("Define XML data...");
        mnuDefineXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDefineXMLActionPerformed(evt);
            }
        });
        mnuRandomisers.add(mnuDefineXML);

        mnuDefineDB.setLabel("Define database data...");
        mnuDefineDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDefineDBActionPerformed(evt);
            }
        });
        mnuRandomisers.add(mnuDefineDB);
        */
        
        mnuRandomisers.add(jSeparator3);

        mnuRefreshTree.setText("Refresh generators browser");
        mnuRefreshTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRefreshTreeActionPerformed(evt);
            }
        });
        mnuRandomisers.add(mnuRefreshTree);

        mnuMenubar.add(mnuRandomisers);
        
        /*
        mnuTools.setText("Tools");
        mnuTools.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuToolsActionPerformed(evt);
            }
        });

        mnuDriversManager.setMnemonic('D');
        mnuDriversManager.setText("Drivers manager...");
        mnuDriversManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDriversManagerActionPerformed(evt);
            }
        });
        mnuTools.add(mnuDriversManager);

        mnuMenubar.add(mnuTools);
        */
        
        mnuHelp.setText("Help");

        mnuTopicsHelp.setText("Help contents");
        mnuHelp.add(mnuTopicsHelp);
        mnuHelp.add(jSeparator1);

        mnuAbout.setText("About...");
        mnuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuAbout);

        mnuMenubar.add(mnuHelp);

        setJMenuBar(mnuMenubar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, spltPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, tlbMainToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(tlbMainToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spltPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                .addContainerGap())
        );

        this.setSize(1200, 900);
//        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void mnuDefineXMLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuDefineXMLActionPerformed
    {//GEN-HEADEREND:event_mnuDefineXMLActionPerformed
        notImplementedYet();
}//GEN-LAST:event_mnuDefineXMLActionPerformed

    private void notImplementedYet()
    {
        JDialog dlg = new JDialog();
        NotImplementedPanel pnlNotYet = new NotImplementedPanel();
        int width, height, posX, posY;
        
        width  = getWidth();
        height = getHeight();
        
        
        dlg.getContentPane().setLayout( new BorderLayout() );
        dlg.getContentPane().add(pnlNotYet, BorderLayout.CENTER);
        dlg.setLocationRelativeTo(this);
        
        dlg.setTitle("Working on it...");
        dlg.pack();
        
        posX = getX() + (width-dlg.getWidth())/2;
        posY = getY() + (height-dlg.getHeight())/3;
        
        dlg.setLocation(posX,posY);
        dlg.setResizable(false);
        dlg.setModal(true);
        dlg.setVisible(true);
    }
    
    
    private void mnuDefineDBActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuDefineDBActionPerformed
    {//GEN-HEADEREND:event_mnuDefineDBActionPerformed
        DBWizardContainerPanel dbPanel = new DBWizardContainerPanel();
        
        // this divider line moves to the left, so we need to reset it to its position
        int divLocation=spltPane.getDividerLocation();
        spltPane.setRightComponent(dbPanel);
        spltPane.setDividerLocation(divLocation);
        this.pack();
    }//GEN-LAST:event_mnuDefineDBActionPerformed
        
    private void mnuDriversManagerActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuDriversManagerActionPerformed
    {//GEN-HEADEREND:event_mnuDriversManagerActionPerformed
        URL urlDrivers = this.getClass().getClassLoader().getResource("resources/images/database_connect.png");
        Image img = Toolkit.getDefaultToolkit().getImage(urlDrivers);
        
        DBDriversPanel dbDriversPanel = new DBDriversPanel(vDBDriversInfo);
        JDialog dlg = new JDialog();
        int width, height, posX, posY;
        
        width  = getWidth();
        height = getHeight();
        
        dlg.setIconImage(img);
        dlg.getContentPane().setLayout( new BorderLayout() );
        dlg.getContentPane().add(dbDriversPanel, BorderLayout.CENTER);
        dlg.setLocationRelativeTo(this);
        
        dlg.setTitle("Drivers manager");
        dlg.pack();
        
        posX = getX() + (width-dlg.getWidth())/2;
        posY = getY() + (height-dlg.getHeight())/3;
        
        dlg.setLocation(posX,posY);
        dlg.setResizable(true);
        dlg.setModal(true);
        dlg.setVisible(true);
    }//GEN-LAST:event_mnuDriversManagerActionPerformed
    
    private void mnuToolsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuToolsActionPerformed
    {//GEN-HEADEREND:event_mnuToolsActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_mnuToolsActionPerformed
    
    private void mnuRefreshTreeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuRefreshTreeActionPerformed
    {//GEN-HEADEREND:event_mnuRefreshTreeActionPerformed
        treeDataBrowser.removeSelectionInterval(0, treeDataBrowser.getMaxSelectionRow());
        setTreeDataBrowser();
    }//GEN-LAST:event_mnuRefreshTreeActionPerformed
    
    private void mnuAboutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuAboutActionPerformed
    {//GEN-HEADEREND:event_mnuAboutActionPerformed
        URL urlIcon = this.getClass().getClassLoader().getResource("resources/images/generate-data-small.png");
        Image img = Toolkit.getDefaultToolkit().getImage(urlIcon);
        
        JDialog dlg = new JDialog();
        AboutPanel pnlAbout = new AboutPanel();
        int width, height, posX, posY;
        
        width  = getWidth();
        height = getHeight();
        
        
        dlg.setIconImage(img);
        dlg.getContentPane().setLayout( new BorderLayout() );
        dlg.getContentPane().add(pnlAbout, BorderLayout.CENTER);
        dlg.setLocationRelativeTo(this);
        
        dlg.setTitle("About dgMaster");
        dlg.pack();
        
        posX = getX() + (width-dlg.getWidth())/2;
        posY = getY() + (height-dlg.getHeight())/3;
        
        dlg.setLocation(posX,posY);
        dlg.setResizable(false);
        dlg.setModal(true);
        dlg.setVisible(true);
    }//GEN-LAST:event_mnuAboutActionPerformed
    
    
    /**
     * Creates the panel which allows the user to define the properties
     * of a text file that contains random data.
     * Called by the menu or the toolbar button (A user action).
     */
    private void defineFileDataDefinition()
    {
        //instantiate the panel, passing the vectors that contain all the application data
        FileOutputPanel fop = new FileOutputPanel();
        
        //pass this reference, so as to be able to refresh the tree
        fop.setMainForm(this);
        
        //remember the position of the divider line, when the right panel is set to fop
        // this divider line moves to the left, so we need to reset it to its position
        int divLocation=spltPane.getDividerLocation();
        spltPane.setRightComponent(fop);
        spltPane.setDividerLocation(divLocation);
    }
    
    
    
    private void mnuDefineTextActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuDefineTextActionPerformed
    {//GEN-HEADEREND:event_mnuDefineTextActionPerformed
        defineFileDataDefinition();
}//GEN-LAST:event_mnuDefineTextActionPerformed
    
    
    
    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnRefreshActionPerformed
    {//GEN-HEADEREND:event_btnRefreshActionPerformed
        treeDataBrowser.removeSelectionInterval(0, treeDataBrowser.getMaxSelectionRow());
        setTreeDataBrowser();
    }//GEN-LAST:event_btnRefreshActionPerformed
    
    public void refreshTree()
    {
        treeDataBrowser.removeSelectionInterval(0, treeDataBrowser.getMaxSelectionRow());
        setTreeDataBrowser();
    }
    
    private void btnTextfileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnTextfileActionPerformed
    {//GEN-HEADEREND:event_btnTextfileActionPerformed
        defineFileDataDefinition();
    }//GEN-LAST:event_btnTextfileActionPerformed
    
    
    
    /**
     * Updates the panel on the right, every time a different node on the tree data browser
     * is selected. This node can be:
     *              a randomiser-type (Built-in types),
     *              a randomiser-instance (My generator types),
     *              a data file definition
     * Each time a node is selected, the correct panel needs to be updated on the right
     * panel.
     *
     * @param evt a TreeSelectionEvent representing the event that triggerred the method call.
     */
    private void treeDataBrowserValueChanged(javax.swing.event.TreeSelectionEvent evt)//GEN-FIRST:event_treeDataBrowserValueChanged
    {//GEN-HEADEREND:event_treeDataBrowserValueChanged
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeDataBrowser.getLastSelectedPathComponent();
        boolean loaded;
        
        //we cannot do anything for the nodes that contain other nodes
        if (node == null)
        {
            logger.debug("Node is null");
            return;
        }
        
        //first check the randomiser-type nodes,
        //   then randomiserInstance,
        //   then FileOutputPanel,
        //[*] i can simply check the parent node and see where this node
        //    fits into, but I have chosen to do the explicit type-casting
        //    as in future versions, I want to allow the user to create his own
        //    nodes in the tree, then the parent node trick will not work.
        loaded = false;
        try
        {
            RandomiserType rt =(RandomiserType) node.getUserObject();
            loadRandomiserTypePanel(rt);
            loaded = true;
        }
        catch(Exception e)
        {
            //logger.warn("Selected node was not a randomiser-type");
        }
        
        if(!loaded)
        {
            try
            {
                RandomiserInstance ri =(RandomiserInstance) node.getUserObject();
                loadRandomiserInstancePanel(ri);
                loaded = true;
            }
            catch(Exception e)
            {
                //logger.warn("Selected node was not a randomiser-instance");
            }
        }
        
        if(!loaded)
        {
            try
            {
                DataFileDefinition dfd =(DataFileDefinition) node.getUserObject();
                loadFileOutputPanel(dfd);
                loaded = true;
            }
            catch(Exception e)
            {
                //logger.warn("Selected node was not a file text definition", e);
            }
        }
        
        if(!loaded)
        {
            try
            {
                DBFileDefinition dbd =(DBFileDefinition) node.getUserObject();
                loadDBDefinitionPanel(dbd);
                loaded = true;
            }
            catch(Exception e)
            {
                //logger.warn("Selected node was not a file text definition");
            }
        }
    }//GEN-LAST:event_treeDataBrowserValueChanged
    
    
    
    /**
     * Loads the specific randomiser panel in the right panel.
     * <p>Preconditions: vRandomiserTypes is not null
     * <p>Post-effects : The panelName is loaded and the user can enter
     *                   the required values, so as to fine tune the randomiser
     *
     * @param rt a randomiser-type instance.
     */
    private void loadRandomiserTypePanel(RandomiserType rt)
    {
        Utils utils = new Utils();
        
        String className = rt.getPanel();
        RandomiserPanel pnlRandomTypeUserPanel = (RandomiserPanel) utils.createObject(className);
        if(pnlRandomTypeUserPanel==null)
        {
            logger.error("Error loading panel:"+className);
            return;
        }
        //now we can load the panel in the right panel-container
        pnlRandomTypeContainer = new RandomTypePanel();
        pnlRandomTypeContainer.setUserPanel(pnlRandomTypeUserPanel);
        
        int divLocation=spltPane.getDividerLocation();
        spltPane.setRightComponent(pnlRandomTypeContainer);
        spltPane.setDividerLocation(divLocation);
    }
    
    
    /**
     * Loads the specific randomiser panel in the right panel.
     * This is the randomiser instance, that is, there are user-data
     * that need to be loaded in the panel
     * <p>Preconditions: vRandomiserTypes is not null
     *
     * <p>Post-effects : The panelName is loaded and the user can edit
     *                   the existing values, so as to fine tune the randomiser.
     *
     * @param ri a RandomiserInstnace.
     */
    private void loadRandomiserInstancePanel(RandomiserInstance ri)
    {
        RandomiserType rt=null;
        String className, type;
        RandomiserPanel pnlRandomTypeUserPanel;
        Utils utils = new Utils();
        
        boolean found =false;
        type = ri.getRandomiserType();
        
        //find the randomiser-type of this randomiser-instance
        for(int k=0; k<vRandomiserTypes.size(); k++)
        {
            rt = vRandomiserTypes.elementAt(k);
            if(rt.getName().equalsIgnoreCase(type))
            {
                className =rt.getGenerator();
                found = true;
                break;
            }
        }//for
        
        logger.debug("Found a panel with name? "+ ri.getName() + "---" + found);
        if(found)
        {
            className = rt.getPanel();
            pnlRandomTypeUserPanel = (RandomiserPanel) utils.createObject(className);
            if(pnlRandomTypeUserPanel==null)
            {
                logger.error("error loading panel:"+className);
                return;
            }
            //we need to load the user data
            pnlRandomTypeUserPanel.initialise(ri);
            //create the hosting panel and pass the vector of existing data
            pnlRandomTypeContainer = new RandomTypePanel();
            pnlRandomTypeContainer.setUserPanel(pnlRandomTypeUserPanel);
            int divLocation=spltPane.getDividerLocation();
            spltPane.setRightComponent(pnlRandomTypeContainer);
            spltPane.setDividerLocation(divLocation);
        }
    }
    
    
    /**
     * Loads the specific data file definition panel in the right panel.
     * This is the data file definition, that is, there are user-data
     * that need to be loaded in the panel
     * <p>Preconditions: a) vRandomiserTypes is not null,
     *                   b) vRandomiserInstances is not null,
     *                   c) vDataFileDefinitions is not null
     *
     * <p>Post-effects : The data file definition panel is loaded and the user can edit
     *                   the existing values, so as to define a new data file definition.
     *
     * @param dfd a DataFileDefinition.
     */
    private void loadFileOutputPanel(DataFileDefinition dfd)
    {
        FileOutputPanel fop = new FileOutputPanel();
        fop.setMainForm(this);
        fop.loadExistingDefinition(dfd.getName());
        int divLocation=spltPane.getDividerLocation();
        spltPane.setRightComponent(fop);
        spltPane.setDividerLocation(divLocation);
    }
    
    
    /**
     * Loads the specific data file definition panel in the right panel.
     * This is the data file definition, that is, there are user-data
     * that need to be loaded in the panel
     * <p>Preconditions: a) vRandomiserTypes is not null,
     *                   b) vRandomiserInstances is not null,
     *                   c) vDataFileDefinitions is not null
     *
     * <p>Post-effects : The data file definition panel is loaded and the user can edit
     *                   the existing values, so as to define a new data file definition.
     *
     * @param dfd a DataFileDefinition.
     */
    private void loadDBDefinitionPanel(DBFileDefinition dbd)
    {
//        DBScenarioLoadPanel dbp = new DBScenarioLoadPanel();
//        
//        dbp.setDBScenario( dbd );
//        int divLocation=spltPane.getDividerLocation();
//        spltPane.setRightComponent(dbp);
//        spltPane.setDividerLocation(divLocation);
    }    
    
    
    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuExitActionPerformed
    {//GEN-HEADEREND:event_mnuExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mnuExitActionPerformed
    
    
    private JButton btnExit;
    private JButton btnHelp;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTextfile;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JMenuItem mnuAbout;
    private javax.swing.JMenuItem mnuDefineDB;
    private javax.swing.JMenuItem mnuDefineText;
    private javax.swing.JMenuItem mnuDefineXML;
    private javax.swing.JMenuItem mnuDriversManager;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenuBar mnuMenubar;
    private javax.swing.JMenu mnuRandomisers;
    private javax.swing.JMenuItem mnuRefreshTree;
    private javax.swing.JMenu mnuTools;
    private javax.swing.JMenuItem mnuTopicsHelp;
    private javax.swing.JPanel pnlHolder;
    private javax.swing.JSplitPane spltPane;
    private javax.swing.JToolBar tlbMainToolbar;
    private javax.swing.JTree treeDataBrowser;
    // End of variables declaration//GEN-END:variables
    
}


class MyRenderer extends DefaultTreeCellRenderer
{
    Vector<RandomiserType> vRandomiserTypes;
    Vector<RandomiserInstance> vRandomiserInstances;
    Vector<DataFileDefinition> vDataFileDefinitions;
    
    Icon iconDFD, iconDBD, iconGear, iconGearEdit;
    Utils utils;
    
    
    public MyRenderer(Vector vFileDefs)
    {
        utils = new Utils();
        iconDFD =  utils.createImageIcon("resources/images/new-text-file-small.png");
        iconDBD =  utils.createImageIcon("resources/images/new-db-file-small.png");
        iconGear =  utils.createImageIcon("resources/images/gear.png");
        iconGearEdit =  utils.createImageIcon("resources/images/gear_edit.png");
        
        vDataFileDefinitions = vFileDefs;
    }
    
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus)
    {
        
        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);
        if (leaf && isTextDefinition(value))
        {
            setIcon(iconDFD);
        }
        else if(leaf && isDBDefinition(value))
        {
            setIcon(iconDBD);
        }
        else if(leaf && isRandomiserType(value))
        {
            setIcon(iconGear);
        }
        else if(leaf && isRandomiserInstance(value))
        {
            setIcon(iconGearEdit);
        }
        else
        {
            setToolTipText(null); //no tool tip
        }
        
        return this;
    }
    
    protected boolean isTextDefinition(Object value)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        Object nodeInfo = (Object)node.getUserObject();
        boolean found = false;
        if(nodeInfo instanceof DataFileDefinition)
            found=true;
        return found;
    }


    protected boolean isDBDefinition(Object value)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        Object nodeInfo = (Object)node.getUserObject();
        boolean found = false;
        if(nodeInfo instanceof DBFileDefinition)
            found=true;
        return found;
    }
    
    protected boolean isRandomiserType(Object value)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        Object nodeInfo = (Object)node.getUserObject();
        boolean found = false;
        if(nodeInfo instanceof RandomiserType)
            found=true;
        return found;
    }
    
    protected boolean isRandomiserInstance(Object value)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        Object nodeInfo = (Object)node.getUserObject();
        boolean found = false;
        if(nodeInfo instanceof RandomiserInstance)
            found=true;
        return found;
    }
}