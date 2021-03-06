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

	Clipping of texture is performed separately

	*/
	public static MultiPolygon clip(MultiPolygon poly, MultiPolygon bounds) {
		/*
		ArrayList<Point3D> outputList = new ArrayList<>();
		outputList.add(poly.getFirst());
		outputList.add(poly.getSecond());
		outputList.add(poly.getThird());
		ArrayList<Point3D> textureOutputList = new ArrayList<>();
		textureOutputList.add(poly.getTextureFirst());
		textureOutputList.add(poly.getTextureSecond());
		textureOutputList.add(poly.getTextureThird());
		*/
		ArrayList<Point3D> outputList = poly;
		ArrayList<Point3D> textureOutputList = poly.getTexture();

		for (int i = 0;i<bounds.size()-1;i++) {
			Point3D edgeStart = bounds.get(i);
			Point3D edgeEnd = bounds.get(i+1);

			ArrayList<Point3D> inputList = outputList;
			ArrayList<Point3D> textureInputList = textureOutputList;
			outputList = new ArrayList<>();
			textureOutputList = new ArrayList<>();

			for (int j = 0;j<inputList.size();j++) {
				Point3D currentPoint = inputList.get(j);
				Point3D prevPoint = inputList.get(
						(j+inputList.size()-1)%inputList.size());

				Point3D textureCurrentPoint = textureInputList.get(j);
				Point3D texturePrevPoint = textureInputList.get(
						(j+textureInputList.size()-1)%textureInputList.size());
				/*
				See sketch 2

				Does the same as
				Point3D intersectionPoint = getIntersection(
						prevPoint,
						currentPoint,
						edgeStart,
						edgeEnd
				);
				*/
				Point3D q = prevPoint;
				Point3D s = currentPoint.subtract(prevPoint);
				Point3D p = edgeStart;
				Point3D r = edgeEnd.subtract(edgeStart);

				Point3D qTexture = texturePrevPoint;
				Point3D sTexture = textureCurrentPoint.subtract(texturePrevPoint);

				double intersectionMultiplier = getIntersectionMultiplier(q,s,p,r);
				Point3D intersectionPoint = q.add(s.multiply(intersectionMultiplier));
				Point3D textureIntersectionPoint =
						qTexture.add(sTexture.multiply(intersectionMultiplier));

				if (isInsideEdge(currentPoint,edgeStart,edgeEnd)) {
					if (!isInsideEdge(prevPoint,edgeStart,edgeEnd)) {
						outputList.add(intersectionPoint);
						textureOutputList.add(textureIntersectionPoint);
					}
					outputList.add(currentPoint);
					textureOutputList.add(textureCurrentPoint);
				} else if (isInsideEdge(prevPoint,edgeStart,edgeEnd)) {
					outputList.add(intersectionPoint);
					textureOutputList.add(textureIntersectionPoint);
				}
			}
		}

		return new MultiPolygon(outputList,textureOutputList);
	}

	public static MultiPolygon clipZ(MultiPolygon poly, double clipZ) {
		ArrayList<Point3D> inputList = new ArrayList<>();
		inputList.addAll(poly);
		ArrayList<Point3D> textureInputList = new ArrayList<>();
		textureInputList.addAll(poly.getTexture());

		ArrayList<Point3D> outputList = new ArrayList<>();
		ArrayList<Point3D> textureOutputList = new ArrayList<>();

		for (int j = 0;j<inputList.size();j++) {
			Point3D currentPoint = inputList.get(j);
			Point3D prevPoint = inputList.get(
					(j+inputList.size()-1)%inputList.size());

			Point3D textureCurrentPoint = textureInputList.get(j);
			Point3D texturePrevPoint = textureInputList.get(
					(j+textureInputList.size()-1)%textureInputList.size());

			Point3D q = prevPoint;
			Point3D s = currentPoint.subtract(prevPoint);

			Point3D qTexture = texturePrevPoint;
			Point3D sTexture = textureCurrentPoint.subtract(texturePrevPoint);

			double intersectionMultiplier = getZIntersectionMultiplier(q,s,clipZ);
			Point3D intersectionPoint = q.add(s.multiply(intersectionMultiplier));
			Point3D textureIntersectionPoint =
					qTexture.add(sTexture.multiply(intersectionMultiplier));

			double currentPointZ = currentPoint.getZ();
			double prevPointZ = prevPoint.getZ();

			//Same as in clip(), but replaced isInsideEdge() with simpler check
			if (currentPointZ<clipZ) {
				if (!(prevPointZ<clipZ)) {
					outputList.add(intersectionPoint);
					textureOutputList.add(textureIntersectionPoint);
				}
				outputList.add(currentPoint);
				textureOutputList.add(textureCurrentPoint);
			} else if (prevPointZ<clipZ) {
				outputList.add(intersectionPoint);
				textureOutputList.add(textureIntersectionPoint);
			}
		}

		return new MultiPolygon(outputList,textureOutputList);
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
	Arguments: points q and s are starts of vectors that we
	want to find intersection of
	s and r are the vectors themselves (or vectors collinear to them);
	to find intersection:
	q+u*s = p+t*r, where t and u are constants
	cross product both sides by r:
	q^r + u*(s^r) = p^r
	u*(s^r) = (p-q)^r
	u = (p-q)^r / s^r
	where ^ is cross product
	so the resulting point is q+us
	see sketches 1-4
	Returns NaN if there is no intersection
	*/
	public static double getIntersectionMultiplier(
				Point3D q, Point3D s, Point3D p, Point3D r) {
		p = p.subtract(q);

		double pMinusqCrossr = cross(p,r);
		double sCrossr = cross(s,r);

		//s parallel to r, p not parallel to r => edge is parallel to out vector
		if (sCrossr==0) {
			return Double.NaN;
		}

		double u = pMinusqCrossr/sCrossr;
		return u;
	}

	public static Point3D getIntersectionDirected(
				Point3D q, Point3D s, Point3D p, Point3D r) {
		double u = getIntersectionMultiplier(q,s,p,r);
		return q.add(s.multiply(u));
	}

	/*
	does the same as getIntersectionDirected(q,s.subtract(q),p,r.subtract(p))
	*/
	public static Point3D getIntersection(
				Point3D q, Point3D s, Point3D p, Point3D r) {
		//return getIntersectionDirected(q,s.subtract(q),p,r.subtract(p));
		return q.add(
			s.subtract(q).
			multiply(getIntersectionMultiplier(q,s.subtract(q),p,r.subtract(p)))
		);
	}

	/*
	Same arguments as getIntersectionMultiplier:
	q is the start, s is a vector itself
	check sketches 5 and 6
	Returns NaN if there is no intersection
	*/
	public static double getZIntersectionMultiplier(
			Point3D q, Point3D s, double clipZ) {
		double qz = q.getZ();
		double sz = s.getZ();

		/*
		If vector is on the edge z clipping plane, we still count this as
		no intersection, so return should be 0, not NaN
		*/
		if (sz==0 && clipZ==qz) {
			return 0;
		}

		return (clipZ-qz)/sz;
	}

	public static void main(String[] args) {
		/*
		//clip() test
		Point3D q = new Point3D(2,5,0);
		Point3D s = new Point3D(-2,4,0);
		Point3D center = new Point3D(0,0,0);
		Point3D dir = new Point3D(0,6,0);

		System.out.println(getIntersection(q,s,center,dir));
		*/

		/*
		//clip() test with real bounds and texture
		int width = 8;
		int height = 6;

		Point3D screenBL = new Point3D(0,0,0);
		Point3D screenTL = new Point3D(0,height,0);
		Point3D screenTR = new Point3D(width,height,0);
		Point3D screenBR = new Point3D(width,0,0);

		MultiPolygon bounds = new MultiPolygon(screenBL,screenTL,screenTR,screenBR);

		Point3D v1 = new Point3D(4,8,0);
		Point3D v2 = new Point3D(4,2,0);
		Point3D v3 = new Point3D(-2,2,0);
		Point3D t1 = new Point3D(.4,.8,0);
		Point3D t2 = new Point3D(.4,.2,0);
		Point3D t3 = new Point3D(-.2,.2,0);

		Polygon triangle = new Polygon(v1,v2,v3);
		triangle.setTexture(t1,t2,t3);

		for (Polygon p:clip(triangle,bounds)) {
			System.out.println(p);
		}

		for (Polygon p:clip(triangle,bounds)) {
			System.out.println(p.getTexture());
		}
		*/

		/*
		//getZIntersectionMultiplier() test
		Point3D v1 = new Point3D(0,4,5);
		Point3D v2 = new Point3D(0,13,-4);
		double u = getZIntersectionMultiplier(v1,v2.subtract(v1),-2);
		System.out.println(u);
		System.out.println(v1.add(v2.subtract(v1).multiply(u)));
		*/

		/*
		//clipZ() test
		Point3D v1 = new Point3D(0,2,2);
		Point3D v2 = new Point3D(0,-1,-1);
		Point3D v3 = new Point3D(0,2,-2);
		Point3D v4 = new Point3D(0,4,2);
		Point3D v5 = new Point3D(0,3,3);
		Point3D t1 = new Point3D(0,2,2);
		Point3D t2 = new Point3D(0,-1,-1);
		Point3D t3 = new Point3D(0,2,-2);
		Point3D t4 = new Point3D(0,4,2);
		Point3D t5 = new Point3D(0,3,3);

		MultiPolygon triangle = new MultiPolygon(v1,v2,v3,v4,v5);
		triangle.setTexture(t1,t2,t3,t4,t5);

		for (Polygon p:clipZ(triangle,0)) {
			System.out.println(p);
		}
		*/
	}
}
