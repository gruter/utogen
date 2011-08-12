/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


/*
 * PanelValidator.java
 *
 * Created on 06 November 2006, 13:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.extenders;

import java.util.HashMap;

/**
 * Provides the functionality for the generator panels.
 * Each new generator panel must either extend the GeneratorPanel abstract class,
 * or implement this interface.
 *
 */
public interface IGeneratorPanel
{    
    //called whenever the user clicks on an existing data-definition and the panel needs to be populated with this existing information.
    public void initialise(RandomiserInstance ri);

    //called just before saving the data of the form. If this method returns false, then no data are saved. 
    //If it returns yes, then the method below is called.
    public boolean isFormValid();

    //called to save the data on the form. The panel class needs to create an appropriate RandomiserInstance object,
    //(just a bean object really) if the data on the form are valid.    
    public RandomiserInstance getRandomiserInstance();
}
