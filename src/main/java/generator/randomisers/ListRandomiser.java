/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


package generator.randomisers;

import generator.misc.Utils;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Vector;
import org.apache.log4j.Logger;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;

public class ListRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(ListRandomiser.class);
    Random probability, nullGen, gen;
    Vector<String> vItems;
    boolean fileSelected;
    int limits[];
    int nulls;
    
    /**
     * Creates a new instance of NumIntegerRandomiser
     */
    public ListRandomiser()
    {
    }
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {        
        String inputSource, sRangesNum, sItem, sPercentField, sNulls;
        int rangesNum, percentField[];
        LinkedHashMap hashMap;
        
        hashMap = ri.getProperties();
        inputSource = (String) hashMap.get("inputSource");
        if(inputSource.toLowerCase().equalsIgnoreCase("list"))
        {
            sRangesNum  = (String) hashMap.get("rangesNum");
            sNulls = (String) hashMap.get("nullField");
            try
            {
                rangesNum = Integer.parseInt(sRangesNum);
                nulls = Integer.parseInt(sNulls);
                percentField = new int[rangesNum];
                limits = new int[rangesNum+1];//space reserved for first pos.
            }
            catch(Exception e)
            {
                logger.warn(ri.getName() +": Error setting the numerical values (1 - init)", e);
                nulls=0; rangesNum=0; percentField=null;
            }
            
            limits[0] = 0; vItems = new Vector();
            for(int i=1; i<=rangesNum; i++)
            {
                try
                {
                    sItem = (String) hashMap.get("itemField"+ (i-1) );
                    vItems.add(sItem);
                    sPercentField = (String) hashMap.get("percentField"+ (i-1) );
                    
                    percentField[i-1] = Integer.parseInt(sPercentField);
                    if(percentField[i-1]==-1)
                        percentField[i-1] = 100 / rangesNum;
                    limits[i] = limits[i-1] + percentField[i-1];
                }
                catch(Exception e)
                {
                    logger.warn(ri.getName() +": Error setting the numerical values (2 - Loop, index="+i+")", e);
                }
            }
            fileSelected = false;
        }
        else
        {
            Utils utils = new Utils();
            String inputFile = (String) hashMap.get("inputFile");
            try
            {
                vItems = utils.readFile(inputFile);
            }
            catch(Exception e)
            {
                logger.error(ri.getName() +": could not locate file:"+inputFile,e);
            }
            if(vItems.size()==0)
            {
                vItems.add("ERROR:Vector_size_is_0");
                logger.warn(ri.getName() +": Vector size is 0:");
            }
            fileSelected = true;
        }
        probability = new Random();
        nullGen     = new Random();
        gen         = new Random();
    }
    
    public Object generate()
    {
        //check the nulls first
        int prob;
        int idxGen = 0;
        
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        if(fileSelected==false)
        {
            prob = probability.nextInt(100);
            for(int i=1; i<limits.length; i++)
            {
                if(prob>=limits[i-1] && prob<limits[i])
                {
                    idxGen=i-1;
                    break;
                }
            }
        }
        else
        {
            idxGen = gen.nextInt( vItems.size() );
        }
        return vItems.elementAt(idxGen);
    }
    
    public void destroy()
    {
    }
    
}