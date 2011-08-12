package com.gruter.generator.service;

import generator.engine.ProgressUpdateObserver;
import generator.misc.DataFileDefinition;

public class GeneratorProgressObserver implements ProgressUpdateObserver {
  DataFileDefinition dfd;
  public GeneratorProgressObserver(DataFileDefinition dfd) {
    this.dfd = dfd;
  }

  @Override
  public void dataGenEnd() {
    System.out.println("========= End ========");
  }

  @Override
  public void dataGenMaxProgressValue(int maxProgress) {
    //System.out.println("maxProgress:" + maxProgress);
  }

  @Override
  public boolean dataGenProgressContinue(String msg, int progress) {
    float rate = (float)progress/(float)dfd.getTotalRecords() * 100.0f; 
    if(rate % 5 == 0 ) {
      System.out.println(rate + " % completed, " + msg);
    }
    return false;
  }

  @Override
  public void dataGenStarted() {
    System.out.println("========= Start ========");
  }

  @Override
  public void datageGenError(String msg) {
    System.out.println("Error:" + msg);
  }
}
