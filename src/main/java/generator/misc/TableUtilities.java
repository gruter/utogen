package generator.misc;


import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class TableUtilities
{
    // Calculate the required width of a table column
    public static int calculateColumnWidth(JTable table,int columnIndex)
    {
        int width = 0;      // The return value
        int rowCount = table.getRowCount();
        
        for (int i = 0; i < rowCount ; i++)
        {
            TableCellRenderer renderer = table.getCellRenderer(i, columnIndex);
            Component comp = renderer.getTableCellRendererComponent(table, table.getValueAt(i, columnIndex),false, false, i, columnIndex);
            int thisWidth = comp.getPreferredSize().width;
            if (thisWidth > width)
            {
                width = thisWidth;
            }
        }
        
        return width;
    }
    
    // Set the widths of every column in a table
    public static void setColumnWidths(JTable table, Insets insets,
            boolean setMinimum,
            boolean setMaximum)
    {
        int columnCount = table.getColumnCount();
        TableColumnModel tcm = table.getColumnModel();
        int spare = (insets == null ? 0 : insets.left + insets.right);
        
        for (int i = 0; i < columnCount; i++)
        {
            int width = calculateColumnWidth(table, i);
            width += spare;
            
            TableColumn column = tcm.getColumn(i);
            column.setPreferredWidth(width);
            if (setMinimum == true)
            {
                column.setMinWidth(width);
            }
            if (setMaximum == true)
            {
                column.setMaxWidth(width);
            }
        }
    }
    
    // Sort an array of integers in place
    public static void sort(int[] values)
    {
        int length = values.length;
        if (length > 1)
        {
            for (int i = 0; i < length - 1 ; i++)
            {
                for (int j = i + 1; j < length; j++)
                {
                    if (values[j] < values[i])
                    {
                        int temp = values[i];
                        values[i] = values[j];
                        values[j] = temp;
                    }
                }
            }
        }
    }
}