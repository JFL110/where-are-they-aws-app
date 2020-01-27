package org.jfl110.mylocation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * JSON Bean output for {@link LogLocationsHandler}
 * 
 * OUTPUT ONLY
 * 
 * @author jim
 *
 */
public class ExposedLogLocationsOutput {

	private final ImmutableList<String> savedIds;

	ExposedLogLocationsOutput(ImmutableList<String> savedIds) {
		this.savedIds = savedIds;
	}


	@JsonProperty("savedIds")
	public List<String> getSavedIds() {
		return savedIds;
	}

}