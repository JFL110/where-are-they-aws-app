package org.jfl110.mylocation;

import java.util.stream.Stream;

import javax.inject.Inject;

import org.jfl110.aws.dynamodb.DynamoDBMapperFront;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

/**
 * 
 * DAO for {@link ManualLocationItem}
 * 
 * The default item exists as a template for manual insertion.
 * 
 * @author jim
 */
class ManualLocationsDAO {

	static final String DEFAULT_ID = "template";
	private final DynamoDBMapperFront dynamoDBMapperFront;

	@Inject
	ManualLocationsDAO(DynamoDBMapperFront dynamoDBMapperFront) {
		this.dynamoDBMapperFront = dynamoDBMapperFront;
	}


	Stream<ManualLocationItem> listAll() {
		return dynamoDBMapperFront.mapper().scan(ManualLocationItem.class, new DynamoDBScanExpression()).stream()
				.filter(i -> !DEFAULT_ID.equals(i.getId()));
	}


	void saveDefaultItem(ManualLocationItem item) {
		if (!DEFAULT_ID.equals(item.getId())) {
			throw new IllegalArgumentException("ID must be default");
		}
		dynamoDBMapperFront.save(item);
	}


	boolean defaultItemExists() {
		return dynamoDBMapperFront.loadIfExists(ManualLocationItem.class, DEFAULT_ID).isPresent();
	}
}