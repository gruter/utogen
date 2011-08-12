/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

/*
 * Main.java
 *
 * Created on 26 January 2007, 00:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.gui;


public class Main {

  /** Creates a new instance of Main */
  public Main() {
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    //DOMConfigurator.configure("generator.xml");

    // TODO code application logic here
    MainForm frmMain = new MainForm();

    frmMain.setVisible(true);
  }

}
