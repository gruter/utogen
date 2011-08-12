/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


package generator.misc;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Michael
 * Saves a data file definitions vector in TextFileDefinitions.xml
 * When a collection of new classes needs to be saved in an XML file, 
 * a similar class will have to be provided and the saveData method 
 * will have to be called. It is hard to convert these classes into some kind 
 * of design pattern, because each class will actually provide its own XML structure.
 */
public class FileOutDataSaver
{
    private Logger logger = Logger.getLogger(FileOutDataSaver.class);
    private XMLSaver xmlSaver;
    private Document dom=null;
    private Element  root;
    
    
    /** 
     * Saves the data to the XML file. This is the method that the caller will
     * actually call to save the DataDefinition data. When a collection of new
     * classes needs to be saved in an XML file, a similar class will have to be
     * provided and the saveData method will have to be called. It is hard to 
     * convert these classes into some kind of design pattern, becase each 
     * class will actually provide its own XML structure.
     *
     * <p> Preconditions: Vector contains valid DataFileDefinition objects
     * <p> Post-effects: Contents of vector are saved in TextFileDefinitions.xml
     *                   CAUTION: Any actual data existing in file will be overwritten!!!
     *
     * @param vData a Vector of DataDefinition objects to be saved in file TextFileDefinitions.xml
     */
    public void saveData(Vector<DataFileDefinition> vData)
    {
        //[*] maybe i can get the root element out of the dom?
        xmlSaver = new XMLSaver();
        dom = xmlSaver.createDocument("datafile-definitions");
        root = dom.getDocumentElement();
        
        //all data will be linked here
        Element elemDataFileDefinition;
        
        //each dataFileDefinition has a vector linked to it. The vector has
        //DataFileItem objects
        DataFileDefinition dataFileDefinition;
        Vector<DataFileItem> vDataFileItems;
        DataFileItem dataItem;
                
        for(int i=0; i<vData.size(); i++)
        {
            dataFileDefinition     = vData.elementAt(i);
            elemDataFileDefinition = addElementDFD(dataFileDefinition);
            addElementDFD(elemDataFileDefinition, "description", dataFileDefinition.getDescription());
            addElementDFD(elemDataFileDefinition, "log4j", dataFileDefinition.getLog4j());
            
            vDataFileItems = dataFileDefinition.getOutDataItems();
            for(int j=0; j<vDataFileItems.size(); j++)
            {
                dataItem = vDataFileItems.elementAt(j);
                addDataItem(elemDataFileDefinition,dataItem);
            }
        }        
        xmlSaver.writeXMLContent(dom, "conf/TextFileDefinitions.xml");
    }
    

    private Element addElementDFD(Element elemDFD, String elementName, String value)
    {
        Element description = dom.createElement(elementName);
        description.setTextContent(value);
        elemDFD.appendChild(description);
        //logger.debug("description: "+dfd.getDescription());
        return elemDFD;
    }    
    
    private Element addElementDFD(DataFileDefinition dfd)
    {
        Element elemDFD = dom.createElement("File-output-definition");
        elemDFD.setAttribute("name",dfd.getName());
        elemDFD.setAttribute("delimiter",dfd.getDelimiter());
        elemDFD.setAttribute("totalRecords", "" + dfd.getTotalRecords());
        elemDFD.setAttribute("tps", "" + dfd.getTps());
        elemDFD.setAttribute("loggerName", dfd.getLog4jName());
        
        root.appendChild(elemDFD);
        logger.debug("File-output-definition: "+dfd.getName());
        return elemDFD;
    }

    
    private void addDataItem(Element elemDFD, DataFileItem dataItem)
    {
        Element elemDataItem = dom.createElement("data-item");
        elemDataItem.setAttribute("fieldName", dataItem.getFieldName());
        elemDataItem.setAttribute("randomiser-instance",dataItem.getRandomiserInstanceName());
        elemDataItem.setAttribute("width",""+dataItem.getWidth());
        elemDataItem.setAttribute("encloseChar",dataItem.getEncloseChar());
        elemDataItem.setAttribute("alignment",""+dataItem.getAlignment());
        
        elemDFD.appendChild(elemDataItem);
        logger.debug("added a data-item element");        
    }    
    
}
