package com.qucumbah.engine.util;

import com.qucumbah.engine.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.geometry.Point3D;

public class MultiPolygon extends ArrayList<Point3D> {
  private ArrayList<Point3D> texturePoints = new ArrayList<>();
  private boolean isTextured;

  public MultiPolygon(boolean isTextured) {
    this.isTextured = isTextured;
  }

  public MultiPolygon(ArrayList<Point3D> verts, ArrayList<Point3D> texturePoints) {
    this.addAll(verts);
    this.texturePoints.addAll(texturePoints);
    this.isTextured = true;
  }

  public MultiPolygon(ArrayList<Point3D> verts) {
    this.addAll(verts);
    this.isTextured = false;
  }

  public MultiPolygon(Point3D ...vertsArray) {
    List<Point3D> verts = Arrays.asList(vertsArray);
    this.addAll(verts);
    this.isTextured = false;
  }

  public MultiPolygon getTexture() {
    return new MultiPolygon(texturePoints);
  }

  public void setTexture(ArrayList<Point3D> texturePoints) {
    this.isTextured = true;
    this.texturePoints = new ArrayList<>();
    this.texturePoints.addAll(texturePoints);
  }
  public void setTexture(Point3D ...texturePointsArray) {
    this.isTextured = true;
    this.texturePoints = new ArrayList<>();
    List<Point3D> texturePoints = Arrays.asList(texturePointsArray);
    this.texturePoints.addAll(texturePoints);
  }
  public void setTexture(Polygon texturePolygon) {
    this.isTextured = true;
    this.texturePoints = new ArrayList<>();
    this.texturePoints.add(texturePolygon.getFirst());
    this.texturePoints.add(texturePolygon.getSecond());
    this.texturePoints.add(texturePolygon.getThird());
  }

  public void addTexturePoint(Point3D p) {
    if (!this.isTextured) {
      this.isTextured = true;
    }
    texturePoints.add(p);
  }

  public Point3D getTexturePoint(int index) {
    if (!this.isTextured) {
      return null;
    }
    return texturePoints.get(index);
  }

  public void removeTexture() {
    this.isTextured = false;
  }

  public ArrayList<Polygon> divideToTriangles() {
    if (this.size()<3) {
      return null;
    }

    ArrayList<Polygon> result = new ArrayList<>();

    for (int i = 1;i<this.size()-1;i++) {
      Polygon polygon = new Polygon(
          this.get(0),
          this.get(i),
          this.get(i+1));
      if (isTextured) {
        Polygon texture = new Polygon(
            this.getTexturePoint(0),
            this.getTexturePoint(i),
            this.getTexturePoint(i+1));
        polygon.setTexture(texture);
      }

      result.add(polygon);
    }

    return result;
  }

  public boolean isFullPolygon() {
    return this.size()>=3;
  }

  public String toString() {
    String s = "";

    if (this.size()==0) {
      return s;
    }

    for (Point3D p : this) {
      s = s+p+",";
    }

    return s.substring(0,s.length()-1);
  }

  public static void main(String[] args) {
    MultiPolygon mp = new MultiPolygon(false);
    mp.add(new Point3D(1,0,1));
    mp.add(new Point3D(1,1,1));
    mp.add(new Point3D(0,1,1));
    mp.add(new Point3D(0,0,1));

    for (Polygon p : mp.divideToTriangles()) {
      System.out.println(p);
    }
  }
}
