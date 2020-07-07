package org.jfl110.mylocation.status;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Exposed bean for status information
 * 
 * OUTPUT only
 * 
 * @author jim
 */
public class ExposedStatusOutput {
	
	public static final String OK = "ok";

	private final String status;
	private final Optional<String> version;

	@JsonCreator
	public ExposedStatusOutput(@JsonProperty("status") String status, @JsonProperty("version") String version) {
		this.status = status;
		this.version = Optional.ofNullable(version);
	}


	@JsonProperty("status")
	public String getStatus() {
		return status;
	}


	@JsonProperty("version")
	public String getVersion() {
		return version.orElse(null);
	}

}
