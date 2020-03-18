package org.jfl110.mylocation;

import static org.jfl110.aws.dynamodb.CreateTableContributionFromMapper.contribution;

import org.jfl110.app.NowModule;
import org.jfl110.aws.AfterInjectorCreatedEvent;
import org.jfl110.aws.AwsLambdaGuiceApp;
import org.jfl110.aws.SetCorsAllowAllResponseTransformation;
import org.jfl110.aws.dynamodb.AmazonDynamoDBCreateTableContribution;
import org.jfl110.aws.dynamodb.CreateTablesOnStartupModule;
import org.jfl110.aws.dynamodb.SwitchingAmazonDynamoDBSupplierModule;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

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
			install(new CreateTablesOnStartupModule());
			install(new SwitchingAmazonDynamoDBSupplierModule());
			Multibinder.newSetBinder(binder(), AmazonDynamoDBCreateTableContribution.class).addBinding().toInstance(contribution(LogLocationItem.class));
			Multibinder.newSetBinder(binder(), AmazonDynamoDBCreateTableContribution.class).addBinding().toInstance(contribution(ManualLocationItem.class));
			Multibinder.newSetBinder(binder(), AfterInjectorCreatedEvent.class).addBinding().to(InsertDefaultData.class);

			// TODO REMOVE Allow all CORS
			install(new SetCorsAllowAllResponseTransformation.Module());
		}
	}
}