package org.jfl110.mylocation;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.jfl110.aws.DynamoDBTestingRule;
import org.junit.ClassRule;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests for {@link LogLocationDAO}
 * 
 * @author jim
 *
 */
public class TestLogLocationDAO {

	@ClassRule public static final DynamoDBTestingRule dynamoRule = new DynamoDBTestingRule(LogLocationItem.class);

	private final LogLocationDAO dao = dynamoRule.injector().getInstance(LogLocationDAO.class);

	/**
	 * Tests (available) CRUD operations
	 */
	@Test
	public void testCrud() {

		// Given
		LogLocationItem item1 = new LogLocationItem("a", 1, 2, 3, 4, "dev1", "time1", "time2", "time3");
		LogLocationItem item2 = new LogLocationItem("b", 5, 6, 7, 8, "dev2", "time4", "time5", "time6");

		// When
		dao.save(ImmutableList.of(item1, item2));
		List<LogLocationItem> fetchedItems = dao.listAll().sorted((a, b) -> a.getId().compareTo(b.getId())).collect(Collectors.toList());

		// Then
		assertEquals(2, fetchedItems.size());
		assertEquals(item1.getId(), fetchedItems.get(0).getId());
		assertEquals(item1.getLatitude(), fetchedItems.get(0).getLatitude(), 0.001);
		assertEquals(item1.getLongitude(), fetchedItems.get(0).getLongitude(), 0.001);
		assertEquals(item1.getAccuracy(), fetchedItems.get(0).getAccuracy(), 0.001);
		assertEquals(item1.getAltitude(), fetchedItems.get(0).getAltitude(), 0.001);
		assertEquals(item1.getDeviceName(), fetchedItems.get(0).getDeviceName());
		assertEquals(item1.getTime(), fetchedItems.get(0).getTime());
		assertEquals(item1.getSavedTime(), fetchedItems.get(0).getSavedTime());
		assertEquals(item1.getEndTime(), fetchedItems.get(0).getEndTime());

		assertEquals(item2.getId(), fetchedItems.get(1).getId());
		assertEquals(item2.getId(), fetchedItems.get(1).getId());
		assertEquals(item2.getLatitude(), fetchedItems.get(1).getLatitude(), 0.001);
		assertEquals(item2.getLongitude(), fetchedItems.get(1).getLongitude(), 0.001);
		assertEquals(item2.getAccuracy(), fetchedItems.get(1).getAccuracy(), 0.001);
		assertEquals(item2.getAltitude(), fetchedItems.get(1).getAltitude(), 0.001);
		assertEquals(item2.getDeviceName(), fetchedItems.get(1).getDeviceName());
		assertEquals(item2.getTime(), fetchedItems.get(1).getTime());
		assertEquals(item2.getSavedTime(), fetchedItems.get(1).getSavedTime());
		assertEquals(item2.getEndTime(), fetchedItems.get(1).getEndTime());
	}
}
