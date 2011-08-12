/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


package generator.randomisers;
import generator.misc.Utils;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Vector;
import org.apache.log4j.Logger;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;


public class AnswerQuestionID implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(AnswerQuestionID.class);
    EnglishWordRandomiser englishWordGenerator;
    String questID;
    int idx, nextAction, countGens, maxGens;
    Random random;
    Vector vWords;
    
    
    
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {
        Utils utils;
        String sNull, sWidthMax, sWidthMin, sPunctuation;
        LinkedHashMap hashMap;
        
        hashMap = ri.getProperties();
        
        //load the dictionary
        utils  = new Utils();
        try
        {
            vWords = utils.readFile("c:\\question_data.csv");
        }
        catch(Exception e)
        {
            vWords = new Vector();
            vWords.add("Error!");
            logger.warn("Vector size is 0, error during while load file");
        }
        random = new Random(100);
        idx=-1;
        nextAction=999;       
    }
    
    public Object generate()
    {
        String oneWord;
        String aFields[];
        String qtype;
        String retValue=null;
        

        if(nextAction == 10)
        {
            retValue = questID;
            countGens++;
            if(countGens == maxGens)
                nextAction=999;
            return retValue;
        }
        
        if(nextAction == 20)
        {
            retValue=questID;
            nextAction=999;
            return retValue;
        }
        
        if(nextAction == 30)
        {
            retValue=questID;
            nextAction = 999;
            return retValue;
        }

        
        if(nextAction==999)
        {
            idx++;
            if(idx>=vWords.size())
                return null;
            oneWord = (String) vWords.elementAt(idx);
            aFields = oneWord.split(",");
            questID = aFields[0];
            qtype   = aFields[1];
            if( qtype.equalsIgnoreCase("1") || qtype.equalsIgnoreCase("2") )
            {
                retValue = questID;
                countGens = 1;
                maxGens   = 3 + random.nextInt(3);
                nextAction = 10;                
            } else if( qtype.equalsIgnoreCase("3") )
            {            
                retValue=questID;
                nextAction=20;
            }
            else if( qtype.equalsIgnoreCase("4") )
            {            
                retValue=questID;
                nextAction=30;
            }    
            else
            {
                retValue = "ERROR";
            }
        }
        
        return retValue;
    }
        
    
    public void destroy()
    {
    }
}
