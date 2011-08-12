/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


package generator.panels;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;
import generator.extenders.RandomiserPanel;
import generator.extenders.IGeneratorPanel;
import generator.extenders.RandomiserInstance;


public class PanelListItemRandomiser  extends RandomiserPanel
{
    Logger logger = Logger.getLogger(PanelListItemRandomiser.class);
    /** Creates new form PanelDoubleGenerator */
    
    
    /** Creates new form PanelListItemGenerator */
    public PanelListItemRandomiser()
    {
        initComponents();
        radFile.setSelected(true);        
        newModel.addColumn("Item");
        newModel.addColumn("Percent.");
        
        tblItems.setModel(newModel);
        enableList(false);
        loadButtonImages();
    }
    
    private void loadButtonImages() {
        URL urlAdd = this.getClass().getClassLoader().getResource("resources/images/list-add-small.png");
        URL urlRemSelect = this.getClass().getClassLoader().getResource("resources/images/list-remove-small.png");
        URL urlBrowse = this.getClass().getClassLoader().getResource("resources/images/document-open-small.png");
        
        btnAddItem.setIcon(new ImageIcon(urlAdd));
        btnRemove.setIcon(new ImageIcon(urlRemSelect));
        btnBrowse.setIcon(new ImageIcon(urlBrowse));
    }
   
    public boolean isFormValid()
    {
        String name;
        String fname, temp;
        int percent, ival;
        Integer intValue;
        boolean mustCheck;
        
        
        name = txtName.getText().trim();

        //run checks
        if(name.length()==0)
        {
            JOptionPane.showMessageDialog(this,"Please provide a value for the name.","Required field",JOptionPane.ERROR_MESSAGE); 
            return false;
        }        
        
        if(radFile.isSelected())
        {
            fname = txtFilename.getText().trim();
            if(fname.length()==0)
            {
                JOptionPane.showMessageDialog(this,"Please provide a value for the filename.","Required field",JOptionPane.ERROR_MESSAGE); 
                return false;
            }                    
        }
        if(radList.isSelected())
        {
            int listCount = newModel.getRowCount();
            if(listCount<2)
            {
                JOptionPane.showMessageDialog(this,"Please provide at least two items.","Required fields",JOptionPane.ERROR_MESSAGE); 
                return false;
            }                    
        }
        
        //check that there is no need for checking values
        mustCheck=false;
        for(int i = 0; i<newModel.getRowCount(); i++)
        {
            if( newModel.getValueAt(i,1)!=null )
                mustCheck=true;
        }
        
        if(mustCheck)
        {
            //retrieve the values from the table and make sure they are numbers
            //make sure that percentages add up to 100
            percent   = 0;
            for(int i = 0; i<newModel.getRowCount(); i++)
            {
                try
                {
                    temp = (String) newModel.getValueAt(i,1);
                    if(temp.trim().length()>0)
                    {
                        ival    = Integer.parseInt(temp);
                        percent+=ival;
                    }
                }
                catch(Exception e)
                {
                    JOptionPane.showMessageDialog(this,"If values in the table exist, they should all be numerical.","Invalid data",JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            if(percent!=0 && percent!=100)
            {
                JOptionPane.showMessageDialog(this,"Percentages in the table should add up to 100.","Invalid data",JOptionPane.ERROR_MESSAGE);
                return false;
            }        
        }
        return true;
    }
    
    public RandomiserInstance getRandomiserInstance()
    {
        LinkedHashMap hashmap = new LinkedHashMap();
        RandomiserInstance ri = new RandomiserInstance();
        int rowCount, ival;
        String sIntValue;
        String name, description, fname, item;
        
        //get field values
        name = txtName.getText().trim();
        description = txtDescription.getText().trim();
        ri.setName(name);
        ri.setDescription(description);
        ri.setRandomiserType("ListitemsGenerator");
        
        if(radFile.isSelected())
        {
            fname = txtFilename.getText().trim();
            hashmap.put("inputSource","file");
            hashmap.put("inputFile", fname);
        }
        else
        {
            hashmap.put("inputSource","list");
            rowCount = tblItems.getRowCount();
            hashmap.put("rangesNum",""+rowCount);            

            for(int i = 0; i<rowCount; i++)
            {
                try
                {
                    item      = (String)newModel.getValueAt(i,0);
                    sIntValue = (String) newModel.getValueAt(i,1);
                    if(sIntValue.trim().length()>0)
                        ival = Integer.parseInt(sIntValue);
                    else
                        ival = -1;
                    hashmap.put("itemField"+i,""+ item );
                    hashmap.put("percentField"+i,""+ ival );
                }
                catch(Exception e)
                {
                    logger.error("Problem retrieving table values",e);
                }
            }//for
        }
        hashmap.put("nullField",""+spinNull.getValue());
        ri.setProperties(hashmap);                                
        return ri;        
    }
    
    public void initialise(RandomiserInstance ri)
    {
        HashMap hashmap;
        String inputSource, sFilename, sMax, sIntValue, sItem, sNull;
        int    iMax;
        Integer intValue;
        Object  data[];
        
        txtName.setText(ri.getName());
        txtDescription.setText(ri.getDescription());
        
        hashmap     = ri.getProperties();
        inputSource = (String)hashmap.get("inputSource");
        if( inputSource.equalsIgnoreCase("file") )
        {
            sFilename = (String)hashmap.get("inputFile");
            txtFilename.setText(sFilename);
            enableList(false);
        }
        else
        {
            enableList(true);
            data = new Object[2];
            radList.setSelected(true);
            sMax = (String)hashmap.get("rangesNum");
            try
            {
                iMax = Integer.parseInt(sMax);
                for(int i=0; i<iMax; i++)
                {
                    sItem =(String)hashmap.get("itemField"+i);
                    sIntValue =(String)hashmap.get("percentField"+i);
                    intValue  = new Integer(sIntValue);                    
                    data[0] = sItem;
                    if(intValue.intValue()==-1)
                        data[1] = "";
                    else
                        data[1] = intValue;
                    newModel.addRow(data);
                }
                sNull  = (String) hashmap.get("nullField");
                spinNull.setValue(Integer.parseInt(sNull));
            }
            catch(Exception e)
            {
                logger.error("Error while setting properties:",e);
            }        
        }
    }
    
    private DefaultTableModel newModel = new DefaultTableModel();  
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFilename = new javax.swing.JTextField();
        radFile = new javax.swing.JRadioButton();
        radList = new javax.swing.JRadioButton();
        btnBrowse = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtItem = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtPercent = new javax.swing.JTextField();
        btnAddItem = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblItems = new javax.swing.JTable();
        btnRemove = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        spinNull = new javax.swing.JSpinner();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Case description"));
        jLabel4.setText("Name:");

        jLabel5.setText("Description:");

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Tahoma", 0, 11));
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        jScrollPane1.setViewportView(txtDescription);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 207, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Percentages of generated values"));
        jLabel1.setText("List data:");

        buttonGroup1.add(radFile);
        radFile.setText("Use file");
        radFile.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radFile.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radFile.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                radFileActionPerformed(evt);
            }
        });

        buttonGroup1.add(radList);
        radList.setText("Use list");
        radList.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radList.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radList.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                radListActionPerformed(evt);
            }
        });

        btnBrowse.setIcon(new javax.swing.ImageIcon("C:\\javaprojects\\GenGUI\\images\\document-open-small.png"));
        btnBrowse.setText("Browse...");
        btnBrowse.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnBrowseActionPerformed(evt);
            }
        });

        jLabel6.setText("New Item:");

        jLabel7.setText("Percent.(optional):");

        btnAddItem.setIcon(new javax.swing.ImageIcon("C:\\javaprojects\\GenGUI\\images\\list-add-small.png"));
        btnAddItem.setText("Add item");
        btnAddItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnAddItemActionPerformed(evt);
            }
        });

        tblItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblItems);

        btnRemove.setIcon(new javax.swing.ImageIcon("C:\\javaprojects\\GenGUI\\images\\list-remove-small.png"));
        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnRemoveActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtItem, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(txtPercent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnAddItem)
                                .add(111, 111, 111))
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btnRemove))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(txtPercent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnAddItem))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnRemove)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("Null:");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(radFile)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtFilename, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                    .add(radList))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnBrowse))
            .add(jPanel1Layout.createSequentialGroup()
                .add(34, 34, 34)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(spinNull, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(165, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(radFile)
                    .add(txtFilename, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnBrowse))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(radList)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(spinNull, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnRemoveActionPerformed
    {//GEN-HEADEREND:event_btnRemoveActionPerformed
        int row = tblItems.getSelectedRow();
        if(row==-1)
            return;
        newModel.removeRow(row);
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnAddItemActionPerformed
    {//GEN-HEADEREND:event_btnAddItemActionPerformed
        Object  data[];        
        String  sItem, sPercent;
        Integer iValue= Integer.valueOf(-1);
        int error=0;
        
        sItem    = txtItem.getText();
        sPercent = txtPercent.getText();                        
        
        if(sPercent.trim().length()>0)
        {
            try
            {iValue = new Integer(sPercent);}
            catch(Exception e)
            {error=1;}

            if(error>0)
            {
                JOptionPane.showMessageDialog(this,"Value for the field percentage should be numeric.","Invalid data",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(iValue.intValue()<=0)
            {
                JOptionPane.showMessageDialog(this,"Percentage field should be a positive integer.","Invalid data",JOptionPane.ERROR_MESSAGE);
                return;
            }             
        }
        data = new Object[2];
        data[0] = sItem;
        data[1] = sPercent;
        
        newModel.addRow(data);
    }//GEN-LAST:event_btnAddItemActionPerformed
    
    
    
    private void enableList(boolean status)
    {
        tblItems.setEnabled(status);
        btnRemove.setEnabled(status);
        btnAddItem.setEnabled(status);
        txtItem.setEnabled(status);
        txtPercent.setEnabled(status);
        
        txtFilename.setEnabled(!status);
        btnBrowse.setEnabled(!status);
    }
    
    private void radListActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_radListActionPerformed
    {//GEN-HEADEREND:event_radListActionPerformed
        enableList(true);
    }//GEN-LAST:event_radListActionPerformed
    
    private void radFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_radFileActionPerformed
    {//GEN-HEADEREND:event_radFileActionPerformed
        enableList(false);
    }//GEN-LAST:event_radFileActionPerformed
    
    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnBrowseActionPerformed
    {//GEN-HEADEREND:event_btnBrowseActionPerformed
        String inputFile;
        
        JFileChooser chooser = new JFileChooser();
        ListFilter filter = new ListFilter();
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {            
            inputFile = chooser.getSelectedFile().getPath();
            txtFilename.setText(inputFile);
        }
    }//GEN-LAST:event_btnBrowseActionPerformed
    
  
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddItem;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnRemove;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton radFile;
    private javax.swing.JRadioButton radList;
    private javax.swing.JSpinner spinNull;
    private javax.swing.JTable tblItems;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtFilename;
    private javax.swing.JTextField txtItem;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPercent;
    // End of variables declaration//GEN-END:variables
    
}

/** Filter to work with JFileChooser to select java file types. **/
class ListFilter extends javax.swing.filechooser.FileFilter
{
    public boolean accept(File f)
    {
        return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
    }
    
    public String getDescription()
    {
        return "Text files";
    }
} // class J