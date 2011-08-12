/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

package generator.randomisers;
import java.util.LinkedHashMap;
import java.util.Random;
import org.apache.log4j.Logger;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserPanel;
import generator.extenders.RandomiserInstance;



public class LongSequencer implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(LongSequencer.class);
    int nulls;
    long counter;
    Random nullGen;

    
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {        
        String sCounter, sNull;
        LinkedHashMap hashMap;
        
        hashMap  = ri.getProperties();
        sCounter = (String) hashMap.get("counterField");
        sNull    = (String) hashMap.get("nullField");
        try
        {
            nulls    = Integer.parseInt(sNull);
            counter = Long.parseLong(sCounter);
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values", e);
            nulls=0; counter=0;
        }
        nullGen = new Random();
    }
    
    
    public Object generate()
    {
        //check the nulls first
        int prob;
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        Long countLong = Long.valueOf(counter);
        
        counter++;
        
        return countLong;
    }
    
    
    public void destroy()
    {
    }
}

