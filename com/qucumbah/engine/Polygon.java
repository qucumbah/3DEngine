package com.qucumbah.engine;

import javafx.geometry.Point3D;

public class Polygon {
	private Point3D first;
	private Point3D second;
	private Point3D third;

	private boolean isTextured;
	private Point3D tFirst;
	private Point3D tSecond;
	private Point3D tThird;

	public Polygon(Point3D first, Point3D second, Point3D third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public Polygon(Point3D first, Point3D second, Point3D third,
				   Point3D tFirst, Point3D tSecond, Point3D tThird) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.tFirst = tFirst;
		this.tSecond = tSecond;
		this.tThird = tThird;

		isTextured = true;
	}

	public Polygon getTexture() {
		return new Polygon(tFirst,tSecond,tThird);
	}

	public void setTexture(Polygon texture) {
		if (texture==null) {
			isTextured = false;
			return;
		}
		setTexture(texture.getFirst(),texture.getSecond(),texture.getThird());
		isTextured = true;
	}

	public void setTexture(Point3D tFirst, Point3D tSecond, Point3D tThird) {
		this.tFirst = tFirst;
		this.tSecond = tSecond;
		this.tThird = tThird;
	}

	public void sortByY() {
		Point3D temp;
		if (first.getY()>second.getY()) {
			temp = first;
			first = second;
			second = temp;
			temp = tFirst;
			tFirst = tSecond;
			tSecond = temp;
		}
		if (second.getY()>third.getY()) {
			temp = second;
			second = third;
			third = temp;
			temp = tSecond;
			tSecond = tThird;
			tThird = temp;
		}
		if (first.getY()>second.getY()) {
			temp = first;
			first = second;
			second = temp;
			temp = tFirst;
			tFirst = tSecond;
			tSecond = temp;
		}
	}

	public Point3D getFirst() {
		return first;
	}
	public Point3D getSecond() {
		return second;
	}
	public Point3D getThird() {
		return third;
	}

	public void setFirst(Point3D p) {
		this.first = p;
	}
	public void setSecond(Point3D p) {
		this.second = p;
	}
	public void setThird(Point3D p) {
		this.third = p;
	}

	public Point3D getTextureFirst() {
		if (!isTextured)
			return null;
		return tFirst;
	}
	public Point3D getTextureSecond() {
		if (!isTextured)
			return null;
		return tSecond;
	}
	public Point3D getTextureThird() {
		if (!isTextured)
			return null;
		return tThird;
	}

	public String toString() {
		return String.format("(%f:%f:%f),(%f:%f:%f),(%f:%f:%f)",
			first.getX(),first.getY(),first.getZ(),
			second.getX(),second.getY(),second.getZ(),
			third.getX(),third.getY(),third.getZ()
		);
	}
}
