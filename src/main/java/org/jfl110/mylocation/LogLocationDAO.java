package org.jfl110.mylocation;

import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.jfl110.aws.dynamodb.DynamoDBMapperFront;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

/**
 * DAO for {@link LogLocationItem}
 * 
 * @author jim
 *
 */
class LogLocationDAO {

	private final DynamoDBMapperFront dynamoDBMapperFront;

	@Inject
	LogLocationDAO(DynamoDBMapperFront dynamoDBMapperFront) {
		this.dynamoDBMapperFront = dynamoDBMapperFront;
	}


	void save(List<LogLocationItem> items) {
		dynamoDBMapperFront.mapper().batchSave(items);
	}


	Stream<LogLocationItem> listAll() {
		return dynamoDBMapperFront.mapper().scan(LogLocationItem.class, new DynamoDBScanExpression()).stream();
	}
}