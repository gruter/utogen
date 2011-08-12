/*
 * ApplicationContext.java
 *
 * Created on 09 June 2007, 02:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.misc;

import generator.db.SQLJavaMapping;
import generator.extenders.RandomiserInstance;
import java.util.Vector;

/**
 *
 * @author Michael
 */
public class ApplicationContext
{
    private static ApplicationContext context = null;
    private static Vector<RandomiserInstance> vRI;
    private static Vector<RandomiserType> vRT;
    private static Vector<DataFileDefinition> vDFD;
    private static Vector<DBDriverInfo> vDBDriverInfo;
    private static Vector<DBFileDefinition> vDBFileDefinition;
    private static Vector<SQLJavaMapping> vSQLJavaMapping;
    private static Utils utils;


    
    public static ApplicationContext getInstance()
    {
        if(context==null)
        {
            context = new ApplicationContext();
        }

        return context;
    }

    /** Creates a new instance of ApplicationContext */
    private ApplicationContext()
    {
        if(utils==null)
        {
            utils = new Utils();
        }
    }


    public void refreshRandomiserInstances()
    {
        
        vRI = utils.loadRandomiserInstances();
    }
    
    public  void refreshRandomiserTypes()
    {
        vRT = utils.loadRandomiserTypes();
    }


    public void refreshFileDefinitions()
    {
        vDFD = utils.loadDataFileDefinitions();
    }

    public void refreshDBDefinitions()
    {
        vDBFileDefinition = utils.loadDBFileDefinitions();
    }
    
    public void refreshDriverInfo()
    {
        vDBDriverInfo = utils.loadDBDriversInfo();
    }

    public void refreshSQLJavaMappings()
    {
        vSQLJavaMapping = utils.loadSQLJavaMappings();
    }
    
    public Vector<RandomiserInstance> getRandomiserInstances()
    {
        return vRI;
    }

    public Vector<RandomiserType> getRandomiserTypes()
    {
        return vRT;
    }

    public Vector<DBDriverInfo> getDriverInfo()
    {
        return vDBDriverInfo;
    }

    public Vector<DataFileDefinition> getDFD()
    {
        return vDFD;
    }

    public void setDFD( Vector<DataFileDefinition> vDFDs)
    {
        vDFD = vDFDs;
    }


    public Vector<SQLJavaMapping> getSQLJavaMappings()
    {
        return vSQLJavaMapping;
    }


    public Vector<DBFileDefinition> getDBD()
    {
        return vDBFileDefinition;
    }

    public void setDBD( Vector<DBFileDefinition> vDBDs)
    {
        vDBFileDefinition = vDBDs;
    }    
    
    
    
    public void setRandomiserInstances(Vector<RandomiserInstance> vRandomInstances)
    {
        vRI = vRandomInstances;
    }

    public void setDBDriver(Vector<DBDriverInfo> vDBDInfo)
    {
        vDBDriverInfo = vDBDInfo;
    }


}
