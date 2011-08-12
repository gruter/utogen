/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */



package generator.misc;
/**
 *
 * Represents the definition of the information required to plug-in a new 
 * random data generator. Each random data generator needs to define:
 * a name for the generator, a name for the panel that is used to fine tune
 * parameters for the random data generator, the actual name of the generator
 * (fully qualified class name) and a numerical value indicating the type of
 * the returned object
 */
public class RandomiserType
{
    private String name;
    private String panel;
    private String generator;
    private int jtype;
    final public static int TYPE_STRING = 0;
    final public static int TYPE_BOOLEAN = 1;
    final public static int TYPE_BYTE = 20;
    final public static int TYPE_SHORT = 2;
    final public static int TYPE_INTEGER = 3;
    final public static int TYPE_LONG = 4;
    final public static int TYPE_FLOAT = 5;
    final public static int TYPE_DOUBLE = 6;
    final public static int TYPE_BIGDECIMAL = 7;
    final public static int TYPE_DATE = 8;
    final public static int TYPE_TIME = 9;
    final public static int TYPE_TIMESTAMP = 10;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getPanel()
    {
        return panel;
    }
    
    public void setPanel(String panel)
    {
        this.panel = panel;
    }
    
    public String getGenerator()
    {
        return generator;
    }
    
    public void setGenerator(String generator)
    {
        this.generator = generator;
    }
    
    public int getJtype()
    {
        return this.jtype;
    }
    
    public void setJtype(int jtype)
    {
        this.jtype = jtype;
    }
    
    public String toString()
    {
        return name;
    }
}