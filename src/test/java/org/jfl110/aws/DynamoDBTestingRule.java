package org.jfl110.aws;

import org.jfl110.aws.dynamodb.AmazonDynamoDBSupplier;
import org.junit.rules.ExternalResource;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Creates a local DynamoDB instance for testing.
 */
public class DynamoDBTestingRule extends ExternalResource {

	private AmazonDynamoDB amazonDynamoDB;
	private final ImmutableList<Class<?>> clazzes;

	public DynamoDBTestingRule(Class<?>... clazzes) {
		System.setProperty("sqlite4java.library.path", "build/libs");
		this.clazzes = ImmutableList.copyOf(clazzes);
	}


	@Override
	protected void before() throws Throwable {
		amazonDynamoDB = DynamoDBEmbedded.create().amazonDynamoDB();
		clazzes.forEach(c -> amazonDynamoDB.createTable(new DynamoDBMapper(amazonDynamoDB).generateCreateTableRequest(c)
				.withProvisionedThroughput(new ProvisionedThroughput(new Long(1), new Long(1)))));
	}


	AmazonDynamoDB getAmazonDynamoDB() {
		return amazonDynamoDB;
	}


	public Module module() {
		return new Module() {
			@Override
			public void configure(Binder binder) {
				binder.bind(AmazonDynamoDBSupplier.class).toInstance(() -> getAmazonDynamoDB());

			}
		};
	}


	public Injector injector(Module... modules) {
		return Guice.createInjector(FluentIterable.from(modules).append(module()).toList());
	}
}