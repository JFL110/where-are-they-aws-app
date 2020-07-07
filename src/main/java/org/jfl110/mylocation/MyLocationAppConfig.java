package org.jfl110.mylocation;

/**
 * Static config for this app
 * 
 * @author jim
 *
 */
public class MyLocationAppConfig {

	/** Default live tenant id **/
	public static final String LIVE_TENNANT_ID = "live";

	/** Error code returned for a bad security key */
	public static final String BAD_SECURITY_KEY = "bad-security-key";

	/** Region domain for S3 */
	public static final String S3_DOMAIN = ".s3.eu-west-2.amazonaws.com/";

	/** Table name prefixes for DynamoDB */
	public static final String TABLE_NAME_PREFIX = "MyLocation_";

}
