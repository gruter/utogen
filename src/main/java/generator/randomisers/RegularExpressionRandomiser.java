/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

package generator.randomisers;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Random;
import org.apache.log4j.Logger;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;
import org.databene.benerator.Generator;
import org.databene.benerator.factory.GeneratorFactory;



public class RegularExpressionRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(RegularExpressionRandomiser.class);
    LinkedHashMap hashMap;
    Random probability, nullGen;
    String regExpression, sNulls, sMin, sMax;
    int nulls, min, max;
    Generator<String> generator;
    
    /**
     * Creates a new instance of RegularExpressionRandomiser
     */
    public RegularExpressionRandomiser()
    {
    }
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {        
        hashMap = ri.getProperties();
        regExpression  = (String) hashMap.get("expression");
        sNulls = (String) hashMap.get("nullField");
        sMin = (String) hashMap.get("minWidth");
        sMax = (String) hashMap.get("maxWidth");
        try
        {
            nulls = Integer.parseInt(sNulls);
            min = Integer.parseInt(sMin);
            max = Integer.parseInt(sMax);
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values (1 - init)", e);
            nulls=0; min=0; max=10; regExpression="[a-z]*";
        }
        probability = new Random();
        nullGen     = new Random();
        
        Locale locale = new Locale("GBR");
        //get the generator
        generator = GeneratorFactory.getRegexStringGenerator(regExpression, min, max, null, nulls);
    }

    
    public Object generate()
    {                        
        return generator.generate();
    }
    
    public void destroy()
    {
        generator.close();
    }
    
}
