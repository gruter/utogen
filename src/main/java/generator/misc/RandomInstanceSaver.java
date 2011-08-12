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
 * Saves a random-instances vector in Repository.xml
 * When a collection of new classes needs to be saved in an XML file, 
 * a similar class will have to be provided and the saveData method 
 * will have to be called. It is hard to convert these classes into some kind 
 * of design pattern, because each class will actually provide its own XML structure.
 */
public class RandomInstanceSaver
{
    private Logger logger = Logger.getLogger(RandomInstanceSaver.class);
    private XMLSaver xmlSaver;
    private Document dom=null;
    private Element root;
    
    /**
     * Saves the data to the XML file. This is the method that the caller will
     * actually call to save the RandomiserInstance data.
     *
     * <p> Preconditions: Vector contains valid RandomiserInstance objects
     * <p> Post-effects: Contents of vector are saved in Repository.xml
     *                   CAUTION: Any actual data existing in file will be overwritten!!!
     *
     * @param vData a Vector of RandomiserInstance objects to be saved in file Repository.xml
     */
    public void saveData(Vector<RandomiserInstance> vData)
    {
        Element elemRI;
        RandomiserInstance ri;
        
        //create the document and set its root element
        xmlSaver = new XMLSaver();
        dom = xmlSaver.createDocument("randomiser-instances");
        root = dom.getDocumentElement();
        
        for(int i=0; i<vData.size(); i++)
        {
            ri     = vData.elementAt(i);
            elemRI = addElementRI(ri);
            addElementDescription(ri,elemRI);
            
            LinkedHashMap properties;
            properties = ri.getProperties();
            
            Set<String> keys;
            if(properties==null)
                logger.fatal("Properties are null, has this object been linked in RandomiserInstance?");
            keys = properties.keySet();
            
            for(String key : keys)
            {
                String value = (String) properties.get(key);
                addElementProperty(key,value,elemRI);
            }
        }
        xmlSaver.writeXMLContent(dom,"conf/Repository.xml");
    }
    

    
    private Element addElementRI(RandomiserInstance ri)
    {
        Element elemRI = dom.createElement("randomiser-instance");
        elemRI.setAttribute("randomiser-type",ri.getRandomiserType());
        elemRI.setAttribute("name",ri.getName());
        root.appendChild(elemRI);
        logger.debug("added a ranomiser-instance element: "+ri.getName() + ", "+ri.getDescription());
        return elemRI;
    }
    
    private void addElementDescription(RandomiserInstance ri, Element elemRI)
    {
        Element elemDescript= dom.createElement("description");
        elemDescript.setTextContent(ri.getDescription());
        elemRI.appendChild(elemDescript);
        logger.debug("added a description element");
    }
    
    private void addElementProperty(String key, String value, Element elemRI)
    {
        Element elemProperty = dom.createElement("property");
        elemProperty.setAttribute("name",key);
        elemProperty.setAttribute("value",value);
        
        elemRI.appendChild(elemProperty);
        logger.debug("added a property element");
    }
    
}
