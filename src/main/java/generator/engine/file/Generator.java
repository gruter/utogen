/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

package generator.engine.file;
import generator.engine.ProgressUpdateObservable;
import generator.engine.ProgressUpdateObserver;
import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;
import generator.misc.Constants;
import generator.misc.DataFileDefinition;
import generator.misc.DataFileItem;
import generator.misc.RandomiserType;
import generator.misc.Utils;

import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.gruter.generator.randomisers.LogWriter;


public class Generator implements ProgressUpdateObservable
{
    Vector<RandomiserType> vRandomiserTypes;
    Vector<RandomiserInstance> vRandomiserInstances;
    Vector<DataFileDefinition> vDataFileDefinitions;
    DataFileDefinition dataFileDefinition;
    
    Logger logger = Logger.getLogger(Generator.class);
    
    ProgressUpdateObserver observer;
    
    
    /** Creates a new instance of generateData */
    public Generator()
    {
    }
    
    
    /**
     * Loads the randomiser types, randomiser instances and file definitions.
     * This is used when using the application from the command line.
     */
    public void loadfromFiles()
    {
        logger.debug("Loading data from files (RandomiserTypes, randomiser instances, file definitions");
        Utils utils = new Utils();
        
        //load the randomiser-type definitions from file
        vRandomiserTypes = utils.loadRandomiserTypes();
        
        //load the randomiser-instance definitions from the file
        vRandomiserInstances = utils.loadRandomiserInstances();
        
        //load file definitions
        vDataFileDefinitions = utils.loadDataFileDefinitions();
        logger.debug("Loading data from files (RandomiserTypes, randomiser instances, file definitions. Done");
    }
    
    
    /**
     * Loads the randomiser types, randomiser instances and file definitions.
     * This is used when using the application from withing the GUI.
     */
    public void setEngineData(Vector<RandomiserType> vRT, 
        Vector<RandomiserInstance> vRI, 
        Vector<DataFileDefinition> vDFDs)
    {
        this.vRandomiserTypes = vRT;
        this.vRandomiserInstances = vRI;
        this.vDataFileDefinitions = vDFDs;
    }
    
    
    /**
     * Loads the data file definition object; this contains all the
     * generate text file's details.
     * It takes a DataFileDefinition parameter, so the application
     * should have already loaded that prior to calling this method.
     */
    public void setFileDefinitionOutput(DataFileDefinition dfd)
    {
        this.dataFileDefinition = dfd;
    }
    
    
    
    /**
     * Loads the data file definition object; this contains all the
     * generate text file's details.
     * Preconditions: vDataFileDefinitions must NOT be null :)
     */
    public boolean setFileDefinitionOutput(String fileDefinition)
    {
        boolean found=false;
        
        logger.debug("Searching definition: " +fileDefinition);
        DataFileDefinition dfd;
        
        for(int i=0; i<vDataFileDefinitions.size(); i++)
        {
            dfd = vDataFileDefinitions.elementAt(i);
            if(dfd.getName().equalsIgnoreCase(fileDefinition))
            {
                dataFileDefinition = dfd;
                found = true;
                break;
            }
        }
        if(dataFileDefinition!=null)
            logger.debug("Searching definition: Done (loaded)");
        else
            logger.warn("Searching definition, failed.");
        
        return found;
    }
    
    public DataFileDefinition getDataFileDefinition() {
      return dataFileDefinition;
    }
    
    
    /**
     * Returns a RandomiserInstance object, given a string.
     * Preconditions: vRandomiserInstances must NOT be null :)
     */
    private RandomiserInstance getRandomiserInstance(String riName)
    {
        RandomiserInstance ri = null;
        boolean found = false;
        int i=0;
        
        logger.info("Retrieving randomiserInstance object for:"+ riName);
        
        while( i < vRandomiserInstances.size() && !found)
        {
            ri   = vRandomiserInstances.elementAt(i);
            if(ri.getName().equalsIgnoreCase(riName))
                found = true;
            i++;
        }
        logger.debug("Retrieving the randomiserInstance for:"+riName + ". Found:"+found);
        return ri;
    }
    
    
    /**
     * Returns the name of a RandomiserType class,
     * given its name in the application.
     * Preconditions: vRandomiserTypes must NOT be null :)
     */
    private RandomiserType getRandomiserType(String randomiserType)
    {
        RandomiserType rt = null;
        String type, className=null;
        boolean found = false;
        int i=0;
        
        logger.debug("Retrieving randomiserInstance object for:"+randomiserType);
        while( i<vRandomiserTypes.size() && !found)
        {
            rt = vRandomiserTypes.elementAt(i);
            if(rt.getName().equalsIgnoreCase(randomiserType))
            {
                found = true;
            }
            i++;
        }
        logger.debug("Retrieving the randomiserType for:"+randomiserType + ". Found:"+found +", class:"+rt.getName());
        return rt;
    }
    
    
    // padLeft, padRight, padCenter are used when aligning data fields.
    private String padLeft(String s, int width)
    {
        int paramWidth = s.length();
        int pad = width - s.length();
        String temp="", retValue;
        
        for(int i=0; i<pad; i++)
            temp=temp +" ";
        retValue = temp +s;
        
        return retValue;
    }
    
    private String padRight(String s, int width)
    {
        int paramWidth = s.length();
        int pad = width - s.length();
        String temp="", retValue;
        
        for(int i=0; i<pad; i++)
            temp=temp +" ";
        retValue = s + temp;
        
        return retValue;
    }
    
    // [*] may not exactly pad the correct amount of spaces
    private String padCenter(String s, int width)
    {
        int paramWidth = s.length();
        int pad = (width - s.length())/2; //[*] tricky integer division :P
        String temp="", retValue;
        
        for(int i=0; i<pad; i++)
            temp=temp +" ";
        retValue = temp + s + temp;
        if(retValue.length()<width)
            retValue=" "+retValue;
        else if(retValue.length()>width)
            retValue=retValue.substring(0, retValue.length()-1);
        return retValue;
    }
    
    public void generate()
    {
        long start = System.currentTimeMillis();
        Vector<DataFileItem> vDataItems;
        int i;
        long numOfRecs;
        boolean error, cancelled;
        String temp, outLine, riName, enclChar;
        IRandomiserFunctionality aGenerator[];
        Utils utils;
        RandomiserInstance ri;
        RandomiserType rt;
        Object objValue;
        StringBuffer sb;
        
        //load the generator objects together with the appropriate randomiser instances as set by the user
        vDataItems   = dataFileDefinition.getOutDataItems(); //user requested data
        aGenerator   = new IRandomiserFunctionality[vDataItems.size()]; //the generators
        utils        = new Utils();
        
        //now we can notify the observer about the number of steps
        notifyInit();
        notifyMaxProgressValue(vDataItems.size());
        
        LogWriter writer = null;
        try
        {
            writer = new LogWriter(dataFileDefinition);
            
            for(int j=0; j<vDataItems.size(); j++)
            {
                DataFileItem dataItem = vDataItems.elementAt(j);
                riName = dataItem.getRandomiserInstanceName();
                logger.debug("Loading class for :"+ riName);
                
                //inform user
                cancelled = notifyProgrssUpdate("Loading initialiser for:"+ riName, j+1);
                
                //create the randomiser instance out of the name
                ri = getRandomiserInstance(riName);
                
                //get the randomiser type out of the RI
                rt = getRandomiserType( ri.getRandomiserType() );
                
                //load and store the generator, set its RI, now it is ready to use
                aGenerator[j] = (IRandomiserFunctionality) utils.createObject(rt.getGenerator());
                aGenerator[j].setRandomiserInstance(ri);
                logger.debug("Loading class for :"+ riName  +". Done");
            }
        }
        catch(Exception e)
        {
            logger.error("Error while initialising a generator",e);
            observer.datageGenError("Error while initialising a generator:"+e);
            return;
        }
        
        //now generate the data, norify the observer about the number of records
        numOfRecs = dataFileDefinition.getTotalRecords();   
        int numOfRecsPerSec = dataFileDefinition.getTps();
        notifyMaxProgressValue((int)numOfRecs);
        
        i=0; error=false; cancelled = false;
        
        long startTime = System.currentTimeMillis();
        int lapCount = 0;
        
        while(!error && !cancelled)
        {
            if( numOfRecs > 0 && i >= numOfRecs) {
              break;
            }
            if(numOfRecsPerSec > 0 && lapCount >= numOfRecsPerSec) {
              long sleepTime = 1000 - (System.currentTimeMillis() - startTime);
              if(sleepTime > 0) {
                try {
                  Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
              }
              lapCount = 0;
              startTime = System.currentTimeMillis();
            }
            
            try
            {
                //generates a line
                outLine="";
                sb = new StringBuffer();
                for(int j=0; j<vDataItems.size(); j++)
                {
                    //retrieve format parameters for this generator
                    DataFileItem dataItem = vDataItems.elementAt(j);
                    objValue = aGenerator[j].generate();
                    if(objValue==null)
                        objValue="";
                    temp = objValue.toString();
                    
                    enclChar = dataItem.getEncloseChar();
                    if(temp.length()<dataItem.getWidth())
                    {
                        if(dataItem.getAlignment()==Constants.ALIGN_LEFT)
                            temp = padRight(temp,dataItem.getWidth());
                        else if(dataItem.getAlignment()==Constants.ALIGN_RIGHT)
                            temp = padLeft(temp,dataItem.getWidth());
                        else
                            temp = padCenter(temp,dataItem.getWidth());
                    }
                    sb.append(enclChar);
                    sb.append(temp);
                    sb.append(enclChar);
                    sb.append(dataFileDefinition.getDelimiter());
                }
                
                //inform the observer
                cancelled = notifyProgrssUpdate("Generating ("+ (i+1) + "/" +numOfRecs+")", i+1 );
                
                //remove the last delimiter[*] what if there is none?
                sb.deleteCharAt( sb.lastIndexOf(dataFileDefinition.getDelimiter()));
                outLine = sb.toString();
                writer.write(outLine);
                logger.debug(outLine);
                i++;
                lapCount++;
            }
            catch(IOException ioe)
            {
                logger.warn("Exception error while writing data, will exit after loops:"+i, ioe);
                error=true;
            }
        }//end while
        
        writer.close();
        
        long stop = System.currentTimeMillis();
        logger.debug("Time in millis:" + (stop-start) );
        if(error)
        {
            observer.datageGenError("There were errors during the data generation progress, possibly a file write error.");
            return;
        }
        //notify observer, we are done
        notifyEnd();
    }
    
    public void registerObserver(ProgressUpdateObserver observer)
    {
        this.observer = observer;
    }
    
    public void unregisterObserver()
    {
        this.observer = null;
    }
    
    public void notifyInit()
    {
        if(observer==null)
            return;
        observer.dataGenStarted();
    }
    
    public void notifyMaxProgressValue(int max)
    {
        if(observer==null)
            return;        
        observer.dataGenMaxProgressValue(max);
    }
    
    
    public boolean notifyProgrssUpdate(String msg, int progress)
    {
        if(observer==null)
            return false;
        
        return observer.dataGenProgressContinue(msg, progress);
    }
    
    public void notifyEnd()
    {
        if(observer==null)
            return;
        
        observer.dataGenEnd();
        observer=null;
    }
    
    public void datageGenError(String msg)
    {
        if(observer==null)
            return;
        
        observer.datageGenError(msg);
        observer.dataGenEnd();
    }
    
}
