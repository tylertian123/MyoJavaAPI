package com.thalmic.myo;

/**
 * A quaternion that can be used to represent a rotation. <br>
 * <br>
 * This type provides only very basic functionality to store quaternions that's sufficient to retrieve the data
 * to be placed in a full featured quaternion type. 
 *
 */
public class Quaternion implements Cloneable {
	//Most of the code was copied from the C++ API
	private double w, x, y, z;
	
	/**
	 * Construct a quaternion that represents zero rotation (i.e. the multiplicative identity). 
	 */
	public Quaternion() {
		w = 1;
		x = y = z = 0;
	}
	/**
	 * Construct a quaternion with the provided components. 
	 * @param x The x component of the vector component of the quaternion.
	 * @param y The y component of the vector component of the quaternion.
	 * @param z The z component of the vector component of the quaternion.
	 * @param w The scalar part of the quaternion.
	 */
	public Quaternion(double x, double y, double z, double w) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	/**
	 * Creates a quaternion with the components equal to another quaterion.
	 * @param other The quaternion whose components will be copied.
	 */
	public Quaternion(Quaternion other) {
		this.w = other.w;
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}
	
	/**
	 * Set the components of this quaternion to be those of the other.<br>
	 * <br>
	 * This method is provided as a Java counterpart of the overloaded C++ "=" operator.
	 * @param other The quaternion whose components will be copied.
	 * @return This quaternion, after updating the components.
	 */
	public Quaternion equal(Quaternion other) {
		this.w = other.w;
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		return this;
	}

	/**
	 * Clones this quaternion.
	 */
	@Override
	public Quaternion clone() {
		return new Quaternion(this);
	}
	
	/**
	 * Return a quaternion that represents a right-handed rotation of <em>angle</em> radians about the 
	 * given <em>axis</em>. 
	 * @param axis The axis of rotation.
	 * @param angle The angle of rotation, in radians.
	 * @return A quaternion that represents a right-handed rotation of <em>angle</em> radians about the 
	 * given <em>axis</em>. 
	 */
	public static Quaternion fromAxisAngle(Vector3 axis, double angle) {
		return new Quaternion(
				axis.x() * Math.sin(angle / 2),
                axis.y() * Math.sin(angle / 2),
                axis.z() * Math.sin(angle / 2),
                Math.cos(angle / 2));
	}
	/**
	 * Return a copy of this <em>vec</em> rotated by <em>quat</em>.
	 * @param quat The quaternion representing the rotation.
	 * @param vec The vector to be rotated.
	 * @return A copy of this <em>vec</em> rotated by <em>quat</em>.
	 */
	public static Vector3 rotate(Quaternion quat, Vector3 vec) {
		Quaternion q = new Quaternion(vec.x(), vec.y(), vec.z(), 0);
		Quaternion result = quat.multiply(q).multiply(quat.conjugate());
		return new Vector3(result.x, result.y, result.z);
	}
	/**
	 * Return a quaternion that represents a rotation from vector <em>from</em> to <em>to</em>.<br>
	 * <br>
	 * See <a href="http://stackoverflow.com/questions/1171849/finding-quaternion-representing-the-rotation-from-one-vector-to-another">
	 * http://stackoverflow.com/questions/1171849/finding-quaternion-representing-the-rotation-from-one-vector-to-another</a>
	 * for some explanation.  
	 * @param from The original vector.
	 * @param to The vector to rotate to.
	 * @return A quaternion that represents a rotation from vector <em>from</em> to <em>to</em>. 
	 */
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
	
	/**
	 * Return the x-component of this quaternion's vector.
	 * @return The x-component of this quaternion's vector.
	 */
	public double x() {
		return x;
	}
	/**
	 * Return the y-component of this quaternion's vector.
	 * @return The y-component of this quaternion's vector.
	 */
	public double y() {
		return y;
	}
	/**
	 * Return the z-component of this quaternion's vector.
	 * @return The z-component of this quaternion's vector.
	 */
	public double z() {
		return z;
	}
	/**
	 * Return the w-component (scalar) of this quaternion. 
	 * @return The w-component (scalar) of this quaternion. 
	 */
	public double w() {
		return w;
	}
	
	/**
	 * Return this quaternion's conjugate.
	 * @return This quaternion's conjugate.
	 */
	public Quaternion conjugate() {
		return new Quaternion(-x, -y, -z, w);
	}
	/**
	 * Return the unit quaternion corresponding to the same rotation as this one. 
	 * @return The unit quaternion corresponding to the same rotation as this one. 
	 */
	public Quaternion normalized() {
		double mag = Math.sqrt(x * x + y * y + z * z + w * w);
		return new Quaternion(x / mag, y / mag, z / mag, w / mag);
	}
	/**
	 * Return the quaternion multiplied by <em>rhs</em>. <br>
	 * <br>
	 * Note that quaternion multiplication is not commutative.<br>
	 * <br>
	 * This method is provided as a Java counterpart of the overloaded C++ "*" operator.
	 * @param rhs
	 * @return
	 */
	public Quaternion multiply(Quaternion rhs) {
		return new Quaternion(
				w * rhs.x + x * rhs.w + y * rhs.z - z * rhs.y,
	            w * rhs.y - x * rhs.z + y * rhs.w + z * rhs.x,
	            w * rhs.z + x * rhs.y - y * rhs.x + z * rhs.w,
	            w * rhs.w - x * rhs.x - y * rhs.y - z * rhs.z);
	}
	/**
	 * Multiply this quaternion by <em>rhs</em>.<br>
	 * <br>
	 * Return this quaternion updated with the result.<br>
	 * <br>
	 * This method is provided as a Java counterpart of the overloaded C++ "*=" operator.
	 * @param rhs This quaternion updated with the result.  
	 */
	public Quaternion multiplyEquals(Quaternion rhs) {
		equal(multiply(rhs));
		return this;
	}
}
