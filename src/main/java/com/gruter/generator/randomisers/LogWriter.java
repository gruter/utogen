package com.gruter.generator.randomisers;

import generator.misc.DataFileDefinition;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogWriter {
//  static final Logger logger = Logger.getLogger(LogWriter.class);

  private BufferedWriter fileWriter;
  private Logger log4jWriter;
  
  public LogWriter(DataFileDefinition df) throws Exception {
    String log4jName = df.getLog4jName();
    
    log4jWriter = Logger.getLogger(log4jName);
  }
  
  public void write(String data) throws IOException {
    if(log4jWriter != null) {
      log4jWriter.info(data);
    } else {
      fileWriter.write(data);
      fileWriter.newLine();
    }
  }

  public void close() {
    if(fileWriter != null) {
      try {
        fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }    
  }
  
  public static void main(String[] args) throws Exception {
    Thread t1 = new Thread() {
      public void run() {
        PropertyConfigurator.configure(getLogProperties("DFFA01", "/Users/babokim/tmp/log_1.log", "test_1", "yyyy-MM-dd"));
        
        Logger logger = Logger.getLogger("test_1");
        int count = 0;
        while(true) {
          logger.debug("test_1:" + count);
          count++;
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }
        }
      }
    };

    Thread t2 = new Thread() {
      public void run() {
        System.out.println(">>>>>>>" + LogManager.exists("test_2"));
        PropertyConfigurator.configure(getLogProperties("DFFA02", "/Users/babokim/tmp/log_2.log", "test_2", "yyyy/MM/dd"));
        
        Logger logger = Logger.getLogger("test_2");
        int count = 0;
        while(true) {
          logger.debug("test_2:" + count);
          count++;
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }
        }
      }
    };

     t1.start();
     t2.start();
  }
  
  public static Properties getLogProperties(String appenderName, String filePath, String loggerName, String datePattern) {
    Properties props = new Properties();
    props.setProperty("log4j.reset", "true");
    props.put("log4j.appender." + appenderName, "org.apache.log4j.DailyRollingFileAppender");
    
    props.put("log4j.appender." + appenderName + ".File", filePath);
    props.put("log4j.appender." + appenderName + ".DatePattern", ".yyyy-MM-dd");

    props.put("log4j.appender." + appenderName + ".layout", "org.apache.log4j.PatternLayout");
    props.put("log4j.appender." + appenderName + ".layout.ConversionPattern", "%d{" + datePattern + "} %p %t %c{2}.%M() : %m%n");
    
    props.setProperty("log4j.logger." + loggerName ,"DEBUG, " + appenderName);
    return props;
  }
}
