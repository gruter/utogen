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


public class AnswerQuestionIDText implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(AnswerQuestionIDText.class);
    EnglishWordRandomiser englishWordGenerator;
    
    int idx, nextAction, countGens, maxGens;
    Random random;
    Vector vWords;
    
    
    public AnswerQuestionIDText()
    {
        RandomiserInstance ri = new RandomiserInstance();
        LinkedHashMap hashMap = new LinkedHashMap();

        hashMap.put("nullField","0");
        hashMap.put("maxWidth", "5");
        hashMap.put("minWidth", "1");
        hashMap.put("punctuation", "true");
        ri.setProperties(hashMap);
        englishWordGenerator = new EnglishWordRandomiser();
        englishWordGenerator.setRandomiserInstance( ri );       
    }
    
    
    
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
            retValue = generateText();
            countGens++;
            if(countGens == maxGens)
                nextAction=999;
            return retValue;
        }
        
        if(nextAction == 20)
        {
            retValue="False";
            nextAction=999;
            return retValue;
        }
        
        if(nextAction == 30)
        {
            retValue="No";
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
            qtype   = aFields[1];
            if( qtype.equalsIgnoreCase("1") || qtype.equalsIgnoreCase("2") )
            {
                retValue = generateText();
                countGens = 1;
                maxGens   = 3 + random.nextInt(3);
                nextAction = 10;                
            }
            if( qtype.equalsIgnoreCase("3") )
            {            
                retValue="True";
                nextAction=20;
            }
            if( qtype.equalsIgnoreCase("4") )
            {            
                retValue="Yes";
                nextAction=30;
            }            
        }
        
        return retValue;
    }
    
    
    public String generateText()
    {
        return (String) englishWordGenerator.generate();
        
    }
    
    public void destroy()
    {
    }
}
