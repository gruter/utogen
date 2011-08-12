/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

package generator.misc;


import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;
import generator.extenders.RandomiserInstance;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

/**
 *
 * Saves a dbdirver info instance in DBDrivers.xml
 * When a collection of new classes needs to be saved in an XML file, 
 * a similar class will have to be provided and the saveData method 
 * will have to be called. It is hard to convert these classes into some kind 
 * of design pattern, because each class will actually provide its own XML structure.
 */
public class DBDriverInfoSaver
{
    private Logger logger = Logger.getLogger(DBDriverInfoSaver.class);
    private XMLSaver xmlSaver;
    private Document dom=null;
    private Element root;
    
    
    
    /**
     *
     *
     *
     *
     *
     */
    public void saveData(Vector<DBDriverInfo> vData)
    {
        Element elemDB;
        DBDriverInfo dbDriverInfo;
        
        //create the document and set its root element
        xmlSaver = new XMLSaver();
        dom = xmlSaver.createDocument("dbdrivers");
        root = dom.getDocumentElement();
        
        for(int i=0; i<vData.size(); i++)
        {
            dbDriverInfo = vData.elementAt(i);
            elemDB = addElementRI(dbDriverInfo);
            addElements(dbDriverInfo,elemDB);
        }
        xmlSaver.writeXMLContent(dom,"conf/dbdrivers.xml");
    }
    

    
    private Element addElementRI(DBDriverInfo dbDriverInfo)
    {
        Element elemDB = dom.createElement("driver");
        root.appendChild(elemDB);
        logger.debug("added a driver element: "+dbDriverInfo.getName());
        return elemDB;
    }
    
    private void addElements(DBDriverInfo dbDriverInfo, Element elemDB)
    {
        Element elem = dom.createElement("name");
        elem.setTextContent(dbDriverInfo.getName());
        elemDB.appendChild(elem);

        elem = dom.createElement("Connection");
        elem.setTextContent(dbDriverInfo.getConString());
        elemDB.appendChild(elem);

        elem = dom.createElement("Loadclass");
        elem.setTextContent(dbDriverInfo.getLoadClass());
        elemDB.appendChild(elem);

        elem = dom.createElement("location");
        elem.setTextContent(dbDriverInfo.getLocationClass());
        elemDB.appendChild(elem);
        
        elem = dom.createElement("status");
        elem.setTextContent(""+dbDriverInfo.getStatus());
        elemDB.appendChild(elem);  
        
        elem = dom.createElement("CharDelim");
        elem.setTextContent( dbDriverInfo.getCharDelimiter() );
        elemDB.appendChild(elem);
        
        elem = dom.createElement("DateDelim");
        elem.setTextContent( dbDriverInfo.getDateDelimiter() );
        elemDB.appendChild(elem);
        
        elem = dom.createElement("TimeDelim");
        elem.setTextContent( dbDriverInfo.getTimeDelimiter() );
        elemDB.appendChild(elem);

        elem = dom.createElement("TimestampDelim");
        elem.setTextContent( dbDriverInfo.getTimestampDelimiter() );
        elemDB.appendChild(elem);
        
        logger.debug("added a name element");
    }       
}
