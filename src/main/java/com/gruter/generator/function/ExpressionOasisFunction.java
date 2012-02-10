package com.gruter.generator.function;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.vedantatree.expressionoasis.ExpressionContext;
import org.vedantatree.expressionoasis.ExpressionEngine;
import org.vedantatree.expressionoasis.extensions.DefaultVariableProvider;

public class ExpressionOasisFunction {
  public static Map<String, SimpleDateFormat> dateFormatMap = new HashMap<String, SimpleDateFormat>();
  public static Map<String, Locale> locales = new HashMap<String, Locale>();
  static {
    locales.put("US", Locale.US);
    locales.put("KR", Locale.KOREA);
  }
  
  public static void main(String[] args) throws Exception {
    long time = System.currentTimeMillis();
    
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    String test = "dateFormatLocale(" + time + " + 10000000, 'MMM yyyy-MM-dd HH:mm:ss', 'US')";
    ExpressionContext expressionContext = new ExpressionContext();

    DefaultVariableProvider dvp = new DefaultVariableProvider();
    
    System.out.println(">>>" + ExpressionEngine.evaluate( test, expressionContext ));
  }
}
