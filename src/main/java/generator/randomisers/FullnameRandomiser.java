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

/*
 * Each generator needs to implement the IRandomiserFunctionality interface.
 */
public class FullnameRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(FullnameRandomiser.class);
    int nulls; //percentage of nulls
    
    //these will be retrieved from the RandomiserInstance
    boolean lTitle, lFirstName, lFirstNameFull, lFirstInitial, lMiddle, lLastname;
    Random nullGen, gen1, gen2, gen3, gen4;
    
    //vectors to load the dictionaries and vector sizes...
    Vector<String> vFirstNames;
    Vector<String> vLastNames;
    Vector<String> vTitles;
    int iFNames, iLNames;
    
    
    //called immediately after the constructor. I have kept the constructors empty
    //as I want dgMaster to create the objects quickly. loadDictionaries could
    // have been part of the constructor though...
    public void setRandomiserInstance(RandomiserInstance ri)
    {
        LinkedHashMap hashMap;
        String sNull, sTitle, sFirstName, sFirstNameFull, sFirstInitial, sMiddle, sLastname;
        
        //add the titles...
        //Mr and Dr are special cases as explained later,
        //they do not have to be added here...
        vTitles = new Vector();
        vTitles.add("Mrs");
        vTitles.add("Ms");
        vTitles.add("Miss");
        
        //retrieve the hashmap from RandomiserInstance
        hashMap = ri.getProperties();
        
        //extract the values from the hashmap
        sNull  = (String) hashMap.get("nullField");
        sTitle  = (String) hashMap.get("includeTitle");
        sFirstName    = (String)hashMap.get("includeFirstName");
        sFirstNameFull= (String)hashMap.get("firstNameFull");
        sFirstInitial = (String)hashMap.get("firstNameInitial");
        sMiddle       = (String)hashMap.get("includeInitialMiddle");
        sLastname     = (String)hashMap.get("includeLastName");
        try
        {
            nulls = Integer.parseInt(sNull);
            lTitle        = Boolean.valueOf(sTitle);
            lFirstName    = Boolean.valueOf(sFirstName);
            lFirstNameFull= Boolean.valueOf(sFirstNameFull);
            lFirstInitial = Boolean.valueOf(sFirstInitial);
            lMiddle       = Boolean.valueOf(sMiddle);
            lLastname     = Boolean.valueOf(sLastname);
        }
        catch(Exception e)
        {
            logger.error("Error setting the hashmap values", e);
        }
        
        //now load the dictionaries (english firstnames and lastnames)
        loadDictionaries();
        
        //nulls generator
        nullGen = new Random();
        
        //firstname, lastname, titles generators randomisers,
        //could have as well used the same one...
        gen1 = new Random();
        gen2 = new Random();
        gen3 = new Random();
        gen4 = new Random();
    }
    
    
    //loads data from two standard dictionaries provided with dgMaster:
    // english first names and english last names.
    private void loadDictionaries()
    {
        Utils utils = new Utils();
        logger.debug("Loading files");
        try
        {
            vFirstNames = utils.readFile("conf\\resources\\en_firstnames.txt");
            vLastNames  = utils.readFile("conf\\resources\\en_lastnames.txt");
        }
        catch(FileNotFoundException fnfe)
        {
            logger.error("Error loading files...", fnfe);
        }
        iFNames = vFirstNames.size();
        iLNames = vLastNames.size();
        logger.debug("Loading files.Done");
    }
    
    
    
    public Object generate()
    {
        
        int i1 = gen1.nextInt(iFNames);
        int i2 = gen2.nextInt(iLNames);
        
        //we have three female titles
        int i3 = gen3.nextInt(3);
        
        //a smalll probability that someone has the Dr. title
        double drProb = gen3.nextDouble();
        double midlInitProb = gen4.nextDouble();
        
        //check the nulls
        int nullProb = nullGen.nextInt(100);
        if(nullProb<nulls)
            return null;
        
        String aSplitNames[], sFname;
        String sFirstName, sFirstInitial, sLastName, sMiddleInitial, sex, title;
        String returnValue="", space="";
        //form all the data and then include only the ones we need
        
        //get firstname and sex: Firstname,sex (e.g. Michael,m)
        sFname = vFirstNames.elementAt(i1);
        aSplitNames = sFname.split(",");
        sFirstName  = aSplitNames[0];
        sex         = aSplitNames[1];
        sFirstInitial = sFirstName.substring(0,1);
        
        sLastName      = vLastNames.elementAt(i2);
        sMiddleInitial = vLastNames.elementAt(i2).substring(0,1) + "." ;
        if(sex.equalsIgnoreCase("m"))
            title = "Mr";
        else
            title = vTitles.elementAt(i3);
        
        //there will only be a small probability that someone's title is Dr
        if(drProb<=0.3)
            title = "Dr";
        
        //there will be quite some probabilty that not all people will have a middle initial
        if(midlInitProb<=0.85)
            sMiddleInitial="";
        
        
        //now we have all the data we want, we can form the full name as requested by the user
        returnValue=""; space="";
        if(lTitle)
            returnValue = title + " ";
        
        if(lFirstName)
            if(lFirstNameFull)
                returnValue+= sFirstName + " ";
            else
                returnValue+= sFirstInitial + ". ";
        
        if(lMiddle && sMiddleInitial.length()>0)
            returnValue += sMiddleInitial + " ";
        
        if(lLastname)
            returnValue += sLastName;

        return returnValue.trim();
    }
    
    
    public void destroy()
    {
        //not anything to do here with this generator
    }
    
}
