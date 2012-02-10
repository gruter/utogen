package com.gruter.generator.service;

import generator.engine.ProgressUpdateObserver;
import generator.misc.DataFileDefinition;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.gruter.generator.model.GeneratorJob;

public class GeneratorJobThread extends Thread {
  //Logger logger = Logger.getLogger(GeneratorJobThread.class);
  private GeneratorJob job;
  private DataFileDefinition dfd;
  private Logger recordLogger;
  private AtomicBoolean stop = new AtomicBoolean(false);
  private AtomicBoolean pause = new AtomicBoolean(false);
  private long lastTouchTime = System.currentTimeMillis();
  private ProgressUpdateObserver observer;
  
  public GeneratorJobThread(
      GeneratorJob job, 
      DataFileDefinition dfd, 
      Logger recordLogger, 
      ProgressUpdateObserver observer) {
    super("Genetator_" + job.getJobId());
    this.job = job;
    this.dfd = dfd;
    this.recordLogger = recordLogger;
    this.observer = observer;
  }
  
  public String getJobId() {
    return job.getJobId();
  }
  @Override
  public void run() {
    generateLog();
  }
  
  public void generateLog() {
    Generator generator;
    try {
      generator = new Generator(dfd);
    } catch (Exception e1) {
      job.setJobStatus("Error:" + e1.getMessage());
      if(observer != null) {
        observer.datageGenError(e1.getMessage());
      }
      e1.printStackTrace();
      return;
    }

    if(observer != null) {
      observer.dataGenStarted();
    }

    long recordCount = 0;
    int tpsCount = 0;
    long startTime = System.currentTimeMillis();
    while(!stop.get()) {
      if(pause.get()) {
        synchronized(pause) {
          try {
            pause.wait();
          } catch (InterruptedException e) {
            if(stop.get()) {
              break;
            }
          }
        }
      }
      
      if(dfd.getTotalRecords() > 0 && recordCount >= dfd.getTotalRecords()) {
        break;
      }
      String record = generator.next();
      if(record == null) {
        if(generator.getErrorMessage() != null) {
          job.setJobStatus("Error:" + generator.getErrorMessage());
          if(observer != null && generator.getErrorMessage() != null) {
            observer.datageGenError(generator.getErrorMessage());
          }
        }
        break;
      }
      recordLogger.info(record);
      
      recordCount++;
      tpsCount++;
      if(observer != null) {
        observer.dataGenProgressContinue("" + recordCount, (int)recordCount);
      }
      job.setGeneratedRecords(recordCount);
      
      if(dfd.getTps() > 0 && tpsCount > dfd.getTps()) {
        long gap = 1000 - (System.currentTimeMillis() - startTime);
        if(gap > 0) {
          try {
            Thread.sleep(gap);
          } catch (InterruptedException e) {
          }
        }
        startTime = System.currentTimeMillis();
        tpsCount = 0;
      }
      lastTouchTime = System.currentTimeMillis();
      if(recordCount % 100000 == 0) {
        System.out.println(recordCount + " generated");
      }
    }
    if(observer != null) {
      observer.dataGenEnd();
    }
    job.setJobStatus("End");
  }
  
  public void resumeGenerator() {
    pause.set(false);
    job.setJobStatus("Running");
    synchronized(pause) {
      pause.notify();
    }
  }
  
  public void pauseGenerator() {
    pause.set(true);
    job.setJobStatus("Pause");
  }
  
  public void stopGenerator() {
    stop.set(true);
    synchronized(pause) {
      pause.notify();
    }
    this.interrupt(); 
  }
  
  public long getLastTouchTime() {
    return lastTouchTime;
  }

  public boolean isPaused() {
    return pause.get();
  }
}
