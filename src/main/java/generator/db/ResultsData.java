/*
 * ResultsData.java
 *
 * Created on 27 May 2007, 02:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.db;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael
 */
public class ResultsData
{
    List lstData;
    int status;
    
    public ResultsData()
    {    
        lstData = new ArrayList();
        status  = 0;
    }

    public ResultsData(List<?> lstData, int status)
    {    
        this.lstData = lstData;
        status  = 0;
    }

    public ResultsData(int status)
    {    
        status  = 0;
    }
    
    
    public Object get(int idx)
    {
        return lstData.get(idx);
    }    

    public void add(Object obj)
    {
        lstData.add(obj);
    }    

    public int getStatus()
    {
        return status;
    }
    
    public int size()
    {
        return lstData.size();
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }    
    
    public List getData()
    {
        return lstData;
    }
    
    public void setData(List<?> lstData)
    {
        this.lstData = lstData;
    }
}
