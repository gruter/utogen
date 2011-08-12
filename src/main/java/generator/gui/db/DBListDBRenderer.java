/*
 * DBListRenderer.java
 *
 * Created on 26 May 2007, 02:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.gui.db;

import generator.gui.*;
import generator.misc.Constants;
import generator.misc.DBDriverInfo;
import generator.misc.Utils;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.w3c.dom.ls.LSException;


class DBListDBRenderer extends JLabel implements ListCellRenderer
{
    ImageIcon iconDB;
    ImageIcon iconError;
    Utils utils;
    
    public DBListDBRenderer()
    {
        utils = new Utils();
        iconDB =  utils.createImageIcon("resources/images/database.png");
        iconError =  utils.createImageIcon("resources/images/error.png");
    }
    
    // This is the only method defined by ListCellRenderer.
    // We just reconfigure the JLabel each time we're called.
    public Component getListCellRendererComponent(
            JList list,
            Object value,            // value to display
            int index,               // cell index
            boolean isSelected,      // is the cell selected
            boolean cellHasFocus)    // the list and the cell have the focus
    {
        String dbInfo = (String) value;
        
        setText(dbInfo);
        if(list.getModel().getSize()>0)        
            setIcon(iconDB);
        
        if (isSelected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
