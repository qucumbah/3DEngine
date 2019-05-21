package com.qucumbah.engine;

import javafx.geometry.Point3D;
import java.util.ArrayList;

import static java.lang.Math.*;
import static com.qucumbah.engine.util.VectMath.*;

public class World extends ArrayList<Mesh> {
	private double pitch, yaw;
	private Point3D forward, up;
	private Point3D playerPosition;
	private Point3D sunDirection = new Point3D(0,0,1);

	public World() {
		updatePlayerRotation();
		yaw = PI;
		playerPosition = new Point3D(0,0,0);
	}

	public void movePlayer(Point3D dir) {
		Point3D right = forward.crossProduct(up).normalize();

		Point3D changeForward = forward.multiply(dir.getZ());
		Point3D changeUp = up.multiply(dir.getY());
		Point3D changeRight = right.multiply(dir.getX());

		playerPosition = playerPosition
			.add(changeForward)
			.add(changeUp)
			.add(changeRight);
	}

	public void rotatePlayer(Point3D rot) {
		rotatePlayer(rot.getX(), rot.getY());
	}

	public void rotatePlayer(double pitch, double yaw) {
		this.pitch+=pitch;
		this.yaw+=yaw;

		this.pitch = min(this.pitch,PI/2);
		this.pitch = max(this.pitch,-PI/2);
		updatePlayerRotation();
	}

	public void updatePlayerRotation() {
		//worst code
		Point3D forward = new Point3D(0,sin(pitch),cos(pitch));
		Point3D up = new Point3D(0,sin(pitch+PI/2),cos(pitch+PI/2));
		//System.out.println(forward);
		//System.out.println(up);

		double[][] rotY = new double[3][3];
		rotY[0][0] = cos(yaw);
		rotY[0][2] = sin(yaw);
		rotY[1][1] = 1;
		rotY[2][0] = -sin(yaw);
		rotY[2][2] = cos(yaw);

		this.forward = mul3(forward,rotY);
		this.up = mul3(up,rotY);
	}

	public void setSunDirection(Point3D sunDirection) {
		this.sunDirection = sunDirection;
	}

	public Point3D getSunDirection() {
		return sunDirection;
	}

	public Point3D getPlayerPosition() {
		return playerPosition;
	}

	public Point3D getPlayerLook() {
		return forward;
	}
}
