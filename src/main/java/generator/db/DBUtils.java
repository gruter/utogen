/*
 * DBUtils.java
 *
 * Created on 08 June 2007, 01:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.db;

import generator.misc.ApplicationContext;
import generator.misc.RandomiserType;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Michael
 */
public class DBUtils
{
    private static Logger logger = Logger.getLogger("DBUtils");
    private static List<SQLJavaMapping> alSQLMappings;
    private static Map map;
    
    
    // This method returns the name of a JDBC type.
    // Returns null if jdbcType is not recognized.
    //http://www.exampledepot.com/egs/java.sql/JdbcInt2Str.html
    public static String getJdbcTypeName(int jdbcType)
    {
        // Use reflection to populate a map of int values to names
        if (map != null)
            return (String)map.get(new Integer(jdbcType));
        
        map = new HashMap();
        
        // Get all field in java.sql.Types
        Field[] fields = java.sql.Types.class.getFields();
        for (int i=0; i<fields.length; i++)
        {
            try
            {
                // Get field name and value
                String name = fields[i].getName();
                Integer value = (Integer)fields[i].get(null);
                // Add to map
                map.put(value, name);
            }
            catch (IllegalAccessException e)
            {
                logger.error("Could not retrieve value for field...");
            }
        }
        // Return the JDBC type name
        return (String)map.get( Integer.valueOf(jdbcType) );
    }
    
    
    public static void setupMappings()
    {
        if(alSQLMappings!=null)
            return;
        
        alSQLMappings = ApplicationContext.getInstance().getSQLJavaMappings();
    }
    
    
    private ArrayList<Integer> getAllTypes()
    {
        ArrayList alTypes = new ArrayList();
        
        alTypes.add(RandomiserType.TYPE_BIGDECIMAL);
        alTypes.add(RandomiserType.TYPE_BOOLEAN);
        alTypes.add(RandomiserType.TYPE_BYTE);
        alTypes.add(RandomiserType.TYPE_DATE);
        alTypes.add(RandomiserType.TYPE_DOUBLE);
        alTypes.add(RandomiserType.TYPE_FLOAT);
        alTypes.add(RandomiserType.TYPE_INTEGER);
        alTypes.add(RandomiserType.TYPE_LONG);
        alTypes.add(RandomiserType.TYPE_SHORT);
        alTypes.add(RandomiserType.TYPE_STRING);
        alTypes.add(RandomiserType.TYPE_TIME);
        alTypes.add(RandomiserType.TYPE_TIMESTAMP);
        
        return alTypes;
    }
    
    //retrieves the compatible types
    public ArrayList getCompatibleJavaTypes(String genericSQLType)
    {        
        setupMappings();
        
        ArrayList<Integer> alTypes = new ArrayList();
        
        logger.debug("Searching for sql type: " + genericSQLType);
        for(SQLJavaMapping sqlMap: alSQLMappings)
        {
            if( sqlMap.isSQLType(genericSQLType) )
            {
                logger.debug("---Found a compatible java type for " + genericSQLType + ", adding type " + sqlMap.getJavaType());
                alTypes.add(sqlMap.getJavaType() );
            }
        }
        if( alTypes.size()==0 )
        {
            //nothing was found, let the user decide
            logger.fatal("Could not find an appropriate sql mapping for :" + genericSQLType);
            alTypes = getAllTypes();
        }        
        return alTypes;        
    }
}
