/*
 * DBDefinitionSAXHandler.java
 *
 * Created on 18 October 2008, 00:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.misc;

import java.util.Vector;
import java.util.LinkedHashMap;
import java.util.Vector;
import org.apache.log4j.Logger;
import generator.extenders.RandomiserInstance;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 *
 * @author Michael
 */
public class DBDefinitionSAXHandler extends SAXDataHandler
{
    private Logger logger = Logger.getLogger(DBDefinitionSAXHandler.class);
    private Vector<DBFileDefinition> vDBDefinitions; //the output definitions
    private DBFileDefinition dbFileDefinition;
    private String attribValue;
    private String description;
    
    //parsing the description
    private final int DESCRIPTION = 1;
    private final int NONE = -1;
    private int parsedElement=NONE; //used to retrieve the description element
    
    
    /** Creates a new instance of DBDefinitionSAXHandler */
    public DBDefinitionSAXHandler()
    {
        vDBDefinitions = new Vector();
    }
   
    
    public void startDocument() throws SAXException
    {
        logger.debug("Document parsing started");
    }
    
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        String attribValue = null;
        
        
        logger.debug("Element found:"+qName);
        if(qName.equalsIgnoreCase("data-output-definition"))
        {
            dbFileDefinition = new DBFileDefinition();
        }
        
        if(qName.equalsIgnoreCase("scenario"))
        {
            attribValue = attributes.getValue("value");
            dbFileDefinition.setScenario(attribValue);
        }
        
        if(qName.equalsIgnoreCase("scenarioFile"))
        {
            attribValue = attributes.getValue("value");
            dbFileDefinition.setLinkFileDB(attribValue);
        }
        
        
        if(qName.equalsIgnoreCase("description"))
        {
            parsedElement=DESCRIPTION;
        }
        
        //add it to the vector of data items, this will be linked to the data file definition
        
    }
    
    
    public void characters(char characters[], int start, int length)
    {
        String chData = (new String(characters, start, length)).trim();
        
        //the description element does not have attributes, but content, retrieve it.
        if(parsedElement==DESCRIPTION)
        {
            dbFileDefinition.setDescription(chData);
            parsedElement=NONE;
        }
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("Element ending:"+qName);
        if(qName.equalsIgnoreCase("data-output-definition"))
        {
            vDBDefinitions.add(dbFileDefinition);
            logger.debug("Added one element, size is:"+vDBDefinitions.size());
        }
    }
    
//returns the data, this is what the user should actually call.
    public Vector getData()
    {
        logger.debug("Returning vector, size is:"+vDBDefinitions.size());
        return vDBDefinitions;
    }
}
