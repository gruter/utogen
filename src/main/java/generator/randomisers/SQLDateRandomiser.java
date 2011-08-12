/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

package generator.randomisers;
import generator.misc.Utils;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Random;
import org.apache.log4j.Logger;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;
import java.sql.Date;


public class SQLDateRandomiser implements IRandomiserFunctionality
{
    Logger logger = Logger.getLogger(SQLDateRandomiser.class);
    Random probability, nullGen, gen;
    long fromField[], toField[];
    int limits[];
    int nulls;
    
    //these DAYs_OF_WEEK are excluded
    boolean selectedDays[] = new boolean[7];
    
    //dictates whether exclude days will be taken into account
    boolean skipControl = false;
    
    //the days between from and to dates
    int diffDays[];
    
    // gcFrom is class visible so it does not have to be instantiated all the time in generate method
    GregorianCalendar gcFrom;
    
    
    public void setRandomiserInstance(RandomiserInstance ri)
    {
        String sRangesNum, sFromField, sToField, sPercentField, sNulls, sSelectedDays;
        
        //i need these gregorian cals. so i can pre-compute the days between them
        GregorianCalendar gcFromField, gcToField;
        
        int rangesNum, percentField[];
        LinkedHashMap hashMap;
        Utils utils = new Utils();
        
        hashMap = ri.getProperties();
        sRangesNum  = (String) hashMap.get("rangesNum");
        sNulls = (String) hashMap.get("nullField");
        sSelectedDays  = (String) hashMap.get("selectedDays");
        try
        {
            rangesNum = Integer.parseInt(sRangesNum);
            nulls = Integer.parseInt(sNulls);
            fromField = new long[rangesNum];
            toField = new long[rangesNum];
            percentField = new int[rangesNum];
            diffDays = new int[rangesNum];
            limits = new int[rangesNum+1];//space reserved for nulls
        }
        catch(Exception e)
        {
            logger.warn("Error setting the numerical-date values (1 - init)", e);
            nulls=0; rangesNum=0; fromField = null; toField = null; percentField=null;
        }
        
        limits[0] = 0;
        for(int i=1; i<=rangesNum; i++)
        {
            try
            {
                sFromField = (String) hashMap.get("fromField"+ (i-1) );
                sToField = (String) hashMap.get("toField"+ (i-1) );
                sPercentField = (String) hashMap.get("percentField"+ (i-1) );
                
                fromField[i-1] = Date.valueOf(sFromField).getTime();
                toField[i-1] = Date.valueOf(sToField).getTime();
                percentField[i-1] = Integer.parseInt(sPercentField);
                limits[i] = limits[i-1] + percentField[i-1];
                
                gcFromField = new GregorianCalendar();
                gcFromField.setTimeInMillis(fromField[i-1]);
                
                gcToField = new GregorianCalendar();
                gcToField.setTimeInMillis(toField[i-1]);
                diffDays[i-1] = utils.getDays(gcFromField, gcToField);
            }
            catch(Exception e)
            {
                logger.warn("Error setting the numerical values (2 - Loop, index="+i+")", e);
            }
        }
        try
        {
            for(int i=0; i<7; i++)
            {
                if(sSelectedDays.charAt(i)=='0')
                    selectedDays[i] = false;
                else
                    selectedDays[i] = true;
            }
        }
        catch(Exception e)
        {
            logger.warn("Error retrieving selected days", e);
        }
        probability = new Random();
        nullGen     = new Random();
        gen         = new Random();
        
        //instantiate the gregorian calendar used for calculations by generate
        gcFrom = new GregorianCalendar();
    }
    
    
    public Object generate()
    {
        //check the nulls first
        int prob;
        prob = nullGen.nextInt(100);
        if(prob<nulls)
            return null;
        
        
        long number;      //generated timestamp is stored as long
        int daysDiff;     //the number of the days between from and to dates
        int day, days;    //excluded day, random number of days
        boolean dateHasInvalidDays;  //does the generated timestamp have invalid days of week?
        
        int attempts;     //number of attempts to generate a desired timestamp
        
        
        //find which row from the table to use
        prob = probability.nextInt(100);
        int idxGen = 0;
        for(int i=1; i<limits.length; i++)
        {
            if(prob>=limits[i-1] && prob<limits[i])
            {
                idxGen=i-1;
                break;
            }
        }
        
        //set timestamp vars
        daysDiff = diffDays[idxGen];
        gcFrom.setTimeInMillis(fromField[idxGen]);
        
        
        //an attempt was made in the past to generate a timestamp which excluded certain days of week
        //that attempt might have failed, so it is a waste of time trying to generate timestamps
        //that exclude the user-selected days of week. We will not generate exactly what the user requested,
        //but we not fall into an infinite loop.
        if(skipControl==false)
        {
            attempts=0;
            dateHasInvalidDays = false;
            do
            {
                //first, generate an appropriate random number of days
                days = gen.nextInt( daysDiff + 1);
                
                //form the random timestamp
                gcFrom.add(GregorianCalendar.DATE,days);
                
                //lastly, check that we have a date which excludes the user selected days
                day = gcFrom.get(Calendar.DAY_OF_WEEK);
                dateHasInvalidDays = selectedDays[day-1];
                attempts++;
            }while(dateHasInvalidDays && attempts<10);
            if(attempts==10)
            {
                logger.warn("Could not find a date that excludes the requested days,will ignore it");
                skipControl = true;
            }
        }
        else
        {
            //get a random number of days up to the day difference
            days = gen.nextInt( daysDiff + 1);
            
            //form the random timestamp
            gcFrom.add(GregorianCalendar.DATE,days);
        }
        number = gcFrom.getTime().getTime();
        return new Date(number);
    }
    
    
    public void destroy()
    {
    }
    
    /** Creates a new instance of SQLDateRandomiser */
    public SQLDateRandomiser()
    {
    }
    
}
