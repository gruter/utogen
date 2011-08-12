/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

package generator.randomisers;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Random;
import org.apache.log4j.Logger;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;


public class NumDoubleRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(NumDoubleRandomiser.class);
    Random probability, nullGen, gen;
    double fromField[], toField[];
    int limits[];
    int nulls;
    DecimalFormat df;
    
    /**
     * Creates a new instance of NumDoubleRandomiser
     */
    public NumDoubleRandomiser()
    {
    }

    public void setRandomiserInstance(RandomiserInstance ri)
    {        
        String sRangesNum, sFromField, sToField, sPercentField, sPrecision, sNulls;
        int rangesNum, percentField[], precision;
        LinkedHashMap hashMap;
        
        hashMap = ri.getProperties();
        sRangesNum  = (String) hashMap.get("rangesNum");
        sNulls = (String) hashMap.get("nullField");
        sPrecision = (String) hashMap.get("Precision");
        try
        {
            rangesNum = Integer.parseInt(sRangesNum);
            nulls = Integer.parseInt(sNulls);
            fromField = new double[rangesNum];
            toField = new double[rangesNum];
            percentField = new int[rangesNum];
            precision = Integer.parseInt(sPrecision);
            limits = new int[rangesNum+1];//space reserved for nulls
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values (1 - init)", e);
            nulls=0; rangesNum=0; precision=1;
            fromField = null; toField = null; percentField=null;
        }
        
        limits[0] = 0;
        for(int i=1; i<=rangesNum; i++)
        {
            try
            {
                sFromField = (String) hashMap.get("fromField"+ (i-1) );
                sToField = (String) hashMap.get("toField"+ (i-1) );
                sPercentField = (String) hashMap.get("percentField"+ (i-1) );
                
                fromField[i-1] = Double.parseDouble(sFromField);
                toField[i-1] = Double.parseDouble(sToField);
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
        //create the format string
        String sDecimalPart ="";
        for(int i=0; i<precision; i++)
            sDecimalPart+="0";
        df = new DecimalFormat("."+sDecimalPart);
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
        double range = toField[idxGen] - fromField[idxGen];
        double number = fromField[idxGen] + gen.nextDouble() * range;
        String sDouble = df.format( number );
        Double retVal;
        try
        {
            retVal = Double.valueOf(sDouble);
        }
        catch(Exception e)
        {
            retVal = Double.valueOf(-1);
        }
        
        return retVal;      
    }

    public void destroy()
    {
    }
    
}
