package org.jfl110.mylocation;

import org.jfl110.app.NowModule;
import org.jfl110.aws.AwsLambdaGuiceApp;
import org.jfl110.aws.SetCorsAllowAllResponseTransformation;
import org.jfl110.dynamodb.CreateTablesOnStartupModule;
import org.jfl110.dynamodb.DynamoDBTablePrefixModule;
import org.jfl110.dynamodb.SwitchingAmazonDynamoDBSupplierModule;
import org.jfl110.genericitem.DynamoGenericItemModule;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;

/**
 * Guice App for the My Location
 * 
 * @author jim
 */
class MyLocationApp extends AwsLambdaGuiceApp {

	/**
	 * The single instance of this app, which wraps a single instance of the
	 * injector
	 **/
	static final Supplier<MyLocationApp> INSTANCE = Suppliers.memoize(MyLocationApp::new);

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