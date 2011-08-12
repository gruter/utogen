/*
 * DBTable.java
 *
 * Created on 27 October 2006, 22:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.db;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * Represents a database table
 */
public class DBTable
{
    private ArrayList<DBField> alFields;
    private String name;
    
    public DBTable()
    {
        
    }
    
    public DBTable(String name, Collection fields)
    {
        this.name = name;
        this.alFields = (ArrayList) fields;
    }
    
    //name of the table
    public String getName()
    {
        return name;
    }
    
    //returns a DB Field
    public DBField getDBField(int i)
    {
        return alFields.get(i);
    }
    
    
    public int getNumFields()
    {
        return alFields.size();
    }
    

    public Object[] getFieldNames()
    {
        Object fieldNames[] = new Object[alFields.size()];

        for(int i=0; i<fieldNames.length; i++)
        {
            fieldNames[i] = fieldNameAt(i);
        }
        return fieldNames;
    }
    
    public String fieldNameAt(int i)
    {
        return alFields.get(i).getField();
    }
    
    
    public String fieldTypeAt(int i)
    {
        return alFields.get(i).getType();
    }

    
    public short genericFieldTypeAt(int i)
    {
        return alFields.get(i).getSql_type();
    }
    
    
    public int fieldSizeAt(int i)
    {
        return alFields.get(i).getSize();
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setDBFields(ArrayList alFields)
    {
        this.alFields = alFields;
    }
    
    public ArrayList<DBField> getDBFields()
    {
        return alFields;
    }
}
