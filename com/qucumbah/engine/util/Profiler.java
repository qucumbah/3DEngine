package com.qucumbah.engine.util;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Profiler {
  private LinkedHashMap<String,Long> currentTimestapms;
  private LinkedHashMap<String,Long> summedTimestapms;
  private long prevTime;
  private int numberOfRecords;

  public static final int NANOSECONDS = 1;
  public static final int MICROSECONDS = 1000;
  public static final int MILISECONDS = 1000000;

  public Profiler() {
    numberOfRecords = 0;
    summedTimestapms = new LinkedHashMap<>();
  }

  public void start() {
    currentTimestapms = new LinkedHashMap<>();
    prevTime = System.nanoTime();
    add("recordStart");
  }

  public void add(String name) {
    if (currentTimestapms==null) {
      start();
    }

    long time = System.nanoTime()-prevTime;
    prevTime = System.nanoTime();

    currentTimestapms.put(name,time);
  }

  public void stop() {
    add("recordStop");

    numberOfRecords++;

    currentTimestapms.forEach((key,value)->{
      summedTimestapms.merge(
          key,
          value,
          (v1,v2)->v1+v2
      );
    });
  }

  public void logTotalTime() {
    logTotalTime(MICROSECONDS);
  }

  public void logTotalTime(int mode) {
    System.out.println("TIMESTAMPS:");
    summedTimestapms.forEach((key,value)->{
      System.out.println(key+":"+value/mode);
    });
  }

  //debug
  public static void main(String[] args) {
    Profiler p = new Profiler();

    p.start();
    int k = 0;
    for (int i = 0;i<1e6;i++) {
      k++;
    }
    p.add("firstDone");
    for (int i = 0;i<1e6;i++) {
      k++;
    }
    p.add("secondDone");
    for (int i = 0;i<1e7;i++) {
      k++;
    }
    p.stop();

    p.logTotalTime(MILISECONDS);
  }
}
