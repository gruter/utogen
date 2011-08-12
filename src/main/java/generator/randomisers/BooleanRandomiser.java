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


public class BooleanRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(BooleanRandomiser.class);
    Random probability, nullGen, gen;
    int nulls;
    int limits[];
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {        
        String sTrue, sFalse, sNull;
        int trues, falses;
        LinkedHashMap hashMap;
        
        hashMap = ri.getProperties();
        sTrue  = (String) hashMap.get("trueField");
        sFalse = (String) hashMap.get("falseField");
        sNull  = (String) hashMap.get("nullField");
        try
        {
            nulls = Integer.parseInt(sNull);
            trues = Integer.parseInt(sTrue);
            falses= Integer.parseInt(sFalse);
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values", e);
            nulls=0; trues=0; falses=0;
        }
        limits = new int[2];
        limits[0] = trues;
        limits[1] = limits[0] + falses;
        
        probability = new Random();
        gen         = new Random();
        nullGen     = new Random();
    }
    
    
    public Object generate()
    {
        //check the nulls first
        int prob;
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        prob = gen.nextInt(100);
        int genField = 0;
        
        if(prob>=0 && prob<limits[0])
            return Boolean.valueOf(true);
        
        return Boolean.valueOf(false);
    }
    
    public void destroy()
    {
    }
}
