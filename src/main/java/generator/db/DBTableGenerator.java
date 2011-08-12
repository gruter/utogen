/*
 * DBTableGenerators.java
 *
 * Created on 09 June 2007, 03:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package generator.db;

import generator.gui.graph.*;
import generator.db.DBField;
import generator.db.DBForeignKey;
import generator.db.DBTable;
import generator.misc.Constants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * Represents a table and its associated generators
 *
 */
public class DBTableGenerator extends DBTable
{

    private String generator;  //reserved for future use,(eg. Beans generator)
    private int generatorType; //reserved for future use (eg.On the fly defined generator,built-in, Bean generator)
    private List<DBFieldGenerator> dbTableGenFields;
    private List<DBForeignKey> dbForeignKeys;
    private Logger logger = Logger.getLogger(DBTableGenerator.class);

    public DBTableGenerator()
    {
        super();
        dbForeignKeys    = new ArrayList();
        dbTableGenFields = new ArrayList();
    }

    public DBTableGenerator(String name, Collection fields, String generator)
    {
        super(name, fields);
        this.generator = generator;
        dbTableGenFields = new ArrayList();
        dbForeignKeys = new ArrayList();
    }

    public DBTableGenerator(DBTable dbTable)
    {
        super(dbTable.getName(), dbTable.getDBFields());
        dbTableGenFields = new ArrayList();
        dbForeignKeys = new ArrayList();

        DBField dbField;
        DBFieldGenerator dbGenField;

        for (int i = 0; i < dbTable.getNumFields(); i++)
        {
            dbField = dbTable.getDBField(i);
            dbGenField = new DBFieldGenerator(dbField, "", Constants.GENERATOR_SIMPLE);
            dbTableGenFields.add(dbGenField);
        }
        this.generator = ""; //default is empty
        this.generatorType = Constants.GENERATOR_SIMPLE;
    }

    public void setFieldGenerator(String fieldName, String fieldGenerator)
    {
        boolean found = false;
        for (DBFieldGenerator dbFieldGen : dbTableGenFields)
        {
            if (dbFieldGen.getField().equalsIgnoreCase(fieldName))
            {
                dbFieldGen.setGenerator(fieldGenerator);
                found = true;
            }
        }
        if (found == false)
        {
            logger.error("Did not find a field for " + fieldGenerator);
        }
    }

    public void setFieldGenerator(int idx, String fieldGenerator)
    {
        DBFieldGenerator field = dbTableGenFields.get(idx);
        field.setGenerator(fieldGenerator);
        dbTableGenFields.set(idx, field);
    }

    public String getGenerator(int idx)
    {
        DBFieldGenerator field = dbTableGenFields.get(idx);
        return field.getGenerator();
    }

    public List<DBFieldGenerator> getDBFieldGenerators()
    {
        return dbTableGenFields;
    }

    public int getForeignKeysSize()
    {
        return dbForeignKeys.size();
    }

    public List<DBForeignKey> getForeignKeys()
    {
        if(dbForeignKeys==null)
        {
            return new ArrayList();
        }
        return dbForeignKeys;
    }


    public DBForeignKey getForeyKeyAt(int idx)
    {
        return dbForeignKeys.get(idx);
    }

    public String getForeignKeyForField(String fieldname)
    {
        String retVal = "";
        for (int i = 0; i < dbForeignKeys.size(); i++)
        {
            DBForeignKey dbForeignKey = (DBForeignKey)dbForeignKeys.get(i);

            if(dbForeignKey.getDetailsField().equalsIgnoreCase(fieldname))
            {
                retVal = dbForeignKey.getMasterTable() + "." + dbForeignKey.getMasterField();
            }
        }
        return retVal;
    }

    
    public void addForeignKey(DBForeignKey dbForeignKey)
    {
        if (dbForeignKeys == null)
        {
            dbForeignKeys = new ArrayList();
        }
        dbForeignKeys.add(dbForeignKey);
    }


    public void addUpdateForeignKey(DBForeignKey dbForeignKey)
    {
        if (dbForeignKeys == null)
        {
            dbForeignKeys = new ArrayList();
        }

        for (int i = dbForeignKeys.size()-1 ; i>0 ; i--)
        {
            DBForeignKey dbFKey = (DBForeignKey)dbForeignKeys.get(i);

            if(dbFKey.equals(dbForeignKey))
            {
                dbForeignKeys.remove(i);
            }
        }
        dbForeignKeys.add(dbForeignKey);
        
    }

    
    public int getMaxForeignKeyCardinality()
    {
        int maxSize = 1;

        for (int i = 0; i < dbForeignKeys.size(); i++)
        {
            DBForeignKey dbForeignKey = (DBForeignKey)dbForeignKeys.get(i);

            if(dbForeignKey.getToNum()>maxSize)
            {
                maxSize = dbForeignKey.getToNum();
            }
        }
        return maxSize;
    }

    public void clearForeignKeys()
    {
        if (dbForeignKeys == null)
        {
            dbForeignKeys = new ArrayList();
        }
        dbForeignKeys.clear();
    }


    public boolean equals(Object o)
    {
        DBTableGenerator oCompare;
        if (o instanceof DBTableGenerator == false)
        {
            return false;
        }
        oCompare = (DBTableGenerator) o;
        if (this.getName().equalsIgnoreCase(oCompare.getName()))
        {
            return true;
        }
        return false;
    }

    public int hashCode()
    {
        return this.getName().hashCode();
    }

    public void setForeignKeys(List<DBForeignKey> fkeys)
    {
        if(fkeys==null)
        {
            fkeys = new ArrayList();
        }
        dbForeignKeys = fkeys;
    }
}

