package com.gruter.generator.model;

public class GeneratorJob {
  private String jobId;
  private String definitionName;
  private long generatedRecords;
  private String jobStatus;
  
  public String getJobId() {
    return jobId;
  }
  public void setJobId(String jobId) {
    this.jobId = jobId;
  }
  public String getDefinitionName() {
    return definitionName;
  }
  public void setDefinitionName(String definitionName) {
    this.definitionName = definitionName;
  }
  public long getGeneratedRecords() {
    return generatedRecords;
  }
  public void setGeneratedRecords(long generatedRecords) {
    this.generatedRecords = generatedRecords;
  }
  public String getJobStatus() {
    return jobStatus;
  }
  public void setJobStatus(String jobStatus) {
    this.jobStatus = jobStatus;
  }
}
