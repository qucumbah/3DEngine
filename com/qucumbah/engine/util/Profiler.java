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
    System.out.println("Average time ("+numberOfRecords+" calls):");
    summedTimestapms.forEach((key,value)->{
      System.out.println(key+":"+value/numberOfRecords/mode);
    });
  }

  //debug
  public static double millionAdditions() {
    double result = 0;
    for (int i = 0;i<1e6;i++) {
      result+=Math.random();
    }
    return result;
  }

  public static double millionMultiplications() {
    double result = 1;
    for (int i = 0;i<1e6;i++) {
      result*=Math.random();
    }
    return result;
  }

  public static double millionP3DOperations() {
    for (int i = 0;i<1e6;i++) {
      javafx.geometry.Point3D p1 = new javafx.geometry.Point3D(Math.random(),Math.random(),Math.random());
      javafx.geometry.Point3D p2 = new javafx.geometry.Point3D(Math.random(),Math.random(),Math.random());
      p1.crossProduct(p2);
    }
    return 0;
  }

  public static void main(String[] args) {
    Profiler p = new Profiler();

    p.start();
    millionAdditions();
    p.add("additions");
    millionMultiplications();
    p.add("multiplications");
    millionP3DOperations();
    p.add("Point3D operations");
    p.stop();

    p.logTotalTime(100);

    for (int i = 0;i<5;i++) {
      p.start();
      millionAdditions();
      p.add("additions");
      millionMultiplications();
      p.add("multiplications");
      System.out.println(millionP3DOperations());
      p.add("Point3D operations");
      p.stop();
    }

    p.logTotalTime(100);
  }
}
