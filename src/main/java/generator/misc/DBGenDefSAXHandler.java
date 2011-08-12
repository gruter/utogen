/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */
package generator.misc;

import generator.db.DBForeignKey;
import generator.db.DBGeneratorDefinition;
import generator.db.DBMetaDataManager;
import generator.db.DBTable;
import generator.db.DBTableGenerator;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Vector;
import org.apache.log4j.Logger;
import generator.extenders.RandomiserInstance;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * Handles the input of an XML file which represents the definitions
 * for generating text data to a specific file (DataFileDefinition).
 * Data are loaded in the initial loading of the application.
 */
public class DBGenDefSAXHandler extends SAXDataHandler
{

    private DBMetaDataManager dbMetaManager;
    private Vector<DBDriverInfo> vDBDriverInfo;
    private DBDriverInfo dbDriverInfo;
    private Logger logger = Logger.getLogger(DBGenDefSAXHandler.class);
    private Vector<DBGeneratorDefinition> vDBGenDef; //the output definitions
    private DBForeignKey dbForeignKey;
    private List<DBForeignKey> vDBForeignKeys;
    private Utils utils;
    //each output definition holds a number of data items, a data item
    // consists of a randomiser instance definition and instructions
    // how to format its output.
    private Vector<DBTableGenerator> vTableGenerators;
    private DBTableGenerator dbTableGenerator;
    private DBGeneratorDefinition dbGenDef;
    //parsing the description
    final int DESCRIPTION = 1;
    final int TABLE_GENERATION = 2;
    final int NONE = -1;
    int parsedElement = NONE; //used to retrieve the description element

    public DBGenDefSAXHandler()
    {
        utils = new Utils();
        vDBGenDef = new Vector();
        dbMetaManager = new DBMetaDataManager();
        vDBDriverInfo = utils.loadDBDriversInfo();
    }

    public void startDocument() throws SAXException
    {
        logger.debug("Document parsing started");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        String attribValue = null;
        String attribValue2 = null;
        String attribValue3 = null;
        String attribValue4 = null;
        String attribValue5 = null;
        String attribValue6 = null;


        logger.debug("Element found:" + qName);
        if (qName.equalsIgnoreCase("database-scenario"))
        {
            dbGenDef = new DBGeneratorDefinition();
            vDBGenDef = new Vector();
            vTableGenerators = new Vector();
        }

        if (qName.equalsIgnoreCase("provider"))
        {
            attribValue = attributes.getValue("value");
            dbGenDef.setDbDriver(attribValue);
        }

        if (qName.equalsIgnoreCase("url"))
        {
            attribValue = attributes.getValue("value");
            dbGenDef.setDbURL(attribValue);
        }

        if (qName.equalsIgnoreCase("user"))
        {
            attribValue = attributes.getValue("value");
            dbGenDef.setUser(attribValue);
        }

        if (qName.equalsIgnoreCase("password"))
        {
            attribValue = attributes.getValue("value");
            dbGenDef.setPassword(attribValue);
        }

        if (qName.equalsIgnoreCase("cycles"))
        {
            attribValue = attributes.getValue("value");
            dbGenDef.setCycles(Integer.valueOf(attribValue));
        }

        if (qName.equalsIgnoreCase("inTransaction"))
        {
            attribValue = attributes.getValue("value");
            dbGenDef.setEncInTransaction(Boolean.valueOf(attribValue));
        }



        if (qName.equalsIgnoreCase("description"))
        {
            parsedElement = DESCRIPTION;
        }

        //retrieve randomiser instance name, its width, alignment and enclosing char
        if (qName.equalsIgnoreCase("table"))
        {
            attribValue = attributes.getValue("name");
            DBTable dbTable = dbMetaManager.getTableInfo("", attribValue);

            dbTable.setName(attribValue);
            dbTableGenerator = new DBTableGenerator(dbTable);
        }

        if (qName.equalsIgnoreCase("field"))
        {
            attribValue = attributes.getValue("name");
            attribValue2 = attributes.getValue("randomiser-instance");
            dbTableGenerator.setFieldGenerator(attribValue, attribValue2);
        }

        if (qName.equalsIgnoreCase("foreign-keys"))
        {
            vDBForeignKeys = new ArrayList();
        }

        if (qName.equalsIgnoreCase("fkey"))
        {
            attribValue = attributes.getValue("masterTable");
            attribValue2 = attributes.getValue("masterField");
            attribValue3 = attributes.getValue("detailsTable");
            attribValue4 = attributes.getValue("detailsField");
            attribValue5 = attributes.getValue("from");
            attribValue6 = attributes.getValue("to");
            dbForeignKey = new DBForeignKey(attribValue, attribValue2, attribValue3, attribValue4, attribValue5, attribValue6);
            vDBForeignKeys.add(dbForeignKey);
        }
    }

    public void characters(char characters[], int start, int length)
    {
        String chData = (new String(characters, start, length)).trim();

        //the description element does not have attributes, but content, retrieve it.
        if (parsedElement == DESCRIPTION)
        {
            dbGenDef.setDescription(chData);
            parsedElement = NONE;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("Element ending:" + qName);

        if (qName.equalsIgnoreCase("table"))
        {
            dbTableGenerator.setForeignKeys(vDBForeignKeys);
            vDBForeignKeys = new ArrayList();
        }

        if (qName.equalsIgnoreCase("db-config"))
        {
            dbDriverInfo = utils.findDBDriver(dbGenDef.getDbDriver(), vDBDriverInfo);
            dbMetaManager.setConnectionProperties(dbDriverInfo.getLocationClass(), dbDriverInfo.getLoadClass(), dbGenDef.getDbURL(), dbGenDef.getUser(), dbGenDef.getPassword());
            logger.debug("will try to connect");
            try
            {
                dbMetaManager.connect();
            } catch (Exception ex)
            {
                logger.error("will try to connect - Error while connecting to the database", ex);
            }
            logger.debug("will try to connect - done");
        }
        if (qName.equalsIgnoreCase("database-scenario"))
        {
            vDBGenDef.add(dbGenDef);
        }

        if (qName.equalsIgnoreCase("table"))
        {
            vTableGenerators.add(dbTableGenerator);
            logger.debug("Added a db table generator, size is:" + vTableGenerators.size());
        }

        if (qName.equalsIgnoreCase("database-scenario"))
        {
            dbGenDef.setDBTableGenerators(vTableGenerators);
        }
    }

    //returns the data, this is what the user should actually call.
    public Vector getData()
    {
        logger.debug("Returning vector, size is:" + vDBGenDef.size());
        return vDBGenDef;
    }
}
