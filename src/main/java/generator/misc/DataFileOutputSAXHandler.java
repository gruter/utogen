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
 * Handles the input of an XML file which represents the definitions
 * for generating text data to a specific file (DataFileDefinition).
 * Data are loaded in the initial loading of the application.
 */
public class DataFileOutputSAXHandler extends SAXDataHandler
{
    private Logger logger = Logger.getLogger(DataFileOutputSAXHandler.class);
    private Vector<DataFileDefinition> vDFDs = new Vector<DataFileDefinition>();
    
    //each output definition holds a number of data items, a data item
    // consists of a randomiser instance definition and instructions
    // how to format its output.
    private Vector<DataFileItem> vDataItems;
    private DataFileDefinition dataFileDefinition;
    //parsing the description
   private final int DESCRIPTION = 1;
   private final int LOG4J = 2;
   private final int NONE = -1;
   private int parsedElement = NONE; //used to retrieve the description element
    
    
    public DataFileOutputSAXHandler()
    {
    }
    
    public void startDocument() throws SAXException
    {
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        String attribValue = null;
        
        if(qName.equalsIgnoreCase("file-output-definition"))
        {
            dataFileDefinition = new DataFileDefinition();
            attribValue = attributes.getValue("delimiter");
            dataFileDefinition.setDelimiter(attribValue);

            attribValue = attributes.getValue("loggerName");
            dataFileDefinition.setLog4jName(attribValue);
            
            try {
                attribValue = attributes.getValue("totalRecords");
                dataFileDefinition.setTotalRecords(Long.parseLong(attribValue));
            } catch(Exception e) {
                logger.error("Error during integer conversion for totalRecords:" + attribValue);
                dataFileDefinition.setTotalRecords(0);
            }
            
            try {
                attribValue = attributes.getValue("tps");
                dataFileDefinition.setTps(Integer.parseInt(attribValue));
            } catch(Exception e) {
                logger.error("Error during integer conversion for tps:" + attribValue);
                dataFileDefinition.setTps(0);
            }
            
            attribValue = attributes.getValue("name");
            dataFileDefinition.setName(attribValue);
            
            logger.debug("file-output-definition attributes:"+ dataFileDefinition.getName());
        }
        
        if(qName.equalsIgnoreCase("description")) {
            parsedElement = DESCRIPTION;
        }
        if(qName.equalsIgnoreCase("log4j")) {
          parsedElement = LOG4J;
        }
        //retrieve randomiser instance name, its width, alignment and enclosing char
        if(qName.equalsIgnoreCase("data-item"))
        {
            if(vDataItems==null) {
                vDataItems = new Vector<DataFileItem>();
            }
            
            DataFileItem dataItem = new DataFileItem();
            
            attribValue = attributes.getValue("fieldName");
            dataItem.setFieldName(attribValue);
            
            try {
                attribValue = attributes.getValue("alignment");
                dataItem.setAlignment(Integer.parseInt(attribValue));
            } catch(Exception e) {
                logger.error("Error during integer conversion for alignment:"+attribValue);
                dataItem.setAlignment(Constants.ALIGN_LEFT);
            }
            attribValue = attributes.getValue("encloseChar");
            if(attribValue.length() > 0) {
                dataItem.setEncloseChar(attribValue.substring(0,1));
            } else {
                dataItem.setEncloseChar("");
            }
            attribValue = attributes.getValue("randomiser-instance");
            dataItem.setRandomiserInstanceName(attribValue);
            
            try {
                attribValue = attributes.getValue("width");
                dataItem.setWidth(Integer.parseInt(attribValue));
            } catch(Exception e) {
                logger.error("Error during integer conversion for width:"+attribValue);
                dataItem.setAlignment(Constants.ALIGN_LEFT);
            }
            //add it to the vector of data items, this will be linked to the data file definition
            vDataItems.add(dataItem);
        }
    }
    
    public void characters(char characters[], int start, int length)
    {
        String chData = (new String(characters, start, length)).trim();
        
        //the description element does not have attributes, but content, retrieve it.
        if(parsedElement == DESCRIPTION) {
            dataFileDefinition.setDescription(chData);
            parsedElement=NONE;
        }
        if(parsedElement == LOG4J) {
          dataFileDefinition.setLog4j(chData);
          parsedElement = NONE;
      }
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("Element ending:"+qName);
        if(qName.equalsIgnoreCase("file-output-definition"))
        {
            //link the vector to the DFD, and reset it.
            dataFileDefinition.setOutDataItems(vDataItems);
            vDataItems=null;
            vDFDs.add(dataFileDefinition);
            logger.debug("Added one element, size is:"+vDFDs.size());
        }
    }
    
    //returns the data, this is what the user should actually call.
    public Vector getData()
    {
        logger.debug("Returning vector, size is:"+vDFDs.size());
        return vDFDs;
    }
}
