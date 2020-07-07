package org.jfl110.mylocation;

import static org.jfl110.mylocation.MyLocationAppConfig.LIVE_TENNANT_ID;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.inject.Inject;

import org.jfl110.app.Logger;
import org.jfl110.aws.AfterInjectorCreatedEvent;

/**
 * Startup task to insert the default ManualLocationsDAO item which serves as a
 * template for manual insertion.
 * 
 * @author jim
 *
 */
class InsertDefaultData implements AfterInjectorCreatedEvent {

	private final LocationsDao dao;
	private final Logger logger;

	@Inject
	InsertDefaultData(LocationsDao dao, Logger logger) {
		this.dao = dao;
		this.logger = logger;
	}


	@Override
	public void run() {
		if (!dao.defaultItemExists(LIVE_TENNANT_ID)) {
			logger.log("Inserting default ManualLocationItem");
			dao.saveDefaultManualItem(LIVE_TENNANT_ID,
					new ManualLogLocation(
							LocationsDao.DEFAULT_ITEM_SORT_KEY,
							Optional.of("title"),
							Optional.of("notes"),
							0,
							0,
							Optional.of(25f),
							ZonedDateTime.of(2020, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC")),
							true,
							Optional.empty()));
		}
	}
}
