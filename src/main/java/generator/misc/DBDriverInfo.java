/*
 * DBDriverInfo.java
 *
 * Created on 19 May 2007, 15:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.misc;

/**
 *
 * @author Michael
 */
public class DBDriverInfo
{
    private String name;
    private String conString;
    private String loadClass;
    private String locationClass;
    private String charDelimiter;
    private String dateDelimiter;
    private String timeDelimiter;
    private String timestampDelimiter;
    private int    status;
    //1: ok, 2: not ok
    
    
    public DBDriverInfo()
    {
   
    }

    public DBDriverInfo( String name,
                         String conString,
                         String loadClass,
                         String locationClass,
                         String charDelimiter,
                         String dateDelimiter,
                         String timeDelimiter,
                         String timeStampDelimiter,
                         int status)
    {
        this.name = name;
        this.conString = conString;
        this.loadClass = loadClass;
        this.locationClass = locationClass;
        this.charDelimiter = charDelimiter;
        this.dateDelimiter = dateDelimiter;
        this.timeDelimiter = timeDelimiter;
        this.timestampDelimiter = timeDelimiter;
        this.status = status;        
                
    }    


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getConString()
    {
        return conString;
    }

    public void setConString(String conString)
    {
        this.conString = conString;
    }

    public String getLoadClass()
    {
        return loadClass;
    }

    public void setLoadClass(String loadClass)
    {
        this.loadClass = loadClass;
    }

    public String getLocationClass()
    {
        return locationClass;
    }

    public void setLocationClass(String locationClass)
    {
        this.locationClass = locationClass;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public String toString()
    {
        return name;
    }

    public String getCharDelimiter()
    {
        return charDelimiter;
    }

    public void setCharDelimiter(String charDelimiter)
    {
        this.charDelimiter = charDelimiter;
    }

    public String getDateDelimiter()
    {
        return dateDelimiter;
    }

    public void setDateDelimiter(String dateDelimiter)
    {
        this.dateDelimiter = dateDelimiter;
    }

    public String getTimeDelimiter()
    {
        return timeDelimiter;
    }

    public void setTimeDelimiter(String timeDelimiter)
    {
        this.timeDelimiter = timeDelimiter;
    }

    public String getTimestampDelimiter()
    {
        return timestampDelimiter;
    }

    public void setTimestampDelimiter(String timestampDelimiter)
    {
        this.timestampDelimiter = timestampDelimiter;
    }
}
