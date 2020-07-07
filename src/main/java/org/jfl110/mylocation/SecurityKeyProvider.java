package org.jfl110.mylocation;

import java.util.Objects;
import java.util.Optional;

import org.jfl110.app.ConfigVariableExtractor;

/**
 * Injectable provider of the system security key property.
 * 
 * @author jim
 *
 */
public class SecurityKeyProvider {

	public Optional<String> getSecurityKey(String tennantId) {
		Objects.requireNonNull(tennantId);
		return ConfigVariableExtractor.readString("SECURITY_KEY_" + tennantId.toUpperCase());
	}
}