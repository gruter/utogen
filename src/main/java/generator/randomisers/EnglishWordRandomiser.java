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
import java.io.File;


public class EnglishWordRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(EnglishWordRandomiser.class);
    
    Random  nullGen, puncGen, gen;
    boolean lPunctuation;
    int     wordsMax, wordsMin, nulls;
    Vector   vWords;
    char[]  chPuncts = {'.', 
                        '?',
                        '!',
                        ',',
                        ';',
                        ' '};
    final int punctSize = chPuncts.length;
    
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {
        Utils utils;
        String sNull, sWidthMax, sWidthMin, sPunctuation;
        LinkedHashMap hashMap;
        
        hashMap = ri.getProperties();
        sNull  = (String) hashMap.get("nullField");
        sWidthMax = (String) hashMap.get("maxWidth");
        sWidthMin = (String) hashMap.get("minWidth");
        sPunctuation = (String) hashMap.get("punctuation");
        
        //load the dictionary
        utils  = new Utils();
        try
        {
            vWords = utils.readFile("conf/resources" + File.separator + "en_dictionary.txt");
        }catch(Exception e)
        {
            vWords = new Vector();
            vWords.add("Error!");
            logger.warn("Vector size is 0, error during while load file resources\\en_dictionary.txt");
        }
        try
        {
            nulls        = Integer.parseInt(sNull);
            wordsMax = Integer.parseInt(sWidthMax);
            wordsMin = Integer.parseInt(sWidthMin);
            lPunctuation = Boolean.parseBoolean(sPunctuation);
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values", e);
            nulls=0;
        } 
        puncGen  = new Random();
        nullGen  = new Random();
        gen      = new Random();        
    }

    public Object generate()
    {
        String oneWord;
        //check the nulls first
        int prob;
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        int maxGen = wordsMin + (gen.nextInt(wordsMax-wordsMin));
        StringBuffer sb = new StringBuffer(maxGen);        
        int i=0;
        while(i<maxGen)
        {
            prob    = gen.nextInt( vWords.size() );
            oneWord = (String)vWords.elementAt(prob);
            sb.append( oneWord );
            i = i + 1;
            
            //handle the punctuation,70% probability
            //".?!" characters are always followed by space and capital letter
            //" ;," characters are always followed by space
            //" " is followed by next character
            if( lPunctuation && puncGen.nextInt(10)>6 )
            {
                prob = gen.nextInt(punctSize);
                sb.append(chPuncts[prob]);
                if(chPuncts[prob]!=' ')
                {
                    sb.append(" ");
                    if(chPuncts[prob]=='.' || chPuncts[prob]!='?' || chPuncts[prob]!='!')
                    {
                        //another character required in capital
                        prob = gen.nextInt(vWords.size());
                        oneWord = (String)vWords.elementAt(prob);
                        String first = oneWord.substring(0,1);
                        first = first.toUpperCase();
                        oneWord = oneWord.substring(1);
                        oneWord = first + oneWord;      
                        sb.append( oneWord );
                        sb.append(" ");
                        i = i + 1;
                    }
                }
            }
            else
                sb.append(" ");
        }        
        sb.deleteCharAt( sb.length() -1 );
        return sb.toString();
    }

    public void destroy()
    {
    }
}
