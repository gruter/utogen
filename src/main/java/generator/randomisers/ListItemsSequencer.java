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

public class ListItemsSequencer implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(ListItemsSequencer.class);
    Random nullGen;
    Vector<String> vItems;
    boolean fileSelected;    
    int idx, nulls;
    

    public void setRandomiserInstance(RandomiserInstance ri)
    {        
        String inputSource, sRangesNum, sItem, sNulls;
        int rangesNum;
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
            }
            catch(Exception e)
            {
                logger.warn(ri.getName() +": Error setting the numerical values (1 - init)", e);
                nulls=0; rangesNum=0;
            }
            
            vItems = new Vector();
            for(int i=1; i<=rangesNum; i++)
            {
                try
                {
                    sItem = (String) hashMap.get("itemField"+ (i-1) );
                    vItems.add(sItem);           
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
                vItems.add("ERROR");
                logger.warn(ri.getName() +": Vector size is 0:");
            }
            fileSelected = true;
        }
        nullGen     = new Random();
        idx = -1;
    }
    
    public Object generate()
    {
        //check the nulls first
        int prob;
        
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        idx++;
        if(idx>=vItems.size())
            idx=0;
        return vItems.elementAt(idx);
    }
    
    public void destroy()
    {
    }
    
}