package org.jfl110.mylocation;

import org.jfl110.app.NowModule;
import org.jfl110.aws.AwsLambdaGuiceApp;
import org.jfl110.aws.DestinationSwitcher;
import org.jfl110.aws.GuiceFunctionHandler;
import org.jfl110.aws.SetCorsAllowAllResponseTransformation;
import org.jfl110.aws.SwitchDestination;
import org.jfl110.dynamodb.CreateTablesOnStartupModule;
import org.jfl110.dynamodb.DynamoDBTablePrefixModule;
import org.jfl110.dynamodb.SwitchingAmazonDynamoDBSupplierModule;
import org.jfl110.genericitem.DynamoGenericItemModule;
import org.jfl110.mylocation.photos.SyncS3PhotosHandler;
import org.jfl110.mylocation.status.StatusHandler;

import com.google.inject.AbstractModule;

/**
 * The actual AWS Lambda which delegates to the application handlers.
 * 
 * @author jim
 */
public class MyLocationRootFunctionHandler extends GuiceFunctionHandler {

	public MyLocationRootFunctionHandler() {
		super(DestinationSwitcher.Switcher.switcher(
				SwitchDestination.switchDestination(DestinationSwitcher.Switcher.DEFAULT, StatusHandler.class), // <-- Default
				SwitchDestination.switchDestination("log-locations", LogLocationsHandler.class),
				SwitchDestination.switchDestination("write-all-json", WriteAllPointsSummaryToS3Handler.class),
				SwitchDestination.switchDestination("sync-s3-photos", SyncS3PhotosHandler.class)));
	}


	@Override
	protected final AwsLambdaGuiceApp guiceApp() {
		return new MyLocationApp();
	}


	@Override
	protected String appName() {
		return MyLocationRootFunctionHandler.class.getSimpleName();
	}

	/**
	 * Guice App for the My Location
	 * 
	 * @author jim
	 */
	static class MyLocationApp extends AwsLambdaGuiceApp {

		private MyLocationApp() {
			super(new MyLocationModule());
		}

		private static class MyLocationModule extends AbstractModule {
			@Override
			protected void configure() {

				// Now
				install(new NowModule());

				// DynamoDB
				install(new DynamoDBTablePrefixModule(MyLocationAppConfig.TABLE_NAME_PREFIX));
				install(new CreateTablesOnStartupModule());
				install(new SwitchingAmazonDynamoDBSupplierModule());
				install(new DynamoGenericItemModule());

				// Allow all cross-origin requests
				install(new SetCorsAllowAllResponseTransformation.Module());
			}
		}
	}
}
