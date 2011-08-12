package com.gruter.generator.service;

import generator.misc.DataFileDefinition;

import java.util.Collection;
import java.util.List;

import com.gruter.generator.model.GeneratorJob;

public interface IGeneratorService {
  public void loadFileDefinitions();
  
  public List<DataFileDefinition> getFileDefinitions();
  
  public String generateLog(DataFileDefinition dataFileDefinition, boolean background) throws Exception;
  
  public Collection<GeneratorJob> getGeneratorJobs();
  
  public void pauseGenerator(String jobId);
  
  public void resumeGenerator(String jobId);
  
  public void stopGenerator(String jobId);

}
