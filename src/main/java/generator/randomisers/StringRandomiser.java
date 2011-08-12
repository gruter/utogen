/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

package generator.randomisers;
import java.util.LinkedHashMap;
import java.util.Random;
import org.apache.log4j.Logger;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;

public class StringRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(StringRandomiser.class);
    
    Random  nullGen, puncGen, gen;
    boolean lPunctuation;
    int     textWidthMax, textWidthMin, nulls;
    char[]  chSmallLetters = new char[26];
    char[]  chCapsLetters  = new char[26];
    char[]  chPuncts = {'.', 
                        '?',
                        '!',
                        ',',
                        ';',
                        ' '};
    final int punctSize = chPuncts.length;
    
    /** Creates a new instance of RandomTextRandomiser */
    public StringRandomiser()
    {
    }

    public void setRandomiserInstance(RandomiserInstance ri)
    {
        String sNull, sWidthMax, sWidthMin, sPunctuation;
        LinkedHashMap hashMap;
        
        hashMap = ri.getProperties();
        sNull  = (String) hashMap.get("nullField");
        sWidthMax = (String) hashMap.get("maxWidth");
        sWidthMin = (String) hashMap.get("minWidth");
        sPunctuation = (String) hashMap.get("punctuation");
        try
        {
            nulls        = Integer.parseInt(sNull);
            textWidthMax = Integer.parseInt(sWidthMax);
            textWidthMin = Integer.parseInt(sWidthMin);
            lPunctuation = Boolean.parseBoolean(sPunctuation);
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values", e);
            nulls=0;
        } 
        chSmallLetters[0] = 'a';
        chCapsLetters[0]  = 'A';
        for(int i=1; i<26; i++)
        {
            chSmallLetters[i] = (char)('a'+ i);
            chCapsLetters[i]  = (char)('A'+ i);
        }
        puncGen  = new Random();
        nullGen  = new Random();
        gen      = new Random();        
    }

    public Object generate()
    {

        //check the nulls first
        int prob;
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        int maxGen = textWidthMin + (gen.nextInt(textWidthMax-textWidthMin));
        StringBuffer sb = new StringBuffer(maxGen);        
        int i=0;
        while(i<maxGen)
        {
            prob = gen.nextInt(26);
            sb.append(chSmallLetters[prob]);
            
            //handle the punctuation. 
            //".?!" characters are always followed by space and capital letter
            //" ;," characters are always followed by space
            //" " is followed by next character
            if( lPunctuation && puncGen.nextInt(10)>6 )
            {
                i++; //punctuation increase
                prob =  gen.nextInt(punctSize);
                sb.append(chPuncts[prob]);
                if(chPuncts[prob]!=' ')
                {
                    sb.append(" ");
                    i++;//space needed after punctuation
                    if(chPuncts[prob]=='.' || chPuncts[prob]!='?' || chPuncts[prob]!='!')
                    {
                        //another character required in capital
                        prob = gen.nextInt(26);
                        sb.append(chCapsLetters[prob]);
                        i++;
                    }
                }
            }
            i++;
        }
        if(i>maxGen)
            sb.setLength(maxGen);
        return sb.toString();
    }

    public void destroy()
    {
    }
    
}
