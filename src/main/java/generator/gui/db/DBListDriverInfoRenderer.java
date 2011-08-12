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


class DBListDriverInfoRenderer extends JLabel implements ListCellRenderer
{
    ImageIcon iconOK;
    ImageIcon iconError;
    Utils utils;
    
    public DBListDriverInfoRenderer()
    {
        utils = new Utils();
        iconOK =  utils.createImageIcon("resources/images/no-error.png");
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
        DBDriverInfo dbInfo = (DBDriverInfo) value;
        
        setText(dbInfo.toString());
        
        if(dbInfo.getStatus()==Constants.DRIVER_OK)
            setIcon(iconOK);
        else if(dbInfo.getStatus()==Constants.DRIVER_NOT_OK)
            setIcon(iconError);
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
