package com.gruter.generator.service;

import generator.engine.ProgressUpdateObserver;
import generator.misc.DataFileDefinition;
import generator.misc.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.stereotype.Service;

import com.gruter.generator.model.GeneratorJob;

@Service("generatorService")
public class GeneratorService implements IGeneratorService {
  private Map<String, GeneratorJob> generatorJobs = new HashMap<String, GeneratorJob>();
  private Map<String, GeneratorJobThread> jobThreads = new HashMap<String, GeneratorJobThread>();
  private List<DataFileDefinition> fileDefinitions = new ArrayList<DataFileDefinition>();
  
  private AtomicInteger currentJobId = new AtomicInteger(0);
  private JobRemoveThread jobRemoveThread;
  
  public GeneratorService() {
    loadFileDefinitions();
    jobRemoveThread = new JobRemoveThread();
    jobRemoveThread.start();
  }

  @Override
  public void loadFileDefinitions() {
    Utils utils = new Utils();
    fileDefinitions.clear();
    fileDefinitions.addAll(utils.loadDataFileDefinitions());
  }
  
  @Override
  public List<DataFileDefinition> getFileDefinitions() {
    loadFileDefinitions();
    return fileDefinitions;
  }
  
  @Override
  public String generateLog(DataFileDefinition dataFileDefinition, boolean background) throws Exception {
    String jobId = "G_" + currentJobId.addAndGet(1);
    GeneratorJob generatorJob = new GeneratorJob();
    generatorJob.setJobId(jobId);
    generatorJob.setDefinitionName(dataFileDefinition.getName());
    generatorJob.setJobStatus("Running");
 
    ProgressUpdateObserver observer = null;
    if(!background) {
      observer = new GeneratorProgressObserver(dataFileDefinition);
    }
    GeneratorJobThread job = new GeneratorJobThread(generatorJob, dataFileDefinition, 
        configLogger(dataFileDefinition), observer);
    if(background) {
      job.start();
    } else {
      job.generateLog();
    }
    jobThreads.put(jobId, job);
    generatorJobs.put(jobId, generatorJob);
    
    return jobId;
  }
  
  private synchronized Logger configLogger(DataFileDefinition dfd)  throws Exception {
    Properties properties = new Properties();
    String log4j = dfd.getLog4j();
    BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(log4j.getBytes())));
    String line = null;
    properties.setProperty("log4j.reset", "true");
    try {
      while( (line = reader.readLine()) != null ) {
        if(line.trim().length() == 0) {
          continue;
        }
        int index = line.indexOf("=");
        if(index <= 0) {
          continue;
        }
        String key = line.substring(0, index).trim();
        String value = line.substring(index + 1).trim();
        properties.setProperty(key, value);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    PropertyConfigurator.configure(properties);
    
    Logger logger = Logger.getLogger(dfd.getLog4jName());
    if(logger.getLevel() == null) {
      throw new IOException("Can't get logger:" + dfd.getLog4jName());
    }
    return logger;
  }
  
  @Override
  public Collection<GeneratorJob> getGeneratorJobs() {
    return generatorJobs.values();
  }
  
  @Override
  public void pauseGenerator(String jobId) {
    System.out.println("Called pause job:" + jobId);
    synchronized(jobThreads) {
      GeneratorJobThread job = jobThreads.get(jobId);
      if(job.isAlive()) {
        job.pauseGenerator();
      }
    }
  }
  
  @Override
  public void resumeGenerator(String jobId) {
    System.out.println("Called resume job:" + jobId);
    synchronized(jobThreads) {
      GeneratorJobThread job = jobThreads.get(jobId);
      if(job.isPaused()) {
        job.resumeGenerator();
      }
    }
  }
  
  @Override
  public void stopGenerator(String jobId) {
    System.out.println("Called stop job:" + jobId);
    synchronized(jobThreads) {
      GeneratorJobThread job = jobThreads.get(jobId);
      if(job.isAlive()) {
        job.stopGenerator();
      }
    }
  }
  
  class JobRemoveThread extends Thread {
    @Override
    public void run() {
      while(true) {
        try {
          Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
          break;
        }
        List<String> deadJob = new ArrayList<String>();
        synchronized(jobThreads) {
          for(GeneratorJobThread eachJob: jobThreads.values()) {
            if(System.currentTimeMillis() - eachJob.getLastTouchTime() > 10 * 60 * 1000) {
              deadJob.add(eachJob.getJobId());
              if(eachJob.isAlive()) {
                eachJob.stopGenerator();
              }
            }
          }
          
          for(String eachJob: deadJob) {
            System.out.println("Remove Job:" + eachJob);
            jobThreads.remove(eachJob);
            generatorJobs.remove(eachJob);
          }
        }
      }
    }
  }

  public void stop() {
    if(jobRemoveThread != null) {
      jobRemoveThread.interrupt();
    }
  }
}
