package org.jfl110.mylocation;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.jfl110.aws.DynamoDBTestingRule;
import org.jfl110.aws.dynamodb.DynamoDBMapperSupplier;
import org.junit.ClassRule;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.inject.Injector;

/**
 * Tests for {@link ManualLocationsDAO}
 * 
 * @author jim
 *
 */
public class TestManualLocationDAO {

	@ClassRule public static final DynamoDBTestingRule dynamoRule = new DynamoDBTestingRule(ManualLocationItem.class);

	private final Injector injector= dynamoRule.injector();
	private final ManualLocationsDAO dao  = injector.getInstance(ManualLocationsDAO.class);
	private final DynamoDBMapper mapper  = injector.getInstance(DynamoDBMapperSupplier.class).get();

	/**
	 * Tests (available) CRUD operations
	 */
	@Test
	public void testCrud() {
		// Given
		ManualLocationItem item = new ManualLocationItem();
		item.setId("someId");
		item.setLatitude(0);
		item.setLongitude(0);
		item.setNight(true);
		item.setTime(ManualLocationItem.DATE_FORMAT.format(ZonedDateTime.of(2020, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC"))));
		item.setAccuracy(25f);
		item.setTitle("title");
		item.setNotes("notes");
		
		ManualLocationItem defaultItem = new ManualLocationItem();
		defaultItem.setId(ManualLocationsDAO.DEFAULT_ID);
		defaultItem.setLatitude(0);
		defaultItem.setLongitude(0);
		defaultItem.setNight(true);
		defaultItem.setTime(ManualLocationItem.DATE_FORMAT.format(ZonedDateTime.of(2020, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC"))));
		defaultItem.setAccuracy(25f);
		defaultItem.setTitle("title");
		defaultItem.setNotes("notes");

		// When
		mapper.save(item);
		dao.saveDefaultItem(defaultItem);
		List<ManualLocationItem> fetchedItems = dao.listAll().collect(Collectors.toList());

		// Then
		assertEquals(1, fetchedItems.size());
		assertEquals(item.getId(), fetchedItems.get(0).getId());
		assertEquals(item.getLatitude(), fetchedItems.get(0).getLatitude(), 0.001);
		assertEquals(item.getLongitude(), fetchedItems.get(0).getLongitude(), 0.001);
		assertEquals(item.getAccuracy(), fetchedItems.get(0).getAccuracy(), 0.001);
		assertEquals(item.getTime(), fetchedItems.get(0).getTime());
		assertEquals(item.getEndTime(), fetchedItems.get(0).getEndTime());
		assertEquals(item.getTitle(), fetchedItems.get(0).getTitle());
		assertEquals(item.getNotes(), fetchedItems.get(0).getNotes());
		assertEquals(item.isNight(), fetchedItems.get(0).isNight());
	}
	
	
	/**
	 * Test that its only possible to save a default item with a default id
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCannotSaveNonDefaultItem() {
		ManualLocationItem item = new ManualLocationItem();
		item.setId("someId");
		dao.saveDefaultItem(item);

	}
}
