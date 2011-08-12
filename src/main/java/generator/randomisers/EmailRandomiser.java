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


public class EmailRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(EmailRandomiser.class);
    int nulls;
    int iTLDs, iFNames, iLNames, iProvid;
    Random nullGen, gen1, gen2, gen3;
    Vector<String> vTopDomains, vFirstNames, vLastNames, vProviders;
    String connector[] = {"",".","_"};
    
    /** Creates a new instance of EmailRandomiser */
    public EmailRandomiser()
    {
    }
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {
        long fnameSeed, lnameSeed;
        LinkedHashMap hashMap;
        String sNull, sFnameSeed, sLnameSeed;
        
        hashMap = ri.getProperties();
        sFnameSeed  = (String) hashMap.get("FirstnameSeedField");
        sLnameSeed = (String) hashMap.get("LastnameSeedField");
        sNull  = (String) hashMap.get("nullField");
        try
        {
            nulls = Integer.parseInt(sNull);
            fnameSeed = Long.parseLong(sFnameSeed);
            lnameSeed = Long.parseLong(sLnameSeed);
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical values", e);
            fnameSeed = System.currentTimeMillis();
            lnameSeed = System.currentTimeMillis();
        }
        
        
        Utils utils = new Utils();
        
        logger.debug("loading file1");
        try
        {
            vTopDomains = utils.readFile("conf/resources" + File.separator +"TLDs.txt");
            vFirstNames = utils.readFile("conf/resources" + File.separator + "en_firstnames.txt");
            vLastNames = utils.readFile("conf/resources" + File.separator +"en_lastnames.txt");
            vProviders = utils.readFile("conf/resources" + File.separator + "emailproviders.txt");
        }
        catch(FileNotFoundException fnfe)
        {
            logger.error("A data file could not be found", fnfe);
        }
        iTLDs   = vTopDomains.size();
        iFNames = vFirstNames.size();
        iLNames = vLastNames.size();
        iProvid = vProviders.size();
        
        nullGen     = new Random();
        gen1        = new Random(fnameSeed);
        gen2        = new Random(lnameSeed);
        gen3        = new Random();
        
        logger.debug("loading files.Done");
    }
    
    
    public Object generate()
    {
        //first "consume" these generators nextInt,
        // so as to keep compatibility with email randomiser
        
        int i1 = gen3.nextInt(iTLDs);
        int i2 = gen1.nextInt(iFNames);
        int i3 = gen2.nextInt(iLNames);
        int i4 = gen3.nextInt(iProvid);
        
        //check the nulls
        int prob;
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        int len;
        
        String s, tld, fname, lname, provider;
        
        tld = vTopDomains.elementAt(i1);
        
        //get some random characters from the firstname, avoid, the last two chars ,f or ,m
        fname = vFirstNames.elementAt(i2);
        len   = fname.length();
        fname = fname.substring(0, len-2);
        len   = fname.length();
        fname = fname.substring( 0, 1+gen3.nextInt(len) );
        
        //get some characters from the last name
        lname = vLastNames.elementAt(i3);
        len   = lname.length();
        lname = lname.substring( 0,1+gen3.nextInt(len) );
        
        provider = vProviders.elementAt(i4);
        if(gen3.nextBoolean())
        {
            fname = fname.toLowerCase();
            lname = lname.toLowerCase();
        }
        
        s = fname + connector[gen3.nextInt(3)] + lname + "@" + provider + tld;
        return s;
    }
    
    public void destroy()
    {
    }
    
}
