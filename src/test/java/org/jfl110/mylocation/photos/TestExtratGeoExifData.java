package org.jfl110.mylocation.photos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.jfl110.mylocation.photos.ExtractGeoExifData.ExtractedPhotoDetails;
import org.junit.Test;

/**
 * Tests {@link ExtractGeoExifData}
 * 
 * @author jim
 *
 */
public class TestExtratGeoExifData {

	private static final String TEST_PHOTOT_1 = "test-photo-1.JPG";

	private final ExtractGeoExifData extractor = new ExtractGeoExifData();

	/**
	 * Extract data from a test photo and confirm the latitude, longitude and time
	 * match
	 */
	@Test
	public void testHasGeoTag() {
		Optional<ExtractedPhotoDetails> data = extractor.extract(getClass().getResourceAsStream(TEST_PHOTOT_1), System.out::println);

		assertTrue(data.isPresent());
		assertEquals(43.7271, data.get().getLatitude(), 0.01d);
		assertEquals(7.420472, data.get().getLongitude(), 0.01d);
		assertEquals(LocalDateTime.of(2020, 1, 30, 18, 13, 27), data.get().getTime());
	}
}