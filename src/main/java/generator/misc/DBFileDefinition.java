/*
 * DBFileDefinition.java
 *
 * Created on 18 October 2008, 12:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.misc;

/**
 *
 * @author Michael
 */
public class DBFileDefinition
{
    private String scenario;
    private String description;
    private String linkFileDB;
    
    /** Creates a new instance of DBFileDefinition */
    public DBFileDefinition()
    {
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

    public String getLinkFileDB()
    {
        return linkFileDB;
    }

    public void setLinkFileDB(String linkFileDB)
    {
        this.linkFileDB = linkFileDB;
    }
    
    public String toString()
    {
        return scenario;        
    }    
}
