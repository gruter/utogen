package com.gruter.generator.service;

import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;
import generator.misc.Constants;
import generator.misc.DataFileDefinition;
import generator.misc.DataFileItem;
import generator.misc.RandomiserType;
import generator.misc.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Generator {
  private Map<String, RandomiserType> randomiserTypes = new HashMap<String, RandomiserType>();
  private Map<String, RandomiserInstance> randomiserInstances = new HashMap<String, RandomiserInstance>();
  private DataFileDefinition dfd;
  private IRandomiserFunctionality randomGnerator[];
  private String errorMessage;

  private long numOfRecords;

  public Generator(DataFileDefinition dfd) {
    Utils utils = new Utils();
    Vector<RandomiserType> randomiserTypes = utils.loadRandomiserTypes();
    if(randomiserTypes == null || randomiserTypes.size() == 0) {
      errorMessage = "No randomiserTypes";
      return;
    }
    for (RandomiserType eachType : randomiserTypes) {
      this.randomiserTypes.put(eachType.getName(), eachType);
    }
    Vector<RandomiserInstance> randomiserInstances = utils.loadRandomiserInstances();
    if(randomiserInstances == null || randomiserInstances.size() == 0) {
      errorMessage = "No randomiserInstances";
      return;
    }
    for (RandomiserInstance eachInstance : randomiserInstances) {
      this.randomiserInstances.put(eachInstance.getName(), eachInstance);
    }
    this.dfd = dfd;
    randomGnerator = new IRandomiserFunctionality[dfd.getOutDataItems().size()];
    init();
  }

  private void init() {
    Utils utils = new Utils();
    int index = 0;
    try {
      for (DataFileItem eachItem : dfd.getOutDataItems()) {
        RandomiserInstance ri = randomiserInstances.get(eachItem.getRandomiserInstanceName());
        if (ri == null) {
          errorMessage = "Error: No Randomiser Instances: " + eachItem.getRandomiserInstanceName();
          System.out.println(new Date() + " ERROR:" + errorMessage);
          return;
        }
  
        RandomiserType rt = randomiserTypes.get(ri.getRandomiserType());
        if (rt == null) {
          errorMessage = "Error: No Randomiser Type: " + ri.getRandomiserType();
          System.out.println(new Date() + " ERROR:" + errorMessage);
          return;
        }
  
        // load and store the generator, set its RI, now it is ready to use
        randomGnerator[index] = (IRandomiserFunctionality) utils.createObject(rt.getGenerator());
        randomGnerator[index].setRandomiserInstance(ri);
        index++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      errorMessage = "Error: " + e.getMessage();
      System.out.println(new Date() + " ERROR:" + e.getMessage());
    }
  }

  public String next() {
    if(errorMessage != null) {
      return null;
    }
    if (dfd.getTotalRecords() > 0 && numOfRecords >= dfd.getTotalRecords()) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    int index = 0;
    
    String delimiter = "";
    String dfdDelimiter = dfd.getDelimiter();
    if("\\t".equals(dfdDelimiter)) {
      dfdDelimiter = "\t";
    }
    
    for (DataFileItem eachItem : dfd.getOutDataItems()) {
      sb.append(delimiter);
      // retrieve format parameters for this generator
      Object objValue = randomGnerator[index].generate();
      if (objValue == null) {
        objValue = "";
      }
      String temp = objValue.toString();

      String enclChar = eachItem.getEncloseChar();
      if (temp.length() < eachItem.getWidth()) {
        if (eachItem.getAlignment() == Constants.ALIGN_LEFT)
          temp = padRight(temp, eachItem.getWidth());
        else if (eachItem.getAlignment() == Constants.ALIGN_RIGHT)
          temp = padLeft(temp, eachItem.getWidth());
        else
          temp = padCenter(temp, eachItem.getWidth());
      }
      sb.append(enclChar);
      sb.append(temp);
      sb.append(enclChar);
      delimiter = dfdDelimiter;
      index++;
    }
    
    return sb.toString();
  }

  private String padLeft(String s, int width) {
    int pad = width - s.length();
    String temp = "", retValue;

    for (int i = 0; i < pad; i++)
      temp = temp + " ";
    retValue = temp + s;

    return retValue;
  }

  private String padRight(String s, int width) {
    int pad = width - s.length();
    String temp = "", retValue;

    for (int i = 0; i < pad; i++)
      temp = temp + " ";
    retValue = s + temp;

    return retValue;
  }

  // [*] may not exactly pad the correct amount of spaces
  private String padCenter(String s, int width) {
    int pad = (width - s.length()) / 2; // [*] tricky integer division :P
    String temp = "", retValue;

    for (int i = 0; i < pad; i++)
      temp = temp + " ";
    retValue = temp + s + temp;
    if (retValue.length() < width)
      retValue = " " + retValue;
    else if (retValue.length() > width)
      retValue = retValue.substring(0, retValue.length() - 1);
    return retValue;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
