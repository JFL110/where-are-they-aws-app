package org.jfl110.mylocation.photos;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.jfl110.aws.DynamoDBTestingRule;
import org.jfl110.genericitem.DynamoGenericItemModule;
import org.jfl110.util.LambdaUtils;
import org.junit.ClassRule;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests {@link PhotoDao}
 * 
 * @author jim
 *
 */
public class TestPhotoDao {

	@ClassRule public static final DynamoDBTestingRule dynamoRule = new DynamoDBTestingRule(DynamoGenericItemModule.itemClazz());

	private static final String TENANT_ID = "test";
	private final PhotoDao dao = dynamoRule.injector(new DynamoGenericItemModule()).getInstance(PhotoDao.class);
	private final ZonedDateTime testTime = ZonedDateTime.of(1999, 4, 2, 1, 1, 1, 1, ZoneId.of("UTC"));

	/**
	 * Test CRUD operations
	 */
	@Test
	public void testCrud() {
		// Given
		Photo photo1 = new Photo("id1",
				55.0,
				33.2,
				"http://photo",
				Optional.of("http:://thumb"),
				testTime,
				Optional.of("title"),
				Optional.of("desc"));
		Photo photo2 = new Photo("id2",
				22.5,
				33.5,
				"http://photo2",
				Optional.empty(),
				testTime,
				Optional.empty(),
				Optional.empty());

		// When
		dao.save(TENANT_ID, ImmutableList.of(photo1, photo2));
		ImmutableList<Photo> photos = LambdaUtils.toList(dao.listAll(TENANT_ID).sorted((p1, p2) -> p1.getId().compareTo(p2.getId())));

		// Then
		assertEquals(2, photos.size());
		assertEquals(photo1.getId(), photos.get(0).getId());
		assertEquals(photo1.getLatitude(), photos.get(0).getLatitude(), 0);
		assertEquals(photo1.getLongitude(), photos.get(0).getLongitude(), 0);
		assertEquals(photo1.getDescription(), photos.get(0).getDescription());
		assertEquals(photo1.getUrl(), photos.get(0).getUrl());
		assertEquals(photo1.getThumbnailUrl(), photos.get(0).getThumbnailUrl());
		assertEquals(photo1.getTitle(), photos.get(0).getTitle());
		assertEquals(photo1.getTime(), photos.get(0).getTime());

		assertEquals(photo2.getId(), photos.get(1).getId());
		assertEquals(photo2.getLatitude(), photos.get(1).getLatitude(), 0);
		assertEquals(photo2.getLongitude(), photos.get(1).getLongitude(), 0);
		assertEquals(photo2.getDescription(), photos.get(1).getDescription());
		assertEquals(photo2.getUrl(), photos.get(1).getUrl());
		assertEquals(photo2.getThumbnailUrl(), photos.get(1).getThumbnailUrl());
		assertEquals(photo2.getTitle(), photos.get(1).getTitle());
		assertEquals(photo2.getTime(), photos.get(1).getTime());

		// When
		dao.delete(TENANT_ID, photo2);
		photos = LambdaUtils.toList(dao.listAll(TENANT_ID));

		// Then
		assertEquals(1, photos.size());
		assertEquals(photo1.getId(), photos.get(0).getId());
	}
}
