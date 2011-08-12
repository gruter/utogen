/*
 * DBField.java
 *
 * Created on 27 October 2006, 22:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.db;

/**
 *
 * @author mmichag
 */
public class DBField
{
    private String field;
    private String type;
    private int    size;
    private short  sql_type;
    private boolean key;
    
    public DBField(String field, String type, int size, short sql_type, boolean key )
    {
        this.setField(field);
        this.setType(type);        
        this.setSize(size);
        this.setSql_type(sql_type);
        this.setKey(key);
    }    

    
    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public short getSql_type()
    {
        return sql_type;
    }

    public void setSql_type(short sql_type)
    {
        this.sql_type = sql_type;
    }

    public boolean isKey()
    {
        return key;
    }

    public void setKey(boolean key)
    {
        this.key = key;
    }
}
