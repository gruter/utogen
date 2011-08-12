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



public class UniqueStringRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(UniqueStringRandomiser.class);
    int nulls, idx, extra;
    char[]  chCapsLetters  = new char[26];
    Random nullGen, gen;     
    
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {        
        String sCounter, sNull;
        LinkedHashMap hashMap;
        
        hashMap  = ri.getProperties();
        sNull    = (String) hashMap.get("nullField");
        try
        {
            nulls    = Integer.parseInt(sNull);
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values", e);
            nulls=0;
        }
        
        chCapsLetters[0]  = 'A';
        for(int i=1; i<26; i++)
            chCapsLetters[i]  = (char)('A'+ i);
        idx = -1;
        extra = -1;
        nullGen = new Random();
        gen     = new Random();
    }
    
    
    public Object generate()
    {        
        //check the nulls first
        int prob;
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
              
        idx++;
        if(idx>25)
            idx=0;
        
        extra++;
        
        return ("" + System.currentTimeMillis() ) + chCapsLetters[idx] + extra;
    }
    
    
    public void destroy()
    {
    }
}

