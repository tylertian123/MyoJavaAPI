package com.thalmic.myo;

/**
 * A vector of three components.<br>
 * <br>
 * This type provides very basic functionality to store a three dimensional vector that's sufficient to retrieve
 * the data to be placed in a full featured vector type. A few common vector operations, such as dot product and
 * cross product, are also provided. 
 */
public class Vector3 implements Cloneable {
	
	//Some of the code was copied from the Vector3 class of the C++ Myo API.
	private double x, y, z;
	
	/**
	 * Construct a vector of all zeroes. 
	 */
	public Vector3() {
		x = y = z = 0;
	}
	/**
	 * Construct a vector with the three provided components. 
	 * @param x The x component.
	 * @param y The y component.
	 * @param z The z component.
	 */
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	/**
	 * Construct a vector with the same components as <em>other</em>. 
	 * @param other The vector whose components will be copied.
	 */
	public Vector3(Vector3 other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}
	
	/**
	 * Clones this vector.
	 */
	@Override
	public Vector3 clone() {
		return new Vector3(this);
	}
	
	/**
	 * Set the components of this vector to be the same as other.<br>
	 * <br>
	 * This method is provided as a Java counterpart of the overloaded C++ "=" operator.
	 * @param other The vector whose components will be copied.
	 * @return This vector, after updating the components.
	 */
	public Vector3 equal(Vector3 other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		return this;
	}
	
	/**
	 * Return a copy of the component of this vector at <em>index</em>, which should be 0, 1, or 2.<br>
	 * <br>
	 * This method is provided as a Java counterpart of the overloaded C++ "[]" operator.
	 * @param index The index of the component to retrieve.
	 * @return The component at the specified index.
	 * @throws ArrayIndexOutOfBoundsException If <em>index</em> is not 0, 1, or 2.
	 */
	public double atIndex(int index) {
		switch(index) {
		case 0:
			return x;
		case 1:
			return y;
		case 2: 
			return z;
		default:
			throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	/**
	 * Return the x-component of this vector. 
	 * @return The x-component of this vector. 
	 */
	public double x() {
		return x;
	}
	/**
	 * Return the y-component of this vector. 
	 * @return The y-component of this vector. 
	 */
	public double y() {
		return y;
	}
	/**
	 * Return the z-component of this vector. 
	 * @return The z-component of this vector. 
	 */
	public double z() {
		return z;
	}
	
	/**
	 * Return the magnitude of this vector.
	 * @return The magnitude of this vector.
	 */
	public double magnitude() {
		return (double) Math.sqrt(x * x + y * y + z * z);
	}
	/**
	 * Return a normalized copy of this vector.
	 * @return A normalized copy of this vector.
	 */
	public Vector3 normalized() {
		double mag = magnitude();
		return new Vector3(this.x / mag, this.y / mag, this.z / mag);
	}
	/**
	 * Return the dot product of this vector and <em>rhs</em>. 
	 * @param rhs The vector to take the dot product with.
	 * @return The dot product of this vector and <em>rhs</em>.
	 */
	public double dot(Vector3 rhs) {
		return x * rhs.x + y * rhs.y + z * rhs.z;
	}
	/**
	 * Return the cross product of this vector and <em>rhs</em>. 
	 * @param rhs The vector to take the cross product with.
	 * @return The cross product of this vector and <em>rhs</em>.
	 */
	public Vector3 cross(Vector3 rhs) {
		return new Vector3(
				y * rhs.z - z * rhs.y,
	            z * rhs.x - x * rhs.z,
	            x * rhs.y - y * rhs.x
		);
	}
	/**
	 * Return the angle between this vector and <em>rhs</em>, in radians.
	 * @param rhs The vector with whom to take the angle.
	 * @return The angle between this vector and <em>rhs</em>.
	 */
	public double angleTo(Vector3 rhs) {
		return Math.acos(dot(rhs) / (rhs.magnitude() * magnitude()));
	}
}
