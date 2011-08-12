/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

package generator.randomisers;

import java.util.LinkedHashMap;
import java.util.Random;
import org.apache.log4j.Logger;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;


public class NumShortRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(NumShortRandomiser.class);
    Random probability, nullGen, gen;
    int fromField[], toField[];
    int limits[];
    int nulls;
    
    /**
     * Creates a new instance of NumIntegerRandomiser
     */
    public NumShortRandomiser()
    {
    }
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {        
        String sRangesNum, sFromField, sToField, sPercentField, sNulls;
        int rangesNum, percentField[];
        LinkedHashMap hashMap;
        
        hashMap = ri.getProperties();
        sRangesNum  = (String) hashMap.get("rangesNum");
        sNulls = (String) hashMap.get("nullField");
        try
        {
            rangesNum = Integer.parseInt(sRangesNum);
            nulls = Integer.parseInt(sNulls);
            fromField = new int[rangesNum];
            toField = new int[rangesNum];
            percentField = new int[rangesNum];
            limits = new int[rangesNum+1];//space reserved for nulls
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values (1 - init)", e);
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
                
                fromField[i-1] = Integer.parseInt(sFromField);
                toField[i-1] = Integer.parseInt(sToField);
                percentField[i-1] = Integer.parseInt(sPercentField);
                limits[i] = limits[i-1] + percentField[i-1];                
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
        int range = toField[idxGen] - fromField[idxGen];
        int number = fromField[idxGen] + gen.nextInt( range + 1 );
        
        
        return Short.valueOf( (short)number );
    }
    
    public void destroy()
    {
    }
    
}
