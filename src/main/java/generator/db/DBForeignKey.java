/*
 * DBForeigkKeys.java
 *
 * Created on 20 July 2008, 16:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.db;

/**
 *
 * @author Administrator
 */
public class DBForeignKey
{
    private String masterTable, detailsTable;
    private String masterField, detailsField;
    private int fromNum, toNum, cardinalityType;


    public DBForeignKey()
    {

    }


    public DBForeignKey(String masterTable, String masterField, String detailsTable, String detailsField, String from, String to)
    {
        this.masterTable = masterTable;
        this.masterField = masterField;
        this.detailsTable = detailsTable;
        this.detailsField = detailsField;
        try
        {
            this.fromNum = Integer.parseInt(from);
        }
        catch(Exception e)
        {
            this.fromNum = -1;
        }
        try
        {
            this.toNum = Integer.parseInt(to);
        }
        catch(Exception e)
        {
            this.toNum = -1;
        }

        this.cardinalityType = 0;
    }


    
    /**
     * @return the masterTable
     */
    public String getMasterTable()
    {
        return masterTable;
    }

    public boolean hasValues()
    {
        if( "".equals(masterTable) || "".equals(masterField) ||
            "".equals(detailsTable) || "".equals(detailsField) )
        {
            return false;
        }
        return true;
    }

    /**
     * @param masterTable the masterTable to set
     */
    public void setMasterTable(String masterTable)
    {
        this.masterTable = masterTable;
    }

    /**
     * @return the detailsTable
     */
    public String getDetailsTable()
    {
        return detailsTable;
    }

    /**
     * @param detailsTable the detailsTable to set
     */
    public void setDetailsTable(String detailsTable)
    {
        this.detailsTable = detailsTable;
    }

    /**
     * @return the masterField
     */
    public String getMasterField()
    {
        return masterField;
    }

    /**
     * @param masterField the masterField to set
     */
    public void setMasterField(String masterField)
    {
        this.masterField = masterField;
    }

    /**
     * @return the detailsField
     */
    public String getDetailsField()
    {
        return detailsField;
    }

    /**
     * @param detailsField the detailsField to set
     */
    public void setDetailsField(String detailsField)
    {
        this.detailsField = detailsField;
    }

    /**
     * @return the fromNum
     */
    public int getFromNum()
    {
        return fromNum;
    }

    /**
     * @param fromNum the fromNum to set
     */
    public void setFromNum(int fromNum)
    {
        this.fromNum = fromNum;
    }

    /**
     * @return the toNum
     */
    public int getToNum()
    {
        return toNum;
    }

    /**
     * @param toNum the toNum to set
     */
    public void setToNum(int toNum)
    {
        this.toNum = toNum;
    }

    /**
     * @return the cardinalityType
     */
    public int getCardinalityType()
    {
        return cardinalityType;
    }

    /**
     * @param cardinalityType the cardinalityType to set
     */
    public void setCardinalityType(int cardinalityType)
    {
        this.cardinalityType = cardinalityType;
    }

    public boolean equals(Object o)
    {
        if(o instanceof DBForeignKey==false || o==null)
        {
            return false;
        }

        DBForeignKey fk = (DBForeignKey) o;
        if( this.getMasterTable().equalsIgnoreCase(fk.getMasterTable())  &&
            this.getMasterField().equalsIgnoreCase(fk.getMasterField())  &&
            this.getDetailsTable().equalsIgnoreCase(fk.getDetailsTable()) &&
            this.getDetailsField().equalsIgnoreCase(fk.getDetailsField()) )
        {
            return true;
        }
        return false;
    }

    public int hashCode()
    {
        return this.getMasterTable().hashCode() +
               this.getMasterField().hashCode() +
               this.getDetailsTable().hashCode() +
               this.getDetailsField().hashCode();
    }
    
}
