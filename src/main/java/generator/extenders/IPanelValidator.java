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
 *
 * @author Michael
 */
public interface IPanelValidator
{
    public boolean isFormValid();
    public RandomiserInstance getRandomiserInstance();
    public void initialise(RandomiserInstance ri);
}
