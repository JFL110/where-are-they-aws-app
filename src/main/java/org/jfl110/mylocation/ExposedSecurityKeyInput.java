package org.jfl110.mylocation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExposedSecurityKeyInput {

	private final String tenantId;
	private final String securityKey;

	@JsonCreator
	public ExposedSecurityKeyInput(
			@JsonProperty("tenantId") String tenantId,
			@JsonProperty("securityKey") String securityKey) {
		this.tenantId = tenantId;
		this.securityKey = securityKey;
	}


	public String getSecurityKey() {
		return securityKey;
	}


	public String getTenantId() {
		return tenantId;
	}
}
