package org.jfl110.mylocation.photos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * Bean for output of {@link SyncS3PhotosHandler}
 * @author jim
 *
 */
public class ExposedSyncS3PhotosOutput {

	private final ImmutableList<String> logLines;

	ExposedSyncS3PhotosOutput(ImmutableList<String> logLines) {
		this.logLines = logLines;
	}
	
	@JsonProperty("log")
	public ImmutableList<String> getLogLines() {
		return logLines;
	}
}