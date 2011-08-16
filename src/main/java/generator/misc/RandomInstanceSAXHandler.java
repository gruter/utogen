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
 * for a user defined random instnace (RandomiserInstance).
 * Data are loaded in the initial loading of the application.
 */
public class RandomInstanceSAXHandler extends SAXDataHandler
{
    private Logger logger = Logger.getLogger(RandomInstanceSAXHandler.class);
    private Vector<RandomiserInstance> vRandomInstances;
    private RandomiserInstance ri;
    private LinkedHashMap properties;
    final int DESCRIPTION = 1;
    final int NONE = -1;
    int     parsedElement=NONE; //used to retrieve the description element    
    
    
    public RandomInstanceSAXHandler()
    {
        vRandomInstances = new Vector();
        properties = new LinkedHashMap();
    }
    
    public void startDocument() throws SAXException
    {
        logger.debug("Document parsing started");
    }
    
    
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        String attribValue, attribValue2;
        
        
        logger.debug("Element found:"+qName);
        if(qName.equalsIgnoreCase("randomiser-instance"))
        {
            ri = new RandomiserInstance();
            attribValue = attributes.getValue("randomiser-type");
            ri.setRandomiserType(attribValue);
            logger.debug("Expected value for randomiser-type:"+ attribValue);
            
            attribValue = attributes.getValue("name");
            ri.setName(attribValue);
            logger.debug("Expected value for name:"+ attribValue);
        }
        if(qName.equalsIgnoreCase("description"))
        {
            parsedElement=DESCRIPTION;
        }
        if(qName.equalsIgnoreCase("property"))
        {
            if(properties==null)
                properties = new LinkedHashMap();
            
            attribValue = attributes.getValue("name");
            attribValue2 = attributes.getValue("value");
            
            properties.put(attribValue, attribValue2);
        }
    }
    
    public void characters(char characters[], int start, int length)
    {
        String chData = (new String(characters, start, length)).trim();
        
        //the description element does not have attributes, but content, retrieve it.
        if(parsedElement==DESCRIPTION)
        {
            ri.setDescription(chData);
            parsedElement=NONE;
        }        
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("Element ending:"+qName);
        if(qName.equalsIgnoreCase("randomiser-instance"))
        {
            ri.setProperties(properties);
            properties = null;
            vRandomInstances.add(ri);
            logger.debug("Added one element, size is:"+vRandomInstances.size());
        }
    }
    
    //this is actually the method that is used by callers
    public Vector getData()
    {
        logger.debug("Returning vector, size is:"+vRandomInstances.size());
        return vRandomInstances;
    }

    @Override
    public void endDocument() throws SAXException {
      RandomiserInstance expressionRi = new RandomiserInstance();
      expressionRi.setName("_expression");
      expressionRi.setRandomiserType("ExpressionRandomiser");
      vRandomInstances.add(expressionRi);

      super.endDocument();
    }
}
