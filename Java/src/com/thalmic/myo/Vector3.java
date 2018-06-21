package com.thalmic.myo;

public class Vector3 {
	
	private float x, y, z;
	
	public Vector3() {
		x = y = z = 0;
	}
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3(Vector3 other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}
	
	public float x() {
		return x;
	}
	public float y() {
		return y;
	}
	public float z() {
		return z;
	}
	
	public float magnitude() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	public Vector3 normalized() {
		float mag = magnitude();
		return new Vector3(this.x / mag, this.y / mag, this.z / mag);
	}
	public float dot(Vector3 rhs) {
		return x * rhs.x + y * rhs.y + z * rhs.z;
	}
	public Vector3 cross(Vector3 rhs) {
		return new Vector3(
				y * rhs.z - z * rhs.y,
	            z * rhs.x - x * rhs.z,
	            x * rhs.y - y * rhs.x
		);
	}
	public float angleTo(Vector3 rhs) {
		return (float) Math.acos(dot(rhs) / (rhs.magnitude() * magnitude()));
	}
}
