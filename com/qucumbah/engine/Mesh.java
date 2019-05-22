package com.qucumbah.engine;

import static com.qucumbah.engine.util.VectMath.*;
import com.qucumbah.engine.util.MultiPolygon;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Locale;
import java.util.Arrays;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javafx.geometry.Point3D;
import static java.lang.Math.*;

public class Mesh extends ArrayList<Polygon> {
	private ArrayList<Polygon> textures = new ArrayList<>();
	private boolean hasTexture;
	private int textureWidth;
	private int textureHeight;
	private BufferedImage texture;

	public Mesh() {

	}

	public Mesh(String filename) throws IOException {
		super();
		File f = new File(filename);
		Scanner s;
		String line = "";
		ArrayList<Point3D> verts = new ArrayList<>();
		ArrayList<Polygon> polys = new ArrayList<>();
		ArrayList<Point3D> textureCoords = new ArrayList<>();
		ArrayList<Polygon> textures = new ArrayList<>();
		try (Scanner in = new Scanner(f).useLocale(Locale.US)) {
			while (in.hasNextLine()) {
				line = in.nextLine();
				if (line.length()==0)
					continue;
				s = new Scanner(line).useLocale(Locale.US);
				//System.out.println(line);
				switch (s.next()) {
					case "v":
						double a[] = new double[3];
						for (int i = 0;i<3;i++) {
							a[i] = s.nextDouble();
						}
						verts.add(new Point3D(a[0],a[1],a[2]));
						break;
					case "f":
						String faceLine = s.nextLine().substring(1);
						//Without substring the line starts with space,
						//creating empty element in the line below
						int nVerts = faceLine.split(" ").length;
						s = new Scanner(faceLine).useLocale(Locale.US);
						int verticeIndexes[] = new int[nVerts];
						int textureIndexes[] = new int[nVerts];
						boolean hasTextureCoordinate = false;
						for (int i = 0;i<nVerts;i++) {
							String[] numStrings = s.next().split("/");
							int[] nums = new int[numStrings.length];
							for (int j = 0;j<numStrings.length;j++) {
								if (numStrings[j].length()==0)
									nums[j] = -1;
								else
									nums[j] = Integer.parseInt(numStrings[j]);
							}
							verticeIndexes[i] = nums[0];
							if (nums.length>1) {
								textureIndexes[i] = nums[1];
								hasTextureCoordinate = true;
							}
						}
						/*
						MultiPolygon mp = new MultiPolygon(hasTextureCoordinate);
						for (int i = 0;i<nVerts;i++) {
							mp.add( verts.get(verticeIndexes[i]-1) );
							if (hasTextureCoordinate) {
								mp.addTexturePoint( textureCoords.get(textureIndexes[i]-1) );
							}
						}

						for (Polygon p : mp.divideToTriangles()) {
							if (!p.isTextured()) {
								System.exit(0);
							}
						}

						polys.addAll(mp.divideToTriangles());
						*/

						if (nVerts==3) {
							if (textureCoords.get(textureIndexes[0]-1)==null) {
								System.out.println(line);
								System.exit(0);
							}
							polys.add(new Polygon(
								verts.get(verticeIndexes[0]-1),
								verts.get(verticeIndexes[1]-1),
								verts.get(verticeIndexes[2]-1),
								textureCoords.get(textureIndexes[0]-1),
								textureCoords.get(textureIndexes[1]-1),
								textureCoords.get(textureIndexes[2]-1)
							));
							//if this polygon doesn't have texture textureIndex[0] will be -1
							//because sometimes "f" token looks like f 14//12 23//2 34//23
							//in this case we replace the middle element with -1
							if (hasTextureCoordinate && textureIndexes[0]!=-1) {
								textures.add(new Polygon(
									textureCoords.get(textureIndexes[0]-1),
									textureCoords.get(textureIndexes[1]-1),
									textureCoords.get(textureIndexes[2]-1)
								));
							}
							//I actually dont know what to do in this scenario, so I let it be black
							else
								textures.add(new Polygon(
									new Point3D(0,0,0),
									new Point3D(0,0,0),
									new Point3D(0,0,0)
								));
						} else {
							polys.add(new Polygon(
								verts.get(verticeIndexes[0]-1),
								verts.get(verticeIndexes[1]-1),
								verts.get(verticeIndexes[2]-1))
							);
							polys.add(new Polygon(
								verts.get(verticeIndexes[0]-1),
								verts.get(verticeIndexes[2]-1),
								verts.get(verticeIndexes[3]-1))
							);
							//if this polygon doesn't have texture textureIndex[0] will be -1
							//because sometimes "f" token looks like f 14//12 23//2 34//23
							//in this case we replace the middle element with -1
							if (hasTextureCoordinate && textureIndexes[0]!=-1) {
								textures.add(new Polygon(
									textureCoords.get(textureIndexes[0]-1),
									textureCoords.get(textureIndexes[1]-1),
									textureCoords.get(textureIndexes[2]-1)
								));
								textures.add(new Polygon(
									textureCoords.get(textureIndexes[0]-1),
									textureCoords.get(textureIndexes[2]-1),
									textureCoords.get(textureIndexes[3]-1)
								));
							}
							//I actually dont know what to do in this scenario, so I let it be black
							else {
								textures.add(new Polygon(
									new Point3D(0,0,0),
									new Point3D(0,0,0),
									new Point3D(0,0,0)
								));
								textures.add(new Polygon(
									new Point3D(0,0,0),
									new Point3D(0,0,0),
									new Point3D(0,0,0)
								));
							}
						}

						break;
					case "vt":
						double b[] = new double[3];
						//System.out.println(s.next());
						for (int i = 0;i<2;i++) {
							b[i] = s.nextDouble();
							//System.out.println(b[i]);
						}
						textureCoords.add(new Point3D(b[0],b[1],0));
						break;
					//default:
					//	in.nextLine();
				}
			}
		}
		catch (IOException e) {
			throw new IOException(line);
		}
		catch (Exception e) {
			System.out.println(line);
			throw e;
		}
		this.addAll(polys);
		this.textures.addAll(textures);
		System.out.println(this.size());
		System.out.println(textures.size());
	}

	public Mesh(double[][] a) {
		for (int i = 0;i<a.length;i++) {
			Point3D first = new Point3D(a[i][0],a[i][1],a[i][2]);
			Point3D second = new Point3D(a[i][3],a[i][4],a[i][5]);
			Point3D third = new Point3D(a[i][6],a[i][7],a[i][8]);
			Polygon p = new Polygon(first,second,third);
			add(p);
		}
	}

	public void rotate(double a, double b, double c) {
		//add origin
		double[][] rotX = new double[3][3];
		rotX[0][0] = 1;
		rotX[1][1] = cos(a);
		rotX[1][2] = -sin(a);
		rotX[2][1] = sin(a);
		rotX[2][2] = cos(a);

		double[][] rotY = new double[3][3];
		rotY[0][0] = cos(b);
		rotY[0][2] = sin(b);
		rotY[1][1] = 1;
		rotY[2][0] = -sin(b);
		rotY[2][2] = cos(b);

		double[][] rotZ = new double[3][3];
		rotZ[0][0] = cos(c);
		rotZ[0][1] = -sin(c);
		rotZ[1][0] = sin(c);
		rotZ[1][1] = cos(c);
		rotZ[2][2] = 1;

		double[][] rot = mulMat(mulMat(rotX,rotY),rotZ);

		for (Polygon p:this) {
			p.setFirst(mul3(p.getFirst(),rot));
			p.setSecond(mul3(p.getSecond(),rot));
			p.setThird(mul3(p.getThird(),rot));
		}
	}

	public void move(double x, double y, double z) {
		Point3D d = new Point3D(x,y,z);
		for (Polygon p:this) {
			p.setFirst(p.getFirst().add(d));
			p.setSecond(p.getSecond().add(d));
			p.setThird(p.getThird().add(d));
		}
	}

	public boolean hasTexture() {
		return this.hasTexture;
	}

	public void assignTexture(String filename) throws IOException {
		this.texture = ImageIO.read(new File(filename));
		this.hasTexture = true;
	}

	public Polygon getTextureForPolygon(int index) {
		return textures.get(index);
	}

	public BufferedImage getTexture() {
		return this.texture;
	}

	public static void main(String[] args) throws IOException {
		String faceLine = "35/13/3 36/14/3 22/12/3 41/11/3";
		int nVerts = faceLine.split(" ").length;
		System.out.println(nVerts);
	}
}
