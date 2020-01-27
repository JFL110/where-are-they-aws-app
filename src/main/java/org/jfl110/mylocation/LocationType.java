package org.jfl110.mylocation;

/**
 * Locations are either AUTO (logged from Android app) or MANUAL (inserted
 * manually).
 * 
 * @author jim
 *
 */
enum LocationType {

	AUTO("A"), MANUAL("M");

	private final String code;

	private LocationType(String code) {
		this.code = code;
	}


	String getCode() {
		return code;
	}
}