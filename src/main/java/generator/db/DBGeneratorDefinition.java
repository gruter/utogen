/*
 * DBGeneratorDefinition.java
 *
 * Created on 10 November 2007, 17:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.db;

import generator.db.DBTableGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class DBGeneratorDefinition
{
    private String scenario;
    private String description;
    private String filename;            
    private String dbDriver;
    private String dbURL;
    private String user;
    private String password;
    private List<DBTableGenerator> dbTableGenerators;
    private int cycles;
    private boolean encInTransaction;
    
    
    public DBGeneratorDefinition()
    {
        
    }
    
    /** Creates a new instance of DBGeneratorDefinition */
    public DBGeneratorDefinition( String scenario, String description, String filename,
                                  String dbDriver, String dbURL, String user, String passwd,
                                  List<DBTableGenerator> dbTableGenerator, 
                                  int cycles, boolean encInTransaction )
    {
        
        this.setScenario(scenario);
        this.setDescription(description);
        this.setFilename(filename);
        this.setDbDriver(dbDriver);
        this.setDbURL(dbURL);
        this.setUser(user);
        this.setPassword(passwd);
        this.setDBTableGenerators(dbTableGenerator);
        this.cycles = cycles;
        this.encInTransaction = encInTransaction;
    }

    public String getScenario()
    {
        return scenario;
    }

    public void setScenario(String scenario)
    {
        this.scenario = scenario;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getDbDriver()
    {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver)
    {
        this.dbDriver = dbDriver;
    }

    public String getDbURL()
    {
        return dbURL;
    }

    public void setDbURL(String dbURL)
    {
        this.dbURL = dbURL;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public List<DBTableGenerator> getDBTableGenerators()
    {
        return dbTableGenerators;
    }

    public void setDBTableGenerators(List<DBTableGenerator> alDBTableGenerators)
    {
        this.dbTableGenerators = alDBTableGenerators;
    }

    public int getCycles()
    {
        return cycles;
    }

    public void setCycles(int cycles)
    {
        this.cycles = cycles;
    }

    public boolean isEncInTransaction()
    {
        return encInTransaction;
    }

    public void setEncInTransaction(boolean encInTransaction)
    {
        this.encInTransaction = encInTransaction;
    }
}
