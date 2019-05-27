package com.qucumbah.engine;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.imageio.ImageIO;

import java.util.*;
import java.nio.file.*;
import java.io.*;

import java.awt.image.BufferedImage;
import javafx.util.Pair;
import javafx.geometry.Point3D;

import com.qucumbah.engine.util.Mat4;
import static com.qucumbah.engine.util.VectMath.*;

public class Main extends JFrame {
	private World world;
	private Keyboard keyboard;
	private long elapsedTime;
	private int tickTime = 10;

	public static void main(String[] args) {
		new Main().run();
	}

	public void run() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		world = createWorld();
		Render r = new Render(world);
		add(r);
		pack();
		setVisible(true);
		keyboard = new Keyboard();
		addKeyListener(keyboard);

		Timer t = new Timer(tickTime,e->{
			repaint();
			world.movePlayer(keyboard.getMovementDirection());
			world.rotatePlayer(keyboard.getRotation());
			elapsedTime+=tickTime;
		});
		t.start();
	}

	private class Keyboard extends KeyAdapter {
		private double x, y, z, pitch, yaw;
		private double MOVEMENT_SPEED = 0.07;
		private double ROTATION_SPEED = 0.02;

		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_D: x=MOVEMENT_SPEED; break;
				case KeyEvent.VK_A: x=-MOVEMENT_SPEED; break;
				case KeyEvent.VK_R: y=MOVEMENT_SPEED; break;
				case KeyEvent.VK_F: y=-MOVEMENT_SPEED; break;
				case KeyEvent.VK_W: z=MOVEMENT_SPEED; break;
				case KeyEvent.VK_S: z=-MOVEMENT_SPEED; break;

				case KeyEvent.VK_UP: pitch=ROTATION_SPEED; break;
				case KeyEvent.VK_DOWN: pitch=-ROTATION_SPEED; break;
				case KeyEvent.VK_LEFT: yaw=ROTATION_SPEED; break;
				case KeyEvent.VK_RIGHT: yaw=-ROTATION_SPEED; break;
			}
		}

		public void keyReleased(KeyEvent e) {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_D: x=0; break;
				case KeyEvent.VK_A: x=0; break;
				case KeyEvent.VK_R: y=0; break;
				case KeyEvent.VK_F: y=0; break;
				case KeyEvent.VK_W: z=0; break;
				case KeyEvent.VK_S: z=0; break;

				case KeyEvent.VK_UP: pitch=0; break;
				case KeyEvent.VK_DOWN: pitch=0; break;
				case KeyEvent.VK_LEFT: yaw=0; break;
				case KeyEvent.VK_RIGHT: yaw=0; break;
			}
		}

		public Point3D getMovementDirection() {
			return new Point3D(x,y,z);
		}
		public Point3D getRotation() {
			return new Point3D(pitch,yaw,0);
		}
	}

	private World createWorld() {
		World w = new World();

		double[][] cubeArr = new double[][] {/*
			// SOUTH
			{ 0.0, 0.0, 0.0,    0.0, 1.0, 0.0,    1.0, 1.0, 0.0 },
			{ 0.0, 0.0, 0.0,    1.0, 1.0, 0.0,    1.0, 0.0, 0.0 },

			// EAST
			{ 1.0, 0.0, 0.0,    1.0, 1.0, 0.0,    1.0, 1.0, 1.0 },
			{ 1.0, 0.0, 0.0,    1.0, 1.0, 1.0,    1.0, 0.0, 1.0 },*/

			// NORTH
			{ 1.0, 0.0, 1.0,    1.0, 1.0, 1.0,    0.0, 1.0, 1.0 },
			{ 1.0, 0.0, 1.0,    0.0, 1.0, 1.0,    0.0, 0.0, 1.0 },/*

			// WEST
			{ 0.0, 0.0, 1.0,    0.0, 1.0, 1.0,    0.0, 1.0, 0.0 },
			{ 0.0, 0.0, 1.0,    0.0, 1.0, 0.0,    0.0, 0.0, 0.0 },

			// TOP
			{ 0.0, 1.0, 0.0,    0.0, 1.0, 1.0,    1.0, 1.0, 1.0 },
			{ 0.0, 1.0, 0.0,    1.0, 1.0, 1.0,    1.0, 1.0, 0.0 },

			// BOTTOM
			{ 1.0, 0.0, 1.0,    0.0, 0.0, 1.0,    0.0, 0.0, 0.0 },
			{ 1.0, 0.0, 1.0,    0.0, 0.0, 0.0,    1.0, 0.0, 0.0 }*/
		};

		Mesh frontCube = new Mesh(cubeArr);
		frontCube.move(0,0,5);
		//w.add(frontCube);

		Mesh testCube = new Mesh(cubeArr);

		try {
			Mesh model;
			//model = new Mesh("com/qucumbah/res/Spaceship2.obj");
			model = new Mesh("com/qucumbah/res/head.obj"); model.assignTexture("com/qucumbah/res/head.png");
			//model = new Mesh("com/qucumbah/res/Handgun_obj.obj");
			//model = new Mesh("com/qucumbah/res/Handgun_obj_tri.obj");
			//model = testCube;
			//model = new Mesh("com/qucumbah/res/testMesh.obj"); model.assignTexture("com/qucumbah/res/missing.png");
			w.add(model);
			w.movePlayer(new Point3D(0,0,10));
			//model.rotate(0,45.0/180*Math.PI,0);
			model.rotate(0,15.0/180*Math.PI,0);
			Timer t = new Timer(10,e->model.rotate(.01,-.02,0.01));
			//t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}


		return w;
	}

	private class Render extends JComponent {
		private int width = 1000;
		private int height = 800;

		private double maxZ;
		private double minZ;

		private World world;

		public Render(World world) {
			this.world = world;
		}

		public void paint(Graphics g1) {
			Graphics2D g = (Graphics2D)g1;

			Mat4 projection = Mat4.identity();
			//center = eye+eyeDir, eyeDir is normalized => camera is always at (0,0,1)
			projection.set(3,2,-1);

			Mat4 viewport = new Mat4(new double[][] {
				{width/2.0,0,0,width/2.0},
				{0,-height/2.0,0,height/2.0},
				{0,0,1,1},
				{0,0,0,1}
			});

			Point3D playerPosition = world.getPlayerPosition();
			Point3D playerLook = world.getPlayerLook();
			Mat4 view = lookat(playerPosition,playerPosition.add(playerLook),new Point3D(0,1,0));

			Mat4 z = viewport.mul(projection.mul(view));

			BufferedImage frame = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

			int meshNumber = 0; //debug
			double[][] zBuffer = new double[width][height];
			minZ = Double.POSITIVE_INFINITY;
			maxZ = Double.NEGATIVE_INFINITY;
			for (int i = 0;i<width;i++)
				for (int j = 0;j<height;j++)
					zBuffer[i][j] = Double.NEGATIVE_INFINITY;

			for (Mesh m:world) {
				for (int i = 0;i<m.size();i++) {
					Polygon pRaw = m.get(i);

					Point3D v1 = pRaw.getSecond().subtract(pRaw.getFirst());
					Point3D v2 = pRaw.getThird().subtract(pRaw.getSecond());
					Point3D normal = v1.crossProduct(v2);

					Point3D viewLine = pRaw.getFirst().subtract(world.getPlayerPosition());

					if (viewLine.dotProduct(normal)>0)
						continue;

					normal = normal.normalize();
					Point3D sunDirection = world.getSunDirection().normalize();
					double illumination = normal.dotProduct(sunDirection);

					Point3D first = z.mul(pRaw.getFirst());
					Point3D second = z.mul(pRaw.getSecond());
					Point3D third = z.mul(pRaw.getThird());
					Polygon polygon = new Polygon(first, second, third);

					polygon.setTexture(pRaw.getTexture());

					//drawTriangle(frame,polygon);
					//fillTriangle(frame,polygon,illumination);

					fillTriangle(frame,polygon,m.getTexture(),illumination,zBuffer);

					/*
					if (m.hasTexture()) {
						fillTriangle(frame,polygon,m.getTextureForPolygon(i),m.getTexture(),illumination,zBuffer);
					} else {
						fillTriangle(frame,polygon,illumination,zBuffer);
					}
					*/
				}
				meshNumber++; //debug
			}

			//frame = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			//displayZBuffer(frame,zBuffer);
			frame.setRGB(width/2,height/2,new Color(255,0,0).getRGB());

			g.drawRenderedImage(frame,null);
		}

		private Mat4 lookat(Point3D eye, Point3D center, Point3D up) {
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

		private double[] pointAsArray(Point3D p) {
			return new double[] {p.getX(), p.getY(), p.getZ()};
		}

		private void drawTriangle(BufferedImage g, Polygon p) {
			Point3D first = p.getFirst();
			Point3D second = p.getSecond();
			Point3D third = p.getThird();

			drawLine(g,(int)first.getX(),(int)first.getY(),(int)second.getX(),(int)second.getY());
			drawLine(g,(int)second.getX(),(int)second.getY(),(int)third.getX(),(int)third.getY());
			drawLine(g,(int)third.getX(),(int)third.getY(),(int)first.getX(),(int)first.getY());
		}

		private void drawLine(BufferedImage img, int x0, int y0, int x1, int y1) {
			int color = new Color(255,255,255).getRGB();
			for (float t = 0;t<1;t+=0.001) {
				int x = (int)(x0*(1-t)+x1*t);
				int y = (int)(y0*(1-t)+y1*t);
				if (x<0 || x>=img.getWidth() || y<0 || y>=img.getHeight())
					continue;
				img.setRGB(x,y,color);
			}
		}

		private class Point {
			public final int x;
			public final int y;
			public final double z;

			public Point(int x, int y) {
				this.x = x;
				this.y = y;
				this.z = 0;
			}
			public Point(Point3D p) {
				this.x = (int)(p.getX()+0.5);
				this.y = (int)(p.getY()+0.5);
				this.z = p.getZ();
			}

			public Point3D add(Point other) {
				return new Point3D(this.x+other.x,this.y+other.y,this.z+other.z);
			}
			public Point3D subtract(Point other) {
				return new Point3D(this.x-other.x,this.y-other.y,this.z-other.z);
			}
			public Point3D multiply(double k) {
				return new Point3D(this.x*k,this.y*k,this.z*k);
			}
			public Point3D add(Point3D other) {
				return new Point3D(this.x+other.getX(),this.y+other.getY(),this.z+other.getZ());
			}
			public Point3D subtract(Point3D other) {
				return new Point3D(this.x-other.getX(),this.y-other.getY(),this.z-other.getZ());
			}

			public String toString() {
				return "("+x+";"+y+";"+z+")";
			}
		}

		private void fillTriangle(BufferedImage img, Polygon p, double illumination, double[][] zBuffer) {
			//int tone = (int)(255*illumination);
			//int color = new Color(0,(int)(255*illumination),0).getRGB();
			//int color = new Color(tone).getRGB();
			if (illumination<0)
				illumination = 0;
			if (illumination>1)
				illumination = 1;

			float tone = (float)illumination;

			int color = new Color(0,0,tone).getRGB();

			if (p.getFirst().getY()==p.getSecond().getY() && p.getSecond().getY()==p.getThird().getY())
				return;
			Point[] points = new Point[3];
			points[0] = new Point(p.getFirst());
			points[1] = new Point(p.getSecond());
			points[2] = new Point(p.getThird());

			Arrays.sort(points,(e1,e2)->e1.y-e2.y);
			//System.out.println(Arrays.toString(points));

			int totalHeight = points[2].y-points[0].y;
			for (int i = 0;i<totalHeight;i++) {
				int firstHalfHeight = points[1].y-points[0].y;
				boolean secondHalf = i>firstHalfHeight || firstHalfHeight==0;
				int segmentHeight = secondHalf?points[2].y-points[1].y:points[1].y-points[0].y;

				double alpha = (double)i/totalHeight;
				double beta = (double)(i-(secondHalf ? points[1].y-points[0].y : 0))/segmentHeight;
				Point3D a = points[0].add(points[2].subtract(points[0]).multiply(alpha));
				Point3D b = secondHalf ?
					points[1].add(points[2].subtract(points[1]).multiply(beta)) :
					points[0].add(points[1].subtract(points[0]).multiply(beta));
				if (a.getX()>b.getX()) {
					Point3D temp = a;
					a = b;
					b = temp;
				}
				Point aInt = new Point(a);
				Point bInt = new Point(b);
				for (int j = aInt.x; j<bInt.x;j++) {
					if (j<0 || j>=img.getWidth() || points[0].y+i<0 || points[0].y+i>=img.getHeight())
						continue;

					double phi = aInt.x==bInt.x ? 1 : (double)(j-aInt.x) / (double)(bInt.x-aInt.x);
					Point P = new Point(a.add(b.subtract(a).multiply(phi)));

					if (P.z<minZ)
						minZ = P.z;
					if (P.z>maxZ)
						maxZ = P.z;

					//if (P.x==width/2 && P.y==height/2)
					//	System.out.println(P.z);

					if (zBuffer[P.x][P.y]<P.z) {
						img.setRGB(P.x,P.y,color);
						zBuffer[P.x][P.y] = P.z;
					}
				}
			}
		}

		private void fillTriangle( //shameful & awful & iwannakms
						BufferedImage img,
						Polygon p,
						BufferedImage texture,
						double illumination,
						double[][] zBuffer) {
			if (illumination<0)
				illumination = 0;
			if (illumination>1)
				illumination = 1;

			float tone = (float)illumination;

			if (p.getFirst().getY()==p.getSecond().getY() && p.getSecond().getY()==p.getThird().getY())
				return;

			p.sortByY();

			Point[] points = new Point[3];
			Point3D[] textureCoords = new Point3D[3];

			points[0] = new Point(p.getFirst());
			points[1] = new Point(p.getSecond());
			points[2] = new Point(p.getThird());

			//TODO: it p isn't textured assign it pink texture
			if (p.isTextured()) {
				textureCoords[0] = p.getTextureFirst();
				textureCoords[1] = p.getTextureSecond();
				textureCoords[2] = p.getTextureThird();
			} else {
				textureCoords[0] = new Point3D(0,0,0);
				textureCoords[1] = new Point3D(0,0,0);
				textureCoords[2] = new Point3D(0,0,0);
			}

			/*
			System.out.println(Arrays.toString(points));
			System.out.println(Arrays.toString(textureCoords));
			System.out.println(p);
			*/
			int totalHeight = points[2].y-points[0].y;
			for (int i = 0;i<totalHeight;i++) {
				int firstHalfHeight = points[1].y-points[0].y;
				boolean secondHalf = i>firstHalfHeight || firstHalfHeight==0;
				int segmentHeight = secondHalf?points[2].y-points[1].y:points[1].y-points[0].y;

				double alpha = (double)i/totalHeight;
				double beta = (double)(i-(secondHalf ? points[1].y-points[0].y : 0))/segmentHeight;
				Point3D a = points[0].add(points[2].subtract(points[0]).multiply(alpha));
				Point3D b = secondHalf ?
					points[1].add(points[2].subtract(points[1]).multiply(beta)) :
					points[0].add(points[1].subtract(points[0]).multiply(beta));

				Point3D aTexture = textureCoords[0].add(textureCoords[2].subtract(textureCoords[0]).multiply(alpha));
				Point3D bTexture = secondHalf ?
					textureCoords[1].add(textureCoords[2].subtract(textureCoords[1]).multiply(beta)) :
					textureCoords[0].add(textureCoords[1].subtract(textureCoords[0]).multiply(beta));

				if (a.getX()>b.getX()) {
					//swap a and b
					Point3D temp = a;
					a = b;
					b = temp;
					//swap aTexture and bTexture
					temp = aTexture;
					aTexture = bTexture;
					bTexture = temp;
				}
				Point aInt = new Point(a);
				Point bInt = new Point(b);
				for (int j = aInt.x; j<bInt.x;j++) {
					if (j<0 || j>=img.getWidth() || points[0].y+i<0 || points[0].y+i>=img.getHeight())
						continue;

					double phi = aInt.x==bInt.x ? 1 : (double)(j-aInt.x) / (double)(bInt.x-aInt.x);
					Point P = new Point(a.add(b.subtract(a).multiply(phi)));
					Point3D textureP = aTexture.add(bTexture.subtract(aTexture).multiply(phi));

					if (P.z<minZ)
						minZ = P.z;
					if (P.z>maxZ)
						maxZ = P.z;

					int textureColor = 0;
					//subtract 1 because texture coordinates in the image are [0;width-1]
					int tWidth = texture.getWidth()-1;
					int tHeight = texture.getHeight()-1;
					if (p.isTextured()) {
						textureColor = texture.getRGB(
							(int)(textureP.getX()*tWidth),
							tHeight-(int)(textureP.getY()*tHeight)
						);
						Color c = new Color(textureColor);


						float[] hsbValues = new float[3];
						Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),hsbValues);
						hsbValues[2]*=illumination;
						textureColor = Color.getHSBColor(hsbValues[0],hsbValues[1],hsbValues[2]).getRGB();
					} else {
						textureColor = new Color(0,0,(float)illumination).getRGB();
					}

					if (zBuffer[P.x][P.y]<P.z) {
						img.setRGB(P.x,P.y,textureColor);
						zBuffer[P.x][P.y] = P.z;
					}
				}
			}
		}

		private void displayZBuffer(BufferedImage img, double[][] zBuffer) {
			//System.out.println(maxZ+";"+minZ);
			for (int i = 0;i<width;i++) {
				for (int j = 0;j<height;j++) {
					if (zBuffer[i][j]>maxZ || zBuffer[i][j]<minZ)
						continue;
					//System.out.println("call");
					float tone = (float)(((double)zBuffer[i][j]-minZ)/(maxZ-minZ));
					try {
						int color = new Color(0,tone,0).getRGB();
						img.setRGB(i,j,color);
					}
					catch(Exception e) {
						//System.out.println(zBuffer[i][j]+";"+maxZ+";"+minZ);
					}
				}
			}
		}

		public Dimension getPreferredSize() {
			return new Dimension(width,height);
		}
	}
}
