package com.qucumbah.engine.util;

import static java.lang.Math.*;
import javafx.geometry.Point3D;

public class VectMath {
	private VectMath() {

	}

	public static Point3D mul4(Point3D i, double[][] m) {
		double x,y,z,w;

		x = i.getX()*m[0][0] + i.getY()*m[1][0] + i.getZ()*m[2][0] + m[3][0];
		y = i.getX()*m[0][1] + i.getY()*m[1][1] + i.getZ()*m[2][1] + m[3][1];
		z = i.getX()*m[0][2] + i.getY()*m[1][2] + i.getZ()*m[2][2] + m[3][2];
		w = i.getX()*m[0][3] + i.getY()*m[1][3] + i.getZ()*m[2][3] + m[3][3];

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

	public static Point3D mul3(Point3D i, double[][] m) {
		double x,y,z;

		x = i.getX()*m[0][0] + i.getY()*m[0][1] + i.getZ()*m[0][2];
		y = i.getX()*m[1][0] + i.getY()*m[1][1] + i.getZ()*m[1][2];
		z = i.getX()*m[2][0] + i.getY()*m[2][1] + i.getZ()*m[2][2];

		return new Point3D(x,y,z);
	}

	public static double[][] mulMat(double[][] a, double[][] b) {
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

		return c;
	}

	private static double cross(Point3D p1, Point3D p2) {
		return p1.getX()*p2.getY()-p1.getY()*p2.getX();
	}

	public static Point3D getIntersection(Point3D q, Point3D s, Point3D center, Point3D dir) {
		q = q.subtract(center);
		double qsCross = cross(q,s);
		double dirsCross = cross(dir,s);

		System.out.println(qsCross);
		System.out.println(dirsCross);

		//q not parallel to s, s||dir => no intersection
		if (dirsCross==0 && qsCross!=0)
			return null;
		return dir.multiply(qsCross/dirsCross); //careful with z multiplication
	}

	public static void main(String[] args) {
		Point3D q = new Point3D(2,4,0);
		Point3D s = new Point3D(-3,2,0);
		Point3D center = new Point3D(0,0,0);
		Point3D dir = new Point3D(0,1,0);
		System.out.println(getIntersection(q,s,center,dir));
	}
}
