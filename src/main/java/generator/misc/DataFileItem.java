/*
 * dgMaster: A versatile, open source data generator.
 *(c) 2007 M. Michalakopoulos, mmichalak@gmail.com
 */

package generator.misc;

/**
 * 
 * Represents a data definition for a single "field", which will be used on the
 * output of a text file.
 */
public class DataFileItem {
  private String fieldName;
  private String randomiserInstanceName;
  private int alignment;
  private String encloseChar;
  private int width;
  private String expression;
  private String dummy;
  
  /** Creates a new instance of DataFileItem */
  public DataFileItem() {
  }

  public String getRandomiserInstanceName() {
    return randomiserInstanceName;
  }

  public void setRandomiserInstanceName(String randomiserInstanceName) {
    this.randomiserInstanceName = randomiserInstanceName;
  }

  public int getAlignment() {
    return alignment;
  }

  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }

  public String getEncloseChar() {
    return encloseChar;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setEncloseChar(String encloseChar) {
    this.encloseChar = encloseChar;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public String getDummy() {
    return dummy;
  }

  public void setDummy(String dummy) {
    this.dummy = dummy;
  }

}