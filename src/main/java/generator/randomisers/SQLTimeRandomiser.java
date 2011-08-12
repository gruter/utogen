/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

package generator.randomisers;
import generator.misc.Utils;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Random;
import org.apache.log4j.Logger;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;
import java.sql.Date;


public class SQLTimeRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(SQLTimeRandomiser.class);
    Random probability, nullGen, gen;
    long fromField[], toField[], diffMillis[];
    int limits[];
    int nulls;
    
    
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {
        String sRangesNum, sFromField, sToField, sPercentField, sNulls;
        
        //i need these gregorian cals. so i can pre-compute the days between them
        Time timeFromField, timeToField;
        
        int rangesNum, percentField[];
        LinkedHashMap hashMap;
        Utils utils = new Utils();
        
        hashMap = ri.getProperties();
        sRangesNum  = (String) hashMap.get("rangesNum");
        sNulls = (String) hashMap.get("nullField");
        try
        {
            rangesNum = Integer.parseInt(sRangesNum);
            nulls = Integer.parseInt(sNulls);
            fromField = new long[rangesNum];
            toField = new long[rangesNum];
            diffMillis= new long[rangesNum];
            percentField = new int[rangesNum];
            limits = new int[rangesNum+1];//space reserved for nulls
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical-date values (1 - init)", e);
            nulls=0; rangesNum=0; fromField = null; toField = null; percentField=null;
        }
        
        limits[0] = 0;
        for(int i=1; i<=rangesNum; i++)
        {
            try
            {
                sFromField = (String) hashMap.get("fromField"+ (i-1) );
                sToField = (String) hashMap.get("toField"+ (i-1) );
                sPercentField = (String) hashMap.get("percentField"+ (i-1) );
                
                fromField[i-1] = Time.valueOf(sFromField).getTime();
                toField[i-1] = Time.valueOf(sToField).getTime();
                percentField[i-1] = Integer.parseInt(sPercentField);
                limits[i] = limits[i-1] + percentField[i-1];
               
                
                diffMillis[i-1] = toField[i-1] - fromField[i-1];
            }
            catch(Exception e)
            {
                logger.warn("Error setting the numerical values (2 - Loop, index="+i+")", e);
            }
        }

        probability = new Random();
        nullGen     = new Random();
        gen         = new Random();
    }
    
    
    public Object generate()
    {
        //check the nulls first
        int prob;
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        
        long number;      //generated timestamp is stored as long
        
        //find which row from the table to use
        prob = probability.nextInt(100);
        int idxGen = 0;
        for(int i=1; i<limits.length; i++)
        {
            if(prob>=limits[i-1] && prob<limits[i])
            {
                idxGen=i-1;
                break;
            }
        }

        //first, generate an appropriate random number of days
        number = fromField[idxGen] + gen.nextInt( (int) diffMillis[idxGen]+1 );

        return new Time(number);
    }
    
    
    public void destroy()
    {
    }
    
    
}
