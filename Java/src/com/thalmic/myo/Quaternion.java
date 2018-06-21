package com.thalmic.myo;

public class Quaternion {
	//Most of the code was copied from the C++ API
	private double w, x, y, z;
	
	public Quaternion() {
		w = 1;
		x = y = z = 0;
	}
	public Quaternion(double x, double y, double z, double w) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Quaternion(Quaternion other) {
		this.w = other.w;
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}
	
	public static Quaternion fromAxisAngle(Vector3 axis, double angle) {
		return new Quaternion(
				axis.x() * Math.sin(angle / 2),
                axis.y() * Math.sin(angle / 2),
                axis.z() * Math.sin(angle / 2),
                Math.cos(angle / 2));
	}
	public static Vector3 rotate(Quaternion quat, Vector3 vec) {
		Quaternion q = new Quaternion(vec.x(), vec.y(), vec.z(), 0);
		Quaternion result = quat.multiply(q).multiply(quat.conjugate());
		return new Vector3(result.x, result.y, result.z);
	}
	public static Quaternion rotate(Vector3 from, Vector3 to) {
		Vector3 cross = from.cross(to);

	    // The product of the square of magnitudes and the cosine of the angle between from and to.
	    double cosTheta = from.dot(to);

	    // Return identity if the vectors are the same direction.
	    if (cosTheta >= 1) {
	        return new Quaternion();
	    }

	    // The product of the square of the magnitudes
	    double k = Math.sqrt(from.dot(from) * to.dot(to));

	    // Return identity in the degenerate case.
	    if (k <= 0) {
	        return new Quaternion();
	    }

	    // Special handling for vectors facing opposite directions.
	    if (cosTheta / k <= -1) {
	        Vector3 xAxis = new Vector3(1, 0, 0);
	        Vector3 yAxis = new Vector3(0, 1, 0);

	        cross = from.cross(Math.abs(from.dot(xAxis)) < 1 ? xAxis : yAxis);
	        k = cosTheta = 0;
	    }

	    return new Quaternion(
	        cross.x(),
	        cross.y(),
	        cross.z(),
	        k + cosTheta);
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
	public double w() {
		return w;
	}
	
	public Quaternion conjugate() {
		return new Quaternion(-x, -y, -z, w);
	}
	public Quaternion normalized() {
		double mag = Math.sqrt(x * x + y * y + z * z + w * w);
		return new Quaternion(x / mag, y / mag, z / mag, w / mag);
	}
	
	public Quaternion multiply(Quaternion rhs) {
		return new Quaternion(
				w * rhs.x + x * rhs.w + y * rhs.z - z * rhs.y,
	            w * rhs.y - x * rhs.z + y * rhs.w + z * rhs.x,
	            w * rhs.z + x * rhs.y - y * rhs.x + z * rhs.w,
	            w * rhs.w - x * rhs.x - y * rhs.y - z * rhs.z);
	}
}
