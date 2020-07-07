package org.jfl110.mylocation;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Tests {@link InsertDefaultData}
 * 
 * @author jim
 *
 */
public class TestInsertDefaultData {

	private static final String TENANT_ID = "live";

	private final LocationsDao locationsDao = mock(LocationsDao.class);
	private final InsertDefaultData task = new InsertDefaultData(locationsDao, System.out::println);

	/**
	 * Tests nothing is inserted if the item exists already
	 */
	@Test
	public void testNoInsert() {
		// Given
		when(locationsDao.defaultItemExists(TENANT_ID)).thenReturn(true);

		// When
		task.run();

		// Then
		verify(locationsDao, Mockito.never()).saveDefaultManualItem(eq(TENANT_ID), Mockito.any(ManualLogLocation.class));
	}


	/**
	 * Tests the inserted values
	 */
	@Test
	public void testInsert() {
		// Given
		when(locationsDao.defaultItemExists(TENANT_ID)).thenReturn(false);

		// When
		task.run();

		// Then
		ArgumentCaptor<ManualLogLocation> captor = ArgumentCaptor.forClass(ManualLogLocation.class);
		verify(locationsDao, Mockito.times(1)).saveDefaultManualItem(eq(TENANT_ID), captor.capture());

		assertEquals("def", captor.getValue().getId());
		assertEquals(0, captor.getValue().getLatitude(), 0d);
		assertEquals(0, captor.getValue().getLongitude(), 0d);
		assertEquals(25, captor.getValue().getAccuracy().orElse(0f), 0d);
		assertEquals("title", captor.getValue().getTitle().orElse(null));
		assertEquals("2020-01-15T00:00Z[UTC]", captor.getValue().getTime().toString());
	}
}
