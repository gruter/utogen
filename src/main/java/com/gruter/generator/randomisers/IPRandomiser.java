package com.gruter.generator.randomisers;

import java.util.Random;

import generator.extenders.IRandomiserFunctionality;
import generator.extenders.RandomiserInstance;

public class IPRandomiser implements IRandomiserFunctionality {
  private Random rand1;
  private Random rand2;
  private Random rand3;
  private Random rand4;
  
  @Override
  public void destroy() {
  }

  @Override
  public Object generate() {
    return rand1.nextInt(256) + "." + rand2.nextInt(256) + "." + rand3.nextInt(256) + "." + rand4.nextInt(256);
  }

  @Override
  public void setRandomiserInstance(RandomiserInstance ri) {
    rand1 = new Random(System.currentTimeMillis());
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    rand2 = new Random(System.currentTimeMillis());
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }
    rand3 = new Random(System.currentTimeMillis());
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    } 
    rand4 = new Random(System.currentTimeMillis());
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }        
  }
}
