/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */



/*
 * RandomiserInstance.java
 *
 * Created on 06 November 2006, 15:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.extenders;

import java.util.LinkedHashMap;

/**
 * Provides a convenient way to save the data from a data-generator form.
 * Each user-defined GeneratorPanel or JPanel that implements IGeneratorPanel
 * needs to return an object of this class. 
 */
public class RandomiserInstance
{
    //the name of the randomiser type to load
    private String randomiserType; 
    
    //name and description are standard fields for all the user-defined cases.
    private String name;       
    private String description;
    
    //any object can be stored in the properties hashmap, however, it will be
    //its String representation that will be saved in the XML file.
    private LinkedHashMap properties;
    
    
    public RandomiserInstance()
    {        
    }
    

    public RandomiserInstance( String randomiserType,
                               String name,
                               String description,
                               LinkedHashMap properties)
    {
        this.randomiserType = randomiserType;
        this.name = name;
        this.description = description;
        this.properties = properties;
    }
    
    
    public String getRandomiserType()
    {
        return randomiserType;
    }

    public void setRandomiserType(String randomiserType)
    {
        this.randomiserType = randomiserType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public LinkedHashMap getProperties()
    {
        return properties;
    }

    public void setProperties(LinkedHashMap properties)
    {
        this.properties = properties;
    }

    public String toString()
    {
        return name;        
    }
}
