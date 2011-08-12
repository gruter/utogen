/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */



/*
 * IRandomiserFunctionality.java
 */

package generator.extenders;

/**
 * Provides the methods that need to be implemented by a data generator class.
 * 
 */
public interface IRandomiserFunctionality
{
    /*
     * Passes the panel information on to the actual data generator class.
     * A RandomiserInstance object contains the choices the user made via the panel
     * The method is called immediately after the implementing class's constructor
     */
    public void setRandomiserInstance(RandomiserInstance ri);
    
    /*
     * Generates the actual data. dgMaster knows how to type-cast the object by 
     * looking into the xml data in SystemDefinitions.xml. If the returned object
     * is to be used in a text file, dgMaster uses the toString() method of the object
     * to retrieve the textual representation of the object.
     */
    public Object generate();
    
    
    /*
     * Performs any tidying up at the end of a data-generation session.
     */
    public void destroy();
}
