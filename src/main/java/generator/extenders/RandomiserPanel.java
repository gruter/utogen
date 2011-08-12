/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */


/*
 * GeneratorPanel.java
 *
 * Created on 03 February 2007, 13:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.extenders;
import javax.swing.JPanel;

/**
 *
 * Extemns JPanel and implements IGeneratorPanel,
 * (well, not actually, the class is abstract).
 * You will need to extend this and implement IGenerator's methods,
 * so as to create a Panel that can be loaded from dgMaster.
 */
public abstract class RandomiserPanel extends JPanel implements IGeneratorPanel
{
    
    
}
