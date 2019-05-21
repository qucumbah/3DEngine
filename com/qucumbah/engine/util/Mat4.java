package com.qucumbah.engine.util;

import static java.lang.Math.*;
import javafx.geometry.Point3D;
import java.util.Arrays;

public class Mat4 {
	private double[][] a;

	public Mat4(double[][] a) {
		this.a = a;
	}

	public static Mat4 identity() {
		return new Mat4(new double[][] {
			{1,0,0,0},
			{0,1,0,0},
			{0,0,1,0},
			{0,0,0,1}
		});
	}

	public Point3D mul(Point3D i) {
		double x,y,z,w;

		x = i.getX()*a[0][0] + i.getY()*a[0][1] + i.getZ()*a[0][2] + a[0][3];
		y = i.getX()*a[1][0] + i.getY()*a[1][1] + i.getZ()*a[1][2] + a[1][3];
		z = i.getX()*a[2][0] + i.getY()*a[2][1] + i.getZ()*a[2][2] + a[2][3];
		w = i.getX()*a[3][0] + i.getY()*a[3][1] + i.getZ()*a[3][2] + a[3][3];

		if (w!=0) {
			x/=w;
			y/=w;
			z/=w;
		}
		else
			return new Point3D(0,0,0);

		//test
		//z = i.getZ();

		return new Point3D(x,y,z);
	}

	public Mat4 mul(Mat4 other) {
		double[][] b = other.a;

		if (a[0].length!=b.length)
			return null;

		int l = a.length;
		int m = a[0].length;
		int n = b[0].length;

		double[][] c = new double[l][n];
		for (int row = 0;row<l;row++)
			for (int col = 0;col<n;col++) {
				double sum = 0;
				for (int i = 0;i<m;i++)
					sum+=a[row][i]*b[i][col];
				c[row][col]=sum;
			}

		return new Mat4(c);
	}

	public void set(int i, int j, double val) {
		a[i][j] = val;
	}

	public double get(int i, int j) {
		return a[i][j];
	}

	public String toString() {
		return Arrays.deepToString(a);
	}

	//debug

	private static void outputMatrix(Mat4 m) {
		for (int i = 0;i < 4;i++) {
			for (int j = 0;j < 4;j++) {
				System.out.printf("%f ", m.get(i,j));
			}
			System.out.printf("\n");
		}
	}

	private static void outputVector(double[] p) {
		for (int i = 0;i < 3;i++) {
			System.out.printf("%f\n",p[i]);
		}
	}

	private static void outputVector(Point3D pVect) {
		double[] p = pointAsArray(pVect);
		for (int i = 0;i < 3;i++) {
			System.out.printf("%f\n",p[i]);
		}
	}

	private static Mat4 lookat(Point3D eye, Point3D center, Point3D up) {
		Point3D zVect = eye.subtract(center).normalize();
		Point3D xVect = up.crossProduct(zVect).normalize();
		Point3D yVect = zVect.crossProduct(xVect).normalize();

		double[] x = pointAsArray(xVect);
		double[] y = pointAsArray(yVect);
		double[] z = pointAsArray(zVect);
		double[] centerArr = pointAsArray(center);

		Mat4 minV = Mat4.identity();
		Mat4 translate = Mat4.identity();
		for (int i = 0;i<3;i++) {
			minV.set(0,i,x[i]);
			minV.set(1,i,y[i]);
			minV.set(2,i,z[i]);
			translate.set(i,3,-centerArr[i]);
		}

		return minV.mul(translate);
	}

	private static double[] pointAsArray(Point3D p) {
		return new double[] {p.getX(), p.getY(), p.getZ()};
	}

	public static void main(String[] args) {
		int width = 1000;
		int height = 800;

		Mat4 viewport = new Mat4(new double[][] {
			{width/2.0,0,0,width/2.0},
			{0,height/2.0,0,height/2.0},
			{0,0,1,1},
			{0,0,0,1}
		});

		Point3D p = new Point3D(-0.5,-0.5,1);
		outputVector(viewport.mul(p));
	}

}
