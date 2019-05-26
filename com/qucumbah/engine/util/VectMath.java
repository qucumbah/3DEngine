package com.qucumbah.engine.util;

import com.qucumbah.engine.Polygon;
import com.qucumbah.engine.util.MultiPolygon;

import static java.lang.Math.*;
import javafx.geometry.Point3D;
import java.util.ArrayList;

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

	/*
	Uses Sutherland–Hodgman algorithm
	Each edge is a pair of points defining its start and end
	To get them we take elements (n,n+1) from bounds list,
	where 0<=n<=list.length-2
	Bounds have to be in clockwise order
	*/
	public static ArrayList<Polygon> clip(Polygon p, MultiPolygon bounds) {
		ArrayList<Point3D> outputList = new ArrayList<>();
		outputList.add(p.getFirst());
		outputList.add(p.getSecond());
		outputList.add(p.getThird());

		for (Point3D boundsPoly:bounds) {
			System.out.println(boundsPoly);
		}

		for (int i = 0;i<bounds.size()-1;i++) {
			Point3D edgeStart = bounds.get(i);
			Point3D edgeEnd = bounds.get(i+1);

			ArrayList<Point3D> inputList = outputList;
			outputList = new ArrayList<>();

			for (int j = 0;j<inputList.size();j++) {
				Point3D currentPoint = inputList.get(j);
				Point3D prevPoint = inputList.get(
						(j+inputList.size()-1)%inputList.size());
				Point3D intersectionPoint = getIntersection(
						prevPoint,
						currentPoint,
						edgeStart,
						edgeEnd
				);

				if (isInsideEdge(currentPoint,edgeStart,edgeEnd)) {
					if (!isInsideEdge(prevPoint,edgeStart,edgeEnd)) {
						outputList.add(intersectionPoint);
					}
					outputList.add(currentPoint);
				} else if (isInsideEdge(prevPoint,edgeStart,edgeEnd)) {
					outputList.add(intersectionPoint);
				}
			}
		}

		return new MultiPolygon(outputList).divideToTriangles();
	}

	/*
	is inside edge==is to the right of the edge
	*/
	public static boolean isInsideEdge(
			Point3D p, Point3D edgeStart, Point3D edgeEnd) {
		Point3D vRight = p.subtract(edgeStart);
		Point3D vLeft = edgeEnd.subtract(edgeStart);
		return cross(vRight,vLeft)>=0;
	}

	/*

	*/
	public static Point3D getIntersection(
			Point3D q, Point3D s, Point3D p, Point3D r) {
		p = p.subtract(q);

		double pMinusqCrossr = cross(p,r);
		double sCrossr = cross(s,r);

		//s parallel to r, p not parallel to r => edge is parallel to out vector
		if (sCrossr==0) {
			return null;
		}

		double u = pMinusqCrossr/sCrossr;
		return q.add(s.multiply(u));
	}

	public static void main(String[] args) {
		/*
		Point3D q = new Point3D(2,3,0);
		Point3D s = new Point3D(-3,1,0);
		Point3D center = new Point3D(0,0,0);
		Point3D dir = new Point3D(0,1,0);

		System.out.println(getIntersection(q,s,center,dir));
		*/

		int width = 800;
		int height = 600;

		Point3D screenBL = new Point3D(0,0,0);
		Point3D screenTL = new Point3D(0,height,0);
		Point3D screenTR = new Point3D(width,height,0);
		Point3D screenBR = new Point3D(width,0,0);

		MultiPolygon bounds = new MultiPolygon(screenBL,screenTL,screenTR,screenBR);

		Point3D v1 = new Point3D(200,500,0);
		Point3D v2 = new Point3D(-200,400,0);
		Point3D v3 = new Point3D(-200,600,0);

		Polygon triangle = new Polygon(v1,v2,v3);

		System.out.println(getIntersection(v2,v1,screenBL,screenTL));
		/*
		for (Polygon p:clip(triangle,bounds)) {
			System.out.println(p);
		}
		*/
	}
}
