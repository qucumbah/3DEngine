package com.qucumbah.engine.util;

import com.qucumbah.engine.Polygon;

import java.util.ArrayList;
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

  public void addTexturePoint(Point3D p) {
    texturePoints.add(p);
  }

  public Point3D getTexturePoint(int index) {
    return texturePoints.get(index);
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
