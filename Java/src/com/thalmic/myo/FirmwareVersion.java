package com.thalmic.myo;

/**
 * Firmware version of {@link Myo}.
 *
 */
public class FirmwareVersion {
	/**
	 * {@link Myo}'s major version must match the required major version.
	 * <p>Note that this field is unsigned.</p>
	 */
	public int firmwareVersionMajor;
	/**
	 * {@link Myo}'s minor version must match the required minor version.
	 * <p>Note that this field is unsigned.</p>
	 */
	public int firmwareVersionMinor;
	/**
	 * {@link Myo}'s patch version must be greater or equal to the required patch version.
	 * <p>Note that this field is unsigned.</p>
	 */
	public int firmwareVersionPatch;
	/**
	 * {@link Myo}'s hardware revision; not used to detect firmware version mismatch.
	 * <p>Note that this field is unsigned.</p>
	 */
	public int firmwareVersionHardwareRev;
}
