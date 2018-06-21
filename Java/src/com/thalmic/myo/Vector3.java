package com.thalmic.myo;

public class Vector3 {
	
	private double x, y, z;
	
	public Vector3() {
		x = y = z = 0;
	}
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3(Vector3 other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}
	
	public double x() {
		return x;
	}
	public double y() {
		return y;
	}
	public double z() {
		return z;
	}
	
	public double magnitude() {
		return (double) Math.sqrt(x * x + y * y + z * z);
	}
	public Vector3 normalized() {
		double mag = magnitude();
		return new Vector3(this.x / mag, this.y / mag, this.z / mag);
	}
	public double dot(Vector3 rhs) {
		return x * rhs.x + y * rhs.y + z * rhs.z;
	}
	public Vector3 cross(Vector3 rhs) {
		return new Vector3(
				y * rhs.z - z * rhs.y,
	            z * rhs.x - x * rhs.z,
	            x * rhs.y - y * rhs.x
		);
	}
	public double angleTo(Vector3 rhs) {
		return Math.acos(dot(rhs) / (rhs.magnitude() * magnitude()));
	}
}
