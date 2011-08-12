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


public class LastnameRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(FirstnameRandomiser.class);
    int nulls;
    int iLNames;
    Random nullGen, gen1;
    Vector<String> vFirstNames;
    
    
    /** Creates a new instance of FirstnameRandomiser */
    public LastnameRandomiser()
    {
    }
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {
        LinkedHashMap hashMap;
        String sNull, sLnameSeed;
        long   lnameSeed;
        
        
        hashMap = ri.getProperties();
        sLnameSeed  = (String) hashMap.get("LastnameSeedField");
        sNull  = (String) hashMap.get("nullField");
        try
        {
            nulls = Integer.parseInt(sNull);
            lnameSeed = Long.parseLong(sLnameSeed);
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values", e);
            lnameSeed = System.currentTimeMillis();
        }
        
        Utils utils = new Utils();
        
        logger.debug("loading file1");
        try
        {
            vFirstNames = utils.readFile("conf/resources" + File.separator + "en_lastnames.txt");
        }
        catch(FileNotFoundException fnfe)
        {
            logger.error("File en_lastnames.txt was not found", fnfe);
        }
        iLNames = vFirstNames.size();
        
        nullGen = new Random();
        gen1    = new Random(lnameSeed);
        
        logger.debug("loading files.Done");
    }
    
    public Object generate()
    {
        //first "consume" the generator nextInt,
        // so as to keep compatibility with email randomiser
        int i2 = gen1.nextInt(iLNames);

        //check the nulls
        int prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        int len;

        String lname;
        
        //get lastname, avoid the last two chars ,f or ,m
        lname = vFirstNames.elementAt(i2);
        len   = lname.length();
        
        return lname;
    }
    
    public void destroy()
    {
    }
    
}
