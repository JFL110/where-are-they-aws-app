package org.jfl110.mylocation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jfl110.app.Logger;
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
	
	private final ManualLocationsDAO manualLocationsDAO = mock(ManualLocationsDAO.class);
	private final Logger logger = mock(Logger.class);
	private final InsertDefaultData task = new InsertDefaultData(manualLocationsDAO, logger);

	/**
	 * Tests nothing is inserted if the item exists already
	 */
	@Test
	public void testNoInsert() {
		// Given
		when(manualLocationsDAO.defaultItemExists()).thenReturn(true);

		// When
		task.run();

		// Then
		verify(manualLocationsDAO, Mockito.never()).saveDefaultItem(Mockito.any(ManualLocationItem.class));
	}


	/**
	 * Tests the inserted values
	 */
	@Test
	public void testInsert() {
		// Given
		when(manualLocationsDAO.defaultItemExists()).thenReturn(false);

		// When
		task.run();

		// Then
		ArgumentCaptor<ManualLocationItem> captor = ArgumentCaptor.forClass(ManualLocationItem.class);
		verify(manualLocationsDAO, Mockito.times(1)).saveDefaultItem(captor.capture());

		assertEquals("template", captor.getValue().getId());
		assertEquals(0, captor.getValue().getLatitude(), 0d);
		assertEquals(0, captor.getValue().getLongitude(), 0d);
		assertEquals(25, captor.getValue().getAccuracy(), 0d);
		assertEquals("title", captor.getValue().getTitle());
		assertEquals("2020-01-15T00:00:00Z[UTC]", captor.getValue().getTime());

	}
}
