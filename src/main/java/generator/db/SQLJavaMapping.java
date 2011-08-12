/*
 * SQLJavaMapping.java
 *
 * Created on 08 June 2007, 17:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.db;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mmichag
 */
public class SQLJavaMapping
{
    private List<String> alSQLType;
    private int javaType;
    
    public SQLJavaMapping()
    {
        alSQLType  = new ArrayList();
        javaType = 0;
    }
    
    public void addSQLType(String sqlType)
    {
        alSQLType.add(sqlType);
    }
    
    public void setJavaType(int javaType)
    {
        this.javaType = javaType;
    }
    
    public boolean isSQLType(String sqlType)
    {
        boolean found = false;
        
        for(String comp: alSQLType)
        {
            if( sqlType.equalsIgnoreCase(comp) )
                found = true;
        }
        return found;
    }
    
    public int getJavaType()
    {
        return javaType;
    }
}
