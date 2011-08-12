/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */



package generator.misc;
import generator.db.SQLJavaMapping;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * Handles the input of an XML file which represents the definitions
 * of the database drivers.
 */
public class JavaDbMappingsSAXHandler extends SAXDataHandler
{
    private Logger logger = Logger.getLogger(JavaDbMappingsSAXHandler.class);
    
    private Vector<SQLJavaMapping> vSQLJavaMapping;
    private SQLJavaMapping sqlJavaMapping;
    
    //parsing the description
    final int JAVA_MAPPING = 1;
    final int DB_TYPE      = 2;
    
    final int NONE = -1;
    int parsedElement=NONE; //used to retrieve the description element
    
    
    public JavaDbMappingsSAXHandler()
    {
        vSQLJavaMapping = new Vector();
    }

    
    public void startDocument() throws SAXException
    {
        logger.debug("Document parsing started");
    }
    
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        String attribValue = null;
        
        
        logger.debug("Element found:"+qName);
        if(qName.equalsIgnoreCase("java-mapping"))
        {
            sqlJavaMapping = new SQLJavaMapping();
            String name = attributes.getValue("name");
            sqlJavaMapping.setJavaType( resolveName(name) );
            logger.debug("New java mapping created ("+name+")");
        }
        
        if(qName.equalsIgnoreCase("db-type"))
        {
            parsedElement=DB_TYPE;
        }
        
    }
    
    public void characters(char characters[], int start, int length)
    {
        String chData = (new String(characters, start, length)).trim();
        
        if(parsedElement==DB_TYPE)
        {
            sqlJavaMapping.addSQLType(chData);
            parsedElement=NONE;
        }                
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("Element ending:"+qName);
        if(qName.equalsIgnoreCase("java-mapping"))
        {
            vSQLJavaMapping.add(sqlJavaMapping);
            logger.debug("Added one element, size is:"+vSQLJavaMapping.size());
        }
    }
    
    //returns the data, this is what the user should actually call.
    public Vector getData()
    {
        logger.debug("Returning vector, size is:"+vSQLJavaMapping.size());
        return vSQLJavaMapping;
    }


    //returns the numerical java type, given a certain java string type
    private int resolveName(String name)
    {
        int retVal = -1;
        if(name.toLowerCase().contains("boolean"))
        {
            retVal = RandomiserType.TYPE_BOOLEAN;
        } else if( name.toLowerCase().contains("short") )
        {
            retVal = RandomiserType.TYPE_SHORT;
        }else if( name.toLowerCase().contains("byte") )
        {
            retVal = RandomiserType.TYPE_BYTE;
        }else if( name.toLowerCase().contains("int") )
        {
            retVal = RandomiserType.TYPE_INTEGER;
        }else if( name.toLowerCase().contains("long") )
        {
            retVal = RandomiserType.TYPE_LONG;
        }else if( name.toLowerCase().contains("float") )
        {
            retVal = RandomiserType.TYPE_FLOAT;
        }else if( name.toLowerCase().contains("double") )
        {
            retVal = RandomiserType.TYPE_DOUBLE;
        }else if( name.toLowerCase().contains("bigdecimal") )
        {
            retVal = RandomiserType.TYPE_BIGDECIMAL;
        }else if( name.toLowerCase().contains("date") )
        {
            retVal = RandomiserType.TYPE_DATE;
        }else if( name.toLowerCase().contains("datetime") )
        {
            retVal = RandomiserType.TYPE_TIMESTAMP;
        }else if( name.toLowerCase().contains("string"))
        {
            retVal = 0;
        }

        if(retVal==-1)
        {
            logger.fatal("No compatible java type found for " + name);
        }
        return retVal;
    }

}
