/*
 * Generator.java
 *
 * Created on 11 November 2007, 21:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package generator.engine.db;

import generator.db.DBGeneratorDefinition;
import generator.engine.ProgressUpdateObserver;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;
import generator.db.DBFieldGenerator;
import generator.db.DBTableGenerator;
import generator.misc.ApplicationContext;
import generator.misc.Constants;
import generator.misc.DBDriverInfo;
import generator.misc.RandomiserType;
import generator.misc.Utils;
import java.sql.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class Generator
{

    private Vector<RandomiserType> vRandomiserTypes;
    private Vector<RandomiserInstance> vRandomiserInstances;
    private Vector<DBDriverInfo> vDBDriverInfo;
    private DBGeneratorDefinition dbGenConfig;
    private Logger logger = Logger.getLogger(Generator.class);
    private ProgressUpdateObserver observer;

    /** Creates a new instance of Generator */
    public Generator()
    {
        //load the randomiser-type definitions from file
        vRandomiserTypes = ApplicationContext.getInstance().getRandomiserTypes();

        //load the randomiser-instance definitions from the file
        vRandomiserInstances = ApplicationContext.getInstance().getRandomiserInstances();

        //load the db drivers info
        vDBDriverInfo = ApplicationContext.getInstance().getDriverInfo();
    }

    public void setDBGeneratorDefinition(DBGeneratorDefinition dbGenConfig)
    {
        this.dbGenConfig = dbGenConfig;
    }

    /**
     * Returns a RandomiserInstance object, given a string.
     * Preconditions: vRandomiserInstances must NOT be null :)
     */
    private RandomiserInstance getRandomiserInstance(String riName)
    {
        RandomiserInstance randomizerInstance = null;

        logger.debug("Retrieving randomiserInstance object for:" + riName);

        for (RandomiserInstance ri : vRandomiserInstances)
        {
            if (ri.getName().equalsIgnoreCase(riName))
            {
                randomizerInstance = ri;
                break;
            }
        }

        logger.debug("Retrieving the randomiserInstance for:" + riName + ". Found:" + randomizerInstance != null ? true : false);
        return randomizerInstance;
    }

    /**
     * Returns the name of a RandomiserType class,
     * given its name in the application.
     * Preconditions: vRandomiserTypes must NOT be null :)
     */
    private RandomiserType getRandomiserType(String randomiserType)
    {
        RandomiserType randomizerType = null;

        logger.debug("Retrieving randomiserType object for:" + randomiserType);
        for (RandomiserType rt : vRandomiserTypes)
        {
            if (rt.getName().equalsIgnoreCase(randomiserType))
            {
                randomizerType = rt;
                break;
            }
        }

        logger.debug("Retrieving the randomiserType for:" + randomiserType + ". Found:" + randomizerType != null ? randomizerType.getName() : "false");
        return randomizerType;
    }

    /**
     * Retrieves a database connection using the currently set database configuration
     * 
     * @return Connection
     */
    private Connection initialiseDBConnection()
    {
        //we need the driver for this database connection
        DBDriverInfo dbDriverInfo;

        //we need a connection
        Connection connection;

        Utils utils = new Utils();
        //perform initialisation: find the driver, get a connection for that driver
        dbDriverInfo = utils.findDBDriver(dbGenConfig.getDbDriver(), vDBDriverInfo);
        connection = utils.getDBConnection(dbDriverInfo, dbGenConfig);
        if (connection == null)
        {
            logger.error("Error while getting database connection:" + dbGenConfig.getDbDriver());
            observer.datageGenError("Error while getting database connection.");
            return null;
        }
        return connection;
    }

    /**
     * Sets up the generators, by going through the list of the tables and the generators assigned to them
     * Setting up means:
     * a) instantiating all the generators, if a generator is used in more than one tables, each table
     *    will have a different instance of that calculator
     * b) for every generator instance, also associate the delimiters it uses
     *
     * The key for the generators is in the form Table.Field
     * If a certain field happens to get its values from another table's key, (foreign key), no generators are assigned to that key
     * 
     * @param alDBGenerators
     * @param mapGenerators
     * @param mapDelimiters
     */
    private void initialiseGenerators(List<DBTableGenerator> alDBGenerators, Map mapGenerators, Map mapDelimiters)
    {
        //now we need to set the randomisers
        IRandomiserFunctionality iGenerator;

        Utils utils = new Utils();

        //find the driver, we need the delimiters
        DBDriverInfo dbDriverInfo = utils.findDBDriver(dbGenConfig.getDbDriver(), vDBDriverInfo);

        for (DBTableGenerator dbTableGenerator : alDBGenerators)
        {
            System.out.println("xxxxxxxxxxxxxxxxx" + dbTableGenerator.getName());
            List<DBFieldGenerator> aDBFieldsGenerators = dbTableGenerator.getDBFieldGenerators();
            for (DBFieldGenerator dbFieldGen : aDBFieldsGenerators)
            {

                //A field may have no generators assigned to it, that's normal, we skip that
                if (dbFieldGen.getGenerator() == null || dbFieldGen.getGenerator().length()==0)
                {
                    logger.debug("POSSIBLE ERROR: No generator for " + dbTableGenerator.getName() + "." + dbFieldGen.getField());
                    continue;
                }

                //if it's not null, then it may be linked to another Table.Field (which is not a generator, so skip that one too)
                if (isForeignKey(dbTableGenerator, dbFieldGen.getField()))
                {
                    logger.debug("Skipping foreign key " + dbFieldGen.getField());
                    continue;
                }

                //if we are here, we have a field with a generator assigned to it

                //the id of the table field we want to generate data for
                String id = dbTableGenerator.getName() + "." + dbFieldGen.getField();

                //create the randomiser instance out of the name
                RandomiserInstance ri = getRandomiserInstance(dbFieldGen.getGenerator());

                //get the randomiser type out of the RI
                RandomiserType rt = getRandomiserType(ri.getRandomiserType());

                //load and store the generator, set its RI, now it is ready to use
                iGenerator = (IRandomiserFunctionality) utils.createObject(rt.getGenerator());
                iGenerator.setRandomiserInstance(ri);

                // this particular field is associated with the generator created above
                mapGenerators.put(id, iGenerator);

                //once a value is generated for this field, we need to wrap it up in the appropriate delimeters
                String delimiter = "";
                if (rt.getJtype() == rt.TYPE_STRING)
                {
                    delimiter = dbDriverInfo.getCharDelimiter();
                } else if (rt.getJtype() == rt.TYPE_DATE)
                {
                    delimiter = dbDriverInfo.getDateDelimiter();
                } else if (rt.getJtype() == rt.TYPE_DATE)
                {
                    delimiter = dbDriverInfo.getDateDelimiter();
                } else if (rt.getJtype() == rt.TYPE_TIME)
                {
                    delimiter = dbDriverInfo.getTimeDelimiter();
                } else if (rt.getJtype() == rt.TYPE_TIMESTAMP)
                {
                    delimiter = dbDriverInfo.getTimestampDelimiter();
                }
                if (delimiter != null)
                {
                    mapDelimiters.put(id, new String(delimiter));
                }
            }//for fields generator
        }//for tables
    } // initialiseGenerators

    /**
     * Generates and returns a map of the tables for which we need to generate data
     * Everytime we geneate data for a certian table we remove it from this map
     * (the map is re-generated after each a cycle - we need a more efficient way than this!)
     * @return
     */
    private Map getTablesToGenerate()
    {
        List<DBTableGenerator> alDBGenerators = dbGenConfig.getDBTableGenerators();

        Map mapDBGenerators = new HashMap();

        for (DBTableGenerator dbT : alDBGenerators)
        {
            mapDBGenerators.put(dbT.getName(), dbT);
        }

        return mapDBGenerators;
    }

    /**
     * Returns true if fieldName of a certain table
     * appears as a key in another tablle (foreign key)
     */
    private boolean isForeignKey(DBTableGenerator dbTableGen, String fieldName)
    {

        if (dbTableGen.getForeignKeyForField(fieldName).length() > 0)
        {
            return true;
        }

        return false;
    }

    /**
     * Returns true if all the fields that are foreign keys
     * have already had their values generated.
     *
     * If TableA.KeyB is a foreign field for TableB.Key,
     * this method returns true, if the value for TableB.key has been generated.
     *
     * If any of the fields are null (they point to values that have not been generated yet),
     * this method returns null
     */
    private boolean checkForeignKeys(DBTableGenerator dbTableGenerator, Map mapValues)
    {
        //first check that if this table has foreign keys, the values for these keys
        //have already been generated from the master table
        List<DBFieldGenerator> aDBFieldsGenerators = dbTableGenerator.getDBFieldGenerators();
        for (DBFieldGenerator dbFieldGen : aDBFieldsGenerators)
        {
            String id = dbTableGenerator.getName() + "." + dbFieldGen.getField();
            String fkey = dbTableGenerator.getForeignKeyForField(dbFieldGen.getField());
            if (fkey.length() > 0)
            {
                Object keyValue = mapValues.get(fkey);
                if (keyValue == null)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the given value enclosed in the appropriate delimiters, which are retrieved
     * either by looking at the delimiters of either id1 or id2.
     * 
     * @param id1
     * @param id2
     * @param objGeneratedValue
     * @param mapDelimeters
     * @return
     */
    private String getDelimitedValue(String id1, String id2, Object obj, Map mapDelimeters)
    {
        String val;

        String tmp;
        tmp = (String) mapDelimeters.get(id1);
        if (tmp == null)
        {
            tmp = (String) mapDelimeters.get(id2);
        }

        StringBuffer delimiter = new StringBuffer(tmp);
        if (delimiter.indexOf(Constants.DATE_DELIM) >= 0)
        {
            int start = delimiter.indexOf(Constants.DATE_DELIM);
            int end = start + Constants.DATE_DELIM.length();
            delimiter.replace(start, end, obj.toString());
            val = delimiter.toString();
        } else
        {
            val = delimiter.toString() + obj + delimiter.toString();
        }
        return val;
    }

    /**
     * Generates a single query for table dbTableGenerator
     *
     * @param dbTableGenerator
     * @param mapGenerators
     * @param mapDelimeters
     * @param mapValues
     * @return String - the generated query
     */
    private String generateQuery(DBTableGenerator dbTableGenerator, Map mapGenerators, Map mapDelimeters, Map mapValues)
    {
        //the generators have already been set, start generating data
        String val;
        IRandomiserFunctionality iGenerator;
        List<DBFieldGenerator> aDBFieldsGenerators = dbTableGenerator.getDBFieldGenerators();

        for (DBFieldGenerator dbFieldGen : aDBFieldsGenerators)
        {
            Object objGeneratedValue = null;

            //the id of the table field we want to generate data for is Table.Field (set in initialiseGenerators)
            String id = dbTableGenerator.getName() + "." + dbFieldGen.getField();

            iGenerator = (IRandomiserFunctionality) mapGenerators.get(id);
            if (iGenerator == null)
            {
                //if we do not have a generator, we might have a foreign key case...
                //get foreign key, and then get the value associated with that key
                String fkey = dbTableGenerator.getForeignKeyForField(dbFieldGen.getField());

                objGeneratedValue = mapValues.get(fkey);
                if (objGeneratedValue == null)
                {
                    logger.error("POSSIBLE ERROR: No foreign key value found for null generator: " + id);
                }
            } else
            {
                objGeneratedValue = iGenerator.generate();
            }
            //every time we generate a value we put it in the map for future reference
            // ( in case this is used as a foreign key somewhere else)
            mapValues.put(id, objGeneratedValue);
        }

        //we have the values, form the Query
        StringBuffer sbQuery = new StringBuffer();
        StringBuffer sbFields = new StringBuffer();
        StringBuffer sbValues = new StringBuffer();
        sbFields.append("(");
        sbValues.append("(");

        sbQuery.append("INSERT INTO ");
        sbQuery.append(dbTableGenerator.getName());

        for (DBFieldGenerator dbFieldGen : aDBFieldsGenerators)
        {
            String id = dbTableGenerator.getName() + "." + dbFieldGen.getField();

            //all the fields have a generator assigned to them except the fields that are linked to other fields
            // which have their generator set as Table.Field
            if (dbFieldGen.getGenerator() != null && dbFieldGen.getGenerator().length() > 0)
            {
                //getValue from Generator
                id = dbTableGenerator.getName() + "." + dbFieldGen.getField();
                Object objGeneratedValue = mapValues.get(id);
                String delimValue = getDelimitedValue(id, dbFieldGen.getGenerator(), objGeneratedValue, mapDelimeters);
                if (objGeneratedValue != null)
                {
                    sbValues.append(delimValue);
                }
                sbValues.append(",");

                sbFields.append(dbFieldGen.getField());
                sbFields.append(",");
            }
        }
        sbFields.deleteCharAt(sbFields.length() - 1);
        sbFields.append(")");

        sbValues.deleteCharAt(sbValues.length() - 1);
        sbValues.append(")");

        sbQuery.append(" ");
        sbQuery.append(sbFields);
        sbQuery.append(" VALUES ");
        sbQuery.append(sbValues);

        return sbQuery.toString();
    }

    /**
     * The main methodused to generate the data
     */
    public void generate()
    {
        notifyInit();
        int numOfRecs = dbGenConfig.getCycles();
        List<DBTableGenerator> alDBGenerators = dbGenConfig.getDBTableGenerators();

        notifyMaxProgressValue(numOfRecs);

        //get the database connection
        Connection connection = initialiseDBConnection();

        //get the generators and the delimeters we will need
        Map hmIGenerators = new HashMap();
        Map hmDelimeters = new HashMap();
        initialiseGenerators(alDBGenerators, hmIGenerators, hmDelimeters);

        //the queries of each table are stored here
        List<String> aInsertQueries = new ArrayList();

        int i=0;
        boolean noError   = true;
        boolean cancelled = false;        
        while(i < numOfRecs && noError && !cancelled)
        {
            i++;
            //inform the observer
            cancelled = notifyProgrssUpdate("Cycle = " + i + "/" + numOfRecs, i );


            //in every cycle we are storing the values for all the tables and fields that have been generated
            Map mapValues = new HashMap();

            //initialise the array
            aInsertQueries.clear();

            //every time data for a table are generated, the table is removed from this hashmap
            Map mapTablesToGenerate = getTablesToGenerate();

            boolean skip;
            while (mapTablesToGenerate.size() > 0)
            {
                for (DBTableGenerator dbT : alDBGenerators)
                {
                    DBTableGenerator dbTable = (DBTableGenerator) mapTablesToGenerate.get(dbT.getName());
                    if (dbTable != null)
                    {
                        skip = false;
                        int maxRecs = dbTable.getMaxForeignKeyCardinality();
                        int j = 0;

                        if (checkForeignKeys(dbTable, mapValues) == false)
                        {
                            skip = true;
                        }
                        //if we made it this far, it means that:
                        // a) we do not have foreign keys in the table, OR
                        // b) we have foreign keys for which we have previously generatedvalues
                        while (j < maxRecs && skip == false)
                        {
                            j++;
                            String sQuery = generateQuery(dbTable, hmIGenerators, hmDelimeters, mapValues);
                            logger.debug(sQuery);
                            writeQuery(connection,sQuery);
                            mapTablesToGenerate.remove(dbT.getName());
                        }
                    }
                }
            }//skipped

            //write the quries
            //writeQueries(connection, aInsertQueries);
        }
        notifyEnd();
    }



    /**
     * Writes the queries in the db
     * @param connection
     * @param aQueries
     */
    private void writeQuery(Connection connection, String query)
    {
        try
        {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);
        } catch (Exception e)
        {
            logger.error("Error while writing query :" + query);
            observer.datageGenError("Error while writing query :" + query);
        }
    }

    /**
     * Writes the queries in the db
     * @param connection
     * @param aQueries
     */
    private void writeQueries(Connection connection, List<String> aQueries)
    {
        String q2 = null;
        try
        {
            Statement stmt = connection.createStatement();
            for (String query : aQueries)
            {
                q2 = query;
                stmt.executeUpdate(query);
            }
        } catch (Exception e)
        {
            logger.error("Error while writing query :" + q2);
            observer.datageGenError("Error while writing query :" + q2);
        }
    }


    public void registerObserver(ProgressUpdateObserver observer)
    {
        this.observer = observer;
    }

    public void unregisterObserver()
    {
        this.observer = null;
    }

    private void notifyInit()
    {
        if (observer == null)
        {
            return;
        }
        observer.dataGenStarted();
    }

    private void notifyMaxProgressValue(int max)
    {
        if (observer == null)
        {
            return;
        }
        observer.dataGenMaxProgressValue(max);
    }

    private boolean notifyProgrssUpdate(String msg, int progress)
    {
        if (observer == null)
        {
            return false;
        }

        return observer.dataGenProgressContinue(msg, progress);
    }

    private void notifyEnd()
    {
        if (observer == null)
        {
            return;
        }

        observer.dataGenEnd();
        observer = null;
    }

    private void datageGenError(String msg)
    {
        if (observer == null)
        {
            return;
        }

        observer.datageGenError(msg);
        observer.dataGenEnd();
    }
}
