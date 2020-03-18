package org.jfl110.mylocation.photos;

import static org.jfl110.aws.dynamodb.CreateTableContributionFromMapper.contribution;

import org.jfl110.aws.dynamodb.AmazonDynamoDBCreateTableContribution;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class PhotosModule extends AbstractModule {
	@Override
	protected void configure() {
		Multibinder.newSetBinder(binder(), AmazonDynamoDBCreateTableContribution.class).addBinding().toInstance(contribution(PhotoDao.PhotoItem.class));
	}
}