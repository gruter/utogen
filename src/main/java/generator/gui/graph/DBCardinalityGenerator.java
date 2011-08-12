/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package generator.gui.graph;

import generator.db.DBTableGenerator;
import java.util.List;

/**
 * The object bewteen two tables
 * 
 * @author michael
 */
public class DBCardinalityGenerator {

    DBTableGenerator fromTable, toTable;
    List<String> fromTableKeys;
    String relation;
    private int fromNum;
    private int toNum;
    private int cardinalityType;


    public DBCardinalityGenerator(DBTableGenerator fromTable, DBTableGenerator toTable)
    {
        this.fromTable = fromTable;
        this.toTable = toTable;

    }

    public void setRelation(String relation)
    {
        this.relation = relation;
    }
    
    String getRelation()
    {
        return relation;
    }


    public DBTableGenerator getFromTable()
    {
        return fromTable;
    }

    public DBTableGenerator getToTable()
    {
        return toTable;
    }

    public void setForeignKeys(List<String> fKeys)
    {
        this.fromTableKeys = fKeys;
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
}
