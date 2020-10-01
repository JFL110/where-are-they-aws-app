package org.jfl110.aws;

import java.util.Arrays;
import java.util.List;

import org.jfl110.dynamodb.AmazonDynamoDBSupplier;
import org.jfl110.dynamodb.AppTableNamePrefixSupplier;
import org.jfl110.dynamodb.GlobalSecondaryIndexSchema;
import org.junit.rules.ExternalResource;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
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
	private final AppTableNamePrefixSupplier prefix;

	public DynamoDBTestingRule(Class<?>... clazzes) {
		this("", clazzes);
	}


	public DynamoDBTestingRule(String prefix, Class<?>... clazzes) {
		System.setProperty("sqlite4java.library.path", "build/libs");
		this.clazzes = ImmutableList.copyOf(clazzes);
		this.prefix = () -> prefix;
	}


	@Override
	protected void before() throws Throwable {
		amazonDynamoDB = DynamoDBEmbedded.create().amazonDynamoDB();

		clazzes.forEach(c -> {
			GlobalSecondaryIndexSchema gsiAnnotation = c.getAnnotation(GlobalSecondaryIndexSchema.class);
			List<GlobalSecondaryIndex> gsis = gsiAnnotation == null ? ImmutableList.of()
					: Guice.createInjector().getInstance(gsiAnnotation.value()).get();

			amazonDynamoDB.createTable(new DynamoDBMapper(amazonDynamoDB, DynamoDBMapperConfig.builder()
					.withTableNameOverride(TableNameOverride.withTableNamePrefix(prefix.get()))
					.build()).generateCreateTableRequest(c)
							.withGlobalSecondaryIndexes(gsis.isEmpty() ? null : gsis)
							.withProvisionedThroughput(new ProvisionedThroughput(new Long(1), new Long(1))));
		});
	}


	AmazonDynamoDB getAmazonDynamoDB() {
		return amazonDynamoDB;
	}


	public Module module() {
		return new Module() {
			@Override
			public void configure(Binder binder) {
				binder.bind(AmazonDynamoDBSupplier.class).toInstance(() -> getAmazonDynamoDB());
				binder.bind(AppTableNamePrefixSupplier.class).toInstance(prefix);
			}
		};
	}


	public Injector injector(Module... modules) {
		return Guice.createInjector(FluentIterable.from(Arrays.asList(modules)).append(module()).toList());
	}
}