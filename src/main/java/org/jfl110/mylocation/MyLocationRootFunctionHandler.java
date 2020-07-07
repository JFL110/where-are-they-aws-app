package org.jfl110.mylocation;

import org.jfl110.aws.AwsLambdaGuiceApp;
import org.jfl110.aws.DestinationSwitcher;
import org.jfl110.aws.GuiceFunctionHandler;
import org.jfl110.aws.SwitchDestination;
import org.jfl110.mylocation.photos.SyncS3PhotosHandler;
import org.jfl110.mylocation.status.StatusHandler;

/**
 * The actual AWS Lambda which delegates to the application handlers.
 * 
 * @author jim
 */
public class MyLocationRootFunctionHandler extends GuiceFunctionHandler {

	public MyLocationRootFunctionHandler() {
		super(DestinationSwitcher.Switcher.switcher(
				SwitchDestination.switchDestination(DestinationSwitcher.Switcher.DEFAULT, Void.class, StatusHandler.class), // <-- Default
				SwitchDestination.switchDestination("log-locations", ExposedLogLocationsInput.class, LogLocationsHandler.class),
				SwitchDestination.switchDestination("write-all-json", ExposedSecurityKeyInput.class, WriteAllPointsSummaryToS3Handler.class),
				SwitchDestination.switchDestination("sync-s3-photos", ExposedSecurityKeyInput.class, SyncS3PhotosHandler.class)));
	}


	@Override
	protected final AwsLambdaGuiceApp guiceApp() {
		return MyLocationApp.INSTANCE.get();
	}
}
