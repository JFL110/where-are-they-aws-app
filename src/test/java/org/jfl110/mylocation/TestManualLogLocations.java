package org.jfl110.mylocation;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jfl110.aws.DynamoDBTestingRule;
import org.jfl110.genericitem.DynamoGenericItemModule;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * Tests for {@link ManualLocationsDAO}
 * 
 * @author jim
 *
 */
public class TestManualLogLocations {

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
		ManualLogLocation item = new ManualLogLocation(
				"id-1",
				Optional.of("the-title"),
				Optional.empty(),
				55d,
				22d,
				Optional.of(35f),
				ZonedDateTime.of(2020, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC")),
				true,
				Optional.empty());

		ManualLogLocation defaultItem = new ManualLogLocation(
				LocationsDao.DEFAULT_ITEM_SORT_KEY,
				Optional.of("title"),
				Optional.of("notes"),
				0,
				0,
				Optional.of(25f),
				ZonedDateTime.of(2020, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC")),
				true,
				Optional.empty());

		assertFalse(dao.defaultItemExists(TENANT_ID));

		// When
		dao.saveManualItem(TENANT_ID, item);
		dao.saveDefaultManualItem(TENANT_ID, defaultItem);
		List<ManualLogLocation> fetchedItems = dao.listAllManual(TENANT_ID).collect(Collectors.toList());

		// Then
		assertEquals(1, fetchedItems.size());
		assertEquals(item.getId(), fetchedItems.get(0).getId());
		assertEquals(item.getLatitude(), fetchedItems.get(0).getLatitude(), 0.001);
		assertEquals(item.getLongitude(), fetchedItems.get(0).getLongitude(), 0.001);
		assertEquals(item.getAccuracy().orElse(0f), fetchedItems.get(0).getAccuracy().orElse(0f), 0.001);
		assertEquals(item.getTime(), fetchedItems.get(0).getTime());
		assertEquals(item.getEndTime(), fetchedItems.get(0).getEndTime());
		assertEquals(item.getTitle(), fetchedItems.get(0).getTitle());
		assertEquals(item.getNotes(), fetchedItems.get(0).getNotes());
		assertEquals(item.isNight(), fetchedItems.get(0).isNight());
		assertTrue(dao.defaultItemExists(TENANT_ID));
	}


	/**
	 * Test that its only possible to save a default item with a default id
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCannotSaveNonDefaultItem() {
		ManualLogLocation badDefaultItem = new ManualLogLocation(
				"not-the-key",
				Optional.of("title"),
				Optional.of("notes"),
				0,
				0,
				Optional.of(25f),
				ZonedDateTime.of(2020, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC")),
				true,
				Optional.empty());
		dao.saveDefaultManualItem(TENANT_ID, badDefaultItem);
	}
}
