/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


package generator.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class XMLSaver
{
    Logger logger = Logger.getLogger(XMLSaver.class);
    

    public Document createDocument(String rootElementName)
    {
        Document dom = null;
        Element root;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            //get an instance of builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            //create an instance of DOM
            dom  = db.newDocument();
            root = dom.createElement(rootElementName);
            dom.appendChild(root);
        }
        catch(ParserConfigurationException pce)
        {
            logger.error("Error while trying to instantiate DocumentBuilder " + pce);
        }
        logger.debug("dom, root created...");
        return dom;
    }
    
    public void writeXMLContent(Document dom, String filename)
    {
        TransformerFactory xmlTF = TransformerFactory.newInstance();
        
        xmlTF.setAttribute("indent-number", Integer.valueOf(4));
        try
        {
            Transformer transformer = xmlTF.newTransformer();
            File userDir = new File(System.getProperty("user.dir"));

            FileOutputStream fout = new FileOutputStream( new File(userDir + "/" + filename) );
            Source domSource = new DOMSource(dom);
            Result domOut    = new StreamResult(new OutputStreamWriter(fout, "utf-8"));
            //the above line might as well had been as below, but due to a Transformer
            //implementation bug, indent does not occur, hence the wrapping in outputStreamWriter
            //Result domOut    = new StreamResult(fout);
            
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
            transformer.transform(domSource,domOut);
        }
        catch(TransformerConfigurationException ex)
        {
            logger.error("Error saving XML file :"+filename ,ex);
        }
        catch(TransformerException ex)
        {
            logger.error("Error saving XML file :"+filename ,ex);
        }
        catch(FileNotFoundException ex)
        {
            logger.error("Error saving XML file :"+filename ,ex);
        }
        catch(UnsupportedEncodingException ex)
        {
            logger.error("Error saving XML file :"+filename ,ex);
        }
    }    
}
