/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


package generator.misc;
import java.util.Vector;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * Extends DefaultHandler by providing an abstract method which is used to return data.
 * See RandomDefinitionsBuilder for details.
 */
public abstract class SAXDataHandler extends DefaultHandler
{   
    public abstract Vector getData();
}
