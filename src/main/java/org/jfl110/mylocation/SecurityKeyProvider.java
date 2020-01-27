package org.jfl110.mylocation;

/**
 * Injectable provider of the system security key property.
 * 
 * @author jim
 *
 */
class SecurityKeyProvider {
	String getSecurityKey() {
		return System.getenv("SECURITY_KEY");
	}
}