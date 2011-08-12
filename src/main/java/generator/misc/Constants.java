/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */



package generator.misc;

public class Constants
{
    //used in string building/parsing for the randomiser instance definition
    // eg. some_randomiser_instance(10,',LEFT)    
    public static final String LEFT_MARK = "(";
    public static final String RIGHT_MARK = ")";
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_CENTER = 2;
    public static final int ALIGN_RIGHT = 3;
    public static final int DEFAULT_WIDTH = -1;     
    
    public static final int DRIVER_OK = 1;
    public static final int DRIVER_NOT_OK = 2;
    
    public static final int GENERATOR_SIMPLE = 1;
    public static final int GENERATOR_BEAN = 2;
    public static final int GENERATOR_DBMONSTER = 3;
    
    public static final String DATE_DELIM  = "$VALUE";
    public static final String REGEX_DATE_DELIM  = "\\p{Punct}VALUE";
}
