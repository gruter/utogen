/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */



package generator.misc;

import java.util.Vector;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * Handles the input of an XML file which represents the "system defined" 
 * specifications for a randomiser (randomiser-type).
 * Data are loaded in the initial loading of the application.
 */
public class RandomTypeSAXHandler extends SAXDataHandler
{
    private RandomiserType rt;
    private Vector<RandomiserType> vRandomTypes;
    private Logger logger = Logger.getLogger(RandomTypeSAXHandler.class);
    
    public RandomTypeSAXHandler()
    {
        vRandomTypes = new Vector();
    }
    
    public void startDocument() throws SAXException
    {
        logger.debug("Document parsing started");
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        String attribName, attribValue;
        
        logger.debug("Element found:"+qName);
        if(qName.equalsIgnoreCase("randomiser-type"))
        {            
            rt = new RandomiserType();
            
            for(int i=0; i<attributes.getLength(); i++)
            {
                attribName  = attributes.getQName(i);
                attribValue = attributes.getValue(i);
                logger.debug("Attribute name,value:"+attribName + "," + attribValue);        
                
                if( attribName.equalsIgnoreCase("name") )
                    rt.setName(attribValue);
                
                if( attribName.equalsIgnoreCase("panel") )
                    rt.setPanel(attribValue);
                    
                if( attribName.equalsIgnoreCase("generator") )
                    rt.setGenerator(attribValue);

                if( attribName.equalsIgnoreCase("jtype") )
                {
                    try
                    {
                        int value;
                        value = Integer.parseInt(attribValue);
                        rt.setJtype(value);
                    }
                    catch(NumberFormatException nfe)
                    {
                        logger.error("Attribute jtype not of numerical value:"+attribValue);
                    }
                }
            }
        }
    }
    
    public void endElement (String uri, String localName, String qName) throws SAXException
    {
        logger.debug("Element ending:"+qName);        
        if(qName.equalsIgnoreCase("randomiser-type"))
        {            
            vRandomTypes.add(rt);
            logger.debug("Added one element, size is:"+vRandomTypes.size());
        }        
    }   
    
    public Vector getData()
    {
        logger.debug("Returning vector, size is:"+vRandomTypes.size());
        return vRandomTypes;
    }   
}
