package org.jfl110.mylocation;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jfl110.aws.DynamoDBTestingRule;
import org.jfl110.genericitem.DynamoGenericItemModule;
import org.junit.ClassRule;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests for {@link LogLocationDAO}
 * 
 * @author jim
 *
 */
public class TestAutoLogLocations {

	@ClassRule public static final DynamoDBTestingRule dynamoRule = new DynamoDBTestingRule(DynamoGenericItemModule.itemClazz());

	private static final String TENANT_ID = "test";
	private final LocationsDao dao = dynamoRule.injector(new DynamoGenericItemModule()).getInstance(LocationsDao.class);
	private final ZonedDateTime testTime = ZonedDateTime.of(1999, 4, 2, 1, 1, 1, 1, ZoneId.of("UTC"));

	/**
	 * Tests (available) CRUD operations
	 */
	@Test
	public void testCrud() {

		// Given
		AutoLogLocation item1 = new AutoLogLocation("a", 1, 2, 3, 4, "dev1", testTime, testTime.plusDays(1), Optional.of(testTime.plusMonths(1)));
		AutoLogLocation item2 = new AutoLogLocation("b", 5, 6, 7, 8, "dev2", testTime.plusMinutes(2), testTime.plusMinutes(5), Optional.empty());

		// When
		dao.save(TENANT_ID, ImmutableList.of(item1, item2));
		List<AutoLogLocation> fetchedItems = dao.listAllAuto(TENANT_ID).sorted((a, b) -> a.getId().compareTo(b.getId())).collect(Collectors.toList());

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
