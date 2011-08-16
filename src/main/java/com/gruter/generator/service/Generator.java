package com.gruter.generator.service;

import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;
import generator.misc.Constants;
import generator.misc.DataFileDefinition;
import generator.misc.DataFileItem;
import generator.misc.RandomiserType;
import generator.misc.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vedantatree.expressionoasis.ExpressionContext;
import org.vedantatree.expressionoasis.ExpressionEngine;
import org.vedantatree.expressionoasis.exceptions.ExpressionEngineException;

public class Generator {
  private Map<String, RandomiserType> randomiserTypes = new HashMap<String, RandomiserType>();
  private Map<String, RandomiserInstance> randomiserInstances = new HashMap<String, RandomiserInstance>();
  private DataFileDefinition dfd;
  private IRandomiserFunctionality randomGnerator[];
  private String errorMessage;

  private long numOfRecords;

  private ExpressionContext expressionContext;
  
  private Map<Integer, List<String>> variables = new HashMap<Integer, List<String>>();
  
  public Generator(DataFileDefinition dfd) throws Exception {
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
    this.randomGnerator = new IRandomiserFunctionality[dfd.getOutDataItems().size()];
    try {
      this.expressionContext = new ExpressionContext();
    } catch (ExpressionEngineException e) {
      e.printStackTrace();
    }
    init();
  }

  private void init() throws Exception{
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
        if("_expression".equals(eachItem.getRandomiserInstanceName())) {
          Pattern  p = Pattern.compile("(\\$[\\w]+)");
          //String expr = "$f1 + $myTest2 : $f3 = f1234 >= $f2134";
          String expr = eachItem.getExpression();
          if(expr == null) {
            throw new IOException("No expression:" + eachItem.getFieldName());
          }
          Matcher matcher = p.matcher(expr);
          List<String> exprFields = new ArrayList<String>();
          System.out.print("variables:");
          while(matcher.find()) {
            String variable = expr.substring(matcher.start(), matcher.end());
            exprFields.add(variable);
            System.out.print("[" + variable + "]");
          }
          System.out.println();
          variables.put(index, exprFields);
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
    
    Map<String, String> generatedFields = new HashMap<String, String>();
    
    int index = 0;
    for (DataFileItem eachItem : dfd.getOutDataItems()) {
      Object objValue = randomGnerator[index].generate();
      if (objValue == null) {
        objValue = "";
      }
      generatedFields.put(eachItem.getFieldName(), objValue.toString());
      index++;
    }

    StringBuffer sb = new StringBuffer();
    
    String dfdDelimiter = dfd.getDelimiter();
    if("\\t".equals(dfdDelimiter)) {
      dfdDelimiter = "\t";
    }
    String delimiter = "";
    index = -1;
    for (DataFileItem eachItem : dfd.getOutDataItems()) {
      index++;
      if(eachItem.getDummy() != null && eachItem.getDummy().equalsIgnoreCase("true")) {
        continue;
      }
      String outputValue = null;
      if("_expression".equals(eachItem.getRandomiserInstanceName())) {
        String expression = eachItem.getExpression();
        
        List<String> variableList = variables.get(index);
        
        for(String eachVariable: variableList) {
          String variableField = eachVariable.substring(1);   //remove $
          //System.out.println(">>>>>" + eachVariable);
          expression = expression.replace(eachVariable, generatedFields.get(variableField));
        }
        try {
//          System.out.println("expression:" + expression);
          Object exprResult = ExpressionEngine.evaluate(expression, expressionContext);
          outputValue = exprResult.toString();
        } catch (ExpressionEngineException e) {
          e.printStackTrace();
          outputValue = "expression:" + expression + ":" + e.getMessage();
        }
      } else {
        outputValue = generatedFields.get(eachItem.getFieldName());
      }
      if (outputValue.length() < eachItem.getWidth()) {
        if (eachItem.getAlignment() == Constants.ALIGN_LEFT) {
          outputValue = padRight(outputValue, eachItem.getWidth());
        } else if (eachItem.getAlignment() == Constants.ALIGN_RIGHT) {
          outputValue = padLeft(outputValue, eachItem.getWidth());
        } else {
          outputValue = padCenter(outputValue, eachItem.getWidth());
        }
      }
      String enclChar = eachItem.getEncloseChar();
      sb.append(delimiter);
      sb.append(enclChar);
      sb.append(outputValue);
      sb.append(enclChar);
      delimiter = dfdDelimiter;
    }
    
    return sb.toString();
  }

  private String padLeft(String s, int width) {
    int pad = width - s.length();
    String temp = "", retValue;

    for (int i = 0; i < pad; i++) {
      temp = temp + " ";
    }
    retValue = temp + s;

    return retValue;
  }

  private String padRight(String s, int width) {
    int pad = width - s.length();
    String temp = "", retValue;

    for (int i = 0; i < pad; i++) {
      temp = temp + " ";
    }
    retValue = s + temp;

    return retValue;
  }

  // [*] may not exactly pad the correct amount of spaces
  private String padCenter(String s, int width) {
    int pad = (width - s.length()) / 2; // [*] tricky integer division :P
    String temp = "", retValue;

    for (int i = 0; i < pad; i++) {
      temp = temp + " ";
    }
    retValue = temp + s + temp;
    if (retValue.length() < width) {
      retValue = " " + retValue;
    } else if (retValue.length() > width) {
      retValue = retValue.substring(0, retValue.length() - 1);
    }
    return retValue;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
  
  public static void main(String[] args) throws Exception {
//    Pattern  p = Pattern.compile("(\\$[\\w]+)");
//    String expr = "$f1 + $myTest2 : $f3 = f1234 >= $f2134";
//    Matcher matcher = p.matcher(expr);
//    while(matcher.find()) {
//      String v = expr.substring(matcher.start(), matcher.end());
//      System.out.println("[" + v + "]");
//    }
    
//    expr = expr.replace("$f1", "false");
//    expr = expr.replace("$f2", "'12345'");
//    expr = expr.replace("$f3", "'56789'");
    
    ExpressionContext expressionContext = new ExpressionContext();
    String expr = "true ? 10 : 'Mari'";
    Object exprResult = ExpressionEngine.evaluate(expr, expressionContext);
    System.out.println(exprResult.getClass() + "," + exprResult);
    
//    String f1 = "false";
//    String f1Name = "$f1";
//    String expr = "$f1 ? $f2 : $f3";
//    
//    System.out.println(">>>>" + expr.replace(f1Name, f1));
  }
}
