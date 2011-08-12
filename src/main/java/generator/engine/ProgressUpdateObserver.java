
package generator.engine;

public interface ProgressUpdateObserver
{
    public void dataGenStarted();
    public void dataGenMaxProgressValue(int maxProgress);
    public boolean dataGenProgressContinue(String msg, int progress);
    public void dataGenEnd();
    
    public void datageGenError(String msg);
}
