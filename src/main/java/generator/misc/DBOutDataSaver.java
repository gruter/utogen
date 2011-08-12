/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


package generator.misc;

import generator.db.DBForeignKey;
import generator.db.DBGeneratorDefinition;
import generator.db.DBFieldGenerator;
import generator.db.DBTableGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;

/**
 *
 * Saves a database definition object in a user defined xml file
 * When a collection of new classes needs to be saved in an XML file, 
 * a similar class will have to be provided and the saveData method 
 * will have to be called. It is hard to convert these classes into some kind 
 * of design pattern, because each class will actually provide its own XML structure.
 */
public class DBOutDataSaver
{
    private Logger logger = Logger.getLogger(DBOutDataSaver.class);
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
    
    public void saveData( DBGeneratorDefinition dbDef)
    {
        //[*] maybe i can get the root element out of the dom?
        xmlSaver = new XMLSaver();
        dom = xmlSaver.createDocument("database-scenario");
        root = dom.getDocumentElement();
        
        //all data will be linked here
        Element elemTable, elemFields, elemFKeys;
        
        addDBConfig(dbDef );
        
        for(DBTableGenerator dbTableGenerator:dbDef.getDBTableGenerators())
        {
            //table
            elemTable = addElementTable(dbTableGenerator);

            //fields
            elemFields = addFieldsElement(elemTable);
            for( DBFieldGenerator dbFieldGenerator: dbTableGenerator.getDBFieldGenerators() )
            {
                addDBFieldGeneratorItem(elemFields, dbFieldGenerator);
            }

            //foreign keys
            elemFKeys = addFKeysElement(elemTable);
            for( DBForeignKey dbForeignKey: dbTableGenerator.getForeignKeys() )
            {
                addDBFKeyGeneratorItem(elemFKeys, dbForeignKey);
            }

        }
        
        xmlSaver.writeXMLContent(dom, dbDef.getFilename() );        
    }
    

    
    private Element addDBConfig(DBGeneratorDefinition dbDef)
    {
        logger.debug("db-config: start");
        
        Element elemDB = dom.createElement("db-config");
        root.appendChild(elemDB);

        Element elemDBScenario = dom.createElement("scenario");
        elemDBScenario.setAttribute("value", dbDef.getScenario());
        elemDB.appendChild(elemDBScenario);
        
        Element elemDescription = dom.createElement("description");
        elemDescription.setTextContent(dbDef.getDescription());
        elemDB.appendChild(elemDescription);
        
        Element elemCycles = dom.createElement("cycles");
        elemCycles.setAttribute("value", "" + dbDef.getCycles());
        elemDB.appendChild(elemCycles);        

        Element elemInTransaction = dom.createElement("inTransaction");
        elemInTransaction.setAttribute("value", "" + dbDef.isEncInTransaction());
        elemDB.appendChild(elemInTransaction);        

        
        Element elemDBProvider = dom.createElement("provider");
        elemDBProvider.setAttribute("value", dbDef.getDbDriver());
        elemDB.appendChild(elemDBProvider);
        
        Element elemURL = dom.createElement("url");
        elemURL.setAttribute("value", dbDef.getDbURL());
        elemDB.appendChild(elemURL);
        
        Element elemUser = dom.createElement("user");
        elemUser.setAttribute("value",dbDef.getUser());
        elemDB.appendChild(elemUser);

        Element elemPasswd = dom.createElement("password");
        elemPasswd.setAttribute("value",dbDef.getPassword());
        elemDB.appendChild(elemPasswd);

        
        logger.debug("db-config: done");
        return elemDB;
    }

    private Element addElementTable(DBTableGenerator dbt)
    {
        Element elemTable = dom.createElement("table");
        
        elemTable.setAttribute("name", dbt.getName());
        root.appendChild(elemTable);
        logger.debug("table name: "+dbt.getName());
        return elemTable;
    }    
    

    private Element addFieldsElement(Element elemTable)
    {
        Element elemFields = dom.createElement("fields");
        elemTable.appendChild(elemFields);
        return elemFields;
    }


    private Element addFKeysElement(Element elemTable)
    {
        Element elemFkeys = dom.createElement("foreign-keys");
        elemTable.appendChild(elemFkeys);
        return elemFkeys;
    }
    
    
    private void addDBFieldGeneratorItem(Element elemFields, DBFieldGenerator fieldGenerator)
    {
        Element elemDataItem = dom.createElement("field");
        elemDataItem.setAttribute("name",fieldGenerator.getField());
        elemDataItem.setAttribute("randomiser-instance",fieldGenerator.getGenerator());
        
        elemFields.appendChild(elemDataItem);
        logger.debug("added a db field generator element");        
    }

    private void addDBFKeyGeneratorItem(Element elemFKeys, DBForeignKey fKey)
    {
        Element elemDataItem = dom.createElement("fkey");
        elemDataItem.setAttribute("masterField",fKey.getMasterField());
        elemDataItem.setAttribute("masterTable",fKey.getMasterTable());        

        elemDataItem.setAttribute("detailsField",fKey.getDetailsField());
        elemDataItem.setAttribute("detailsTable",fKey.getDetailsTable());
        elemDataItem.setAttribute("from", ""+fKey.getFromNum());
        elemDataItem.setAttribute("to", ""+fKey.getToNum());

        elemFKeys.appendChild(elemDataItem);
        logger.debug("added a db foreign key element");
    }
    
}
