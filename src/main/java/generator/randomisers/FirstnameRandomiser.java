/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


package generator.randomisers;
import generator.misc.Utils;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Vector;
import org.apache.log4j.Logger;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;
import java.io.File;


public class FirstnameRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(FirstnameRandomiser.class);
    int nulls;
    int iFNames;
    Random nullGen, gen1;    
    Vector<String> vFirstNames;
            
    
    /** Creates a new instance of FirstnameRandomiser */
    public FirstnameRandomiser()
    {
    }

    public void setRandomiserInstance(RandomiserInstance ri)
    {
        LinkedHashMap hashMap;
        String sNull, sFnameSeed;
        long   fnameSeed;
        

        hashMap = ri.getProperties();
        sFnameSeed  = (String) hashMap.get("FirstnameSeedField");
        sNull  = (String) hashMap.get("nullField");
        try
        {
            nulls = Integer.parseInt(sNull);
            fnameSeed = Long.parseLong(sFnameSeed);
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values", e);
            fnameSeed = System.currentTimeMillis();
        }        
        
        Utils utils = new Utils();
        
        logger.debug("loading file1");
        try
        {
            vFirstNames = utils.readFile("conf/resources" + File.separator + "en_firstnames.txt");
        }
        catch(FileNotFoundException fnfe)
        {
            logger.error("File en_firstnames not found", fnfe);
        }
        iFNames = vFirstNames.size();

        nullGen = new Random();
        gen1        = new Random(fnameSeed);
        
        logger.debug("loading files.Done");        
    }

    public Object generate()
    {
        //first "consume" the generator nextInt,
        // so as to keep compatibility with email randomiser

        int i2 = gen1.nextInt(iFNames);        
        
         //check the nulls
        int prob;
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        int len;
        String fname;

        
        //get firstname, avoid the last two chars ,f or ,m
        fname = vFirstNames.elementAt(i2);
        len   = fname.length();
        fname = fname.substring(0, len-2);
        
        return fname;        
    }

    public void destroy()
    {
    }
    
}
