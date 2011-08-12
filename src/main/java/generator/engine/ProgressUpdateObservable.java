/*
 * ProgressUpdateObservable.java
 *
 * Created on 19 May 2007, 17:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package generator.engine;

/**
 *
 * @author Michael
 */
public interface ProgressUpdateObservable
{
    public void registerObserver( ProgressUpdateObserver observer );
    public void unregisterObserver();
    
    public void notifyInit();
    public void notifyMaxProgressValue(int max);
    public boolean notifyProgrssUpdate(String msg, int progress);
    public void notifyEnd();
    
    public void datageGenError(String msg);
}
