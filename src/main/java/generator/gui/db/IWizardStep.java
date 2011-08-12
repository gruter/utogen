/*
 * WizardPanel.java
 *
 * Created on 11 November 2007, 10:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.gui.db;

import generator.db.DBGeneratorDefinition;
import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public interface IWizardStep
{
    public boolean isWizardStepValid();
    
    public void setDBGeneratorDefinition(DBGeneratorDefinition dbGeneratorDefinition);
    public DBGeneratorDefinition getDBGeneratorDefinition();
    
}
