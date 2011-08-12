/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */



package generator.misc;
import java.util.LinkedHashMap;
import java.util.Vector;
import org.apache.log4j.Logger;
import generator.extenders.RandomiserInstance;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * Handles the input of an XML file which represents the definitions
 * of the database drivers.
 */
public class DBDriverSAXHandler extends SAXDataHandler
{
    private Logger logger = Logger.getLogger(DBDriverSAXHandler.class);
    
    private Vector<DBDriverInfo> vDriverInfo;
    private DBDriverInfo dbDriverInfo;
    
    //parsing the description
    final int NAME       = 1;
    final int CON_STRING = 2;
    final int LOAD_CLASS = 3;
    final int LOC_CLASS  = 4;
    final int STATUS     = 5;
    final int CHAR_DELIM = 6;
    final int DATE_DELIM = 7;
    final int TIME_DELIM = 8;
    final int TIMESTAMP_DELIM = 9;
    
    final int NONE = -1;
    int parsedElement=NONE; //used to retrieve the description element
    
    
    public DBDriverSAXHandler()
    {
        vDriverInfo = new Vector();
    }
    
    public void startDocument() throws SAXException
    {
        logger.debug("Document parsing started");
    }
    
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        String attribValue = null;
        
        
        logger.debug("Element found:"+qName);
        if(qName.equalsIgnoreCase("driver"))
        {
            dbDriverInfo = new DBDriverInfo();
            logger.debug("New dbDriverInfo");
        }
        
        if(qName.equalsIgnoreCase("name"))
            parsedElement=NAME;
        
        if(qName.equalsIgnoreCase("connection"))
            parsedElement=CON_STRING;
        
        if(qName.equalsIgnoreCase("loadclass"))
            parsedElement=LOAD_CLASS;
        
        if(qName.equalsIgnoreCase("location"))
            parsedElement=LOC_CLASS;
        
        if(qName.equalsIgnoreCase("status"))
            parsedElement=STATUS;
        
        if(qName.equalsIgnoreCase("CharDelim"))
            parsedElement=CHAR_DELIM;
        
        if(qName.equalsIgnoreCase("DateDelim"))
            parsedElement=DATE_DELIM;
        
        if(qName.equalsIgnoreCase("TimeDelim"))
            parsedElement=TIME_DELIM;
        
        if(qName.equalsIgnoreCase("TimestampDelim"))
            parsedElement=TIMESTAMP_DELIM;
        
    }
    
    public void characters(char characters[], int start, int length)
    {
        String chData = (new String(characters, start, length)).trim();
        
        if(parsedElement==NAME)
        {
            dbDriverInfo.setName(chData);
            parsedElement=NONE;
        }
        
        if(parsedElement==CON_STRING)
        {
            dbDriverInfo.setConString(chData);
            parsedElement=NONE;
        }
        
        if(parsedElement==LOAD_CLASS)
        {
            dbDriverInfo.setLoadClass(chData);
            parsedElement=NONE;
        }
        
        if(parsedElement==LOC_CLASS)
        {
            dbDriverInfo.setLocationClass(chData);
            parsedElement=NONE;
        }
        
        if(parsedElement==STATUS)
        {
            try
            {
                dbDriverInfo.setStatus(Integer.parseInt( chData));
            }
            catch(Exception e)
            {
                dbDriverInfo.setStatus(-1);
            }
            parsedElement=NONE;
        }
        
        if(parsedElement==CHAR_DELIM)
        {
            dbDriverInfo.setCharDelimiter(chData);
            parsedElement=NONE;
        }

        if(parsedElement==DATE_DELIM)
        {
            dbDriverInfo.setDateDelimiter(chData);
            parsedElement=NONE;
        }

        if(parsedElement==TIME_DELIM)
        {
            dbDriverInfo.setTimeDelimiter(chData);
            parsedElement=NONE;
        }

        if(parsedElement==TIMESTAMP_DELIM)
        {
            dbDriverInfo.setTimestampDelimiter(chData);
            parsedElement=NONE;
        }
        
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("Element ending:"+qName);
        if(qName.equalsIgnoreCase("driver"))
        {
            vDriverInfo.add(dbDriverInfo);
            logger.debug("Added one element, size is:"+vDriverInfo.size());
        }
    }
    
    //returns the data, this is what the user should actually call.
    public Vector getData()
    {
        logger.debug("Returning vector, size is:"+vDriverInfo.size());
        return vDriverInfo;
    }
}
