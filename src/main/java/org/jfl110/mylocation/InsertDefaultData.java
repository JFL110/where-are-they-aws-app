package org.jfl110.mylocation;

import java.time.ZoneId;
import java.time.ZonedDateTime;

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

	private final ManualLocationsDAO manualLocationsDAO;
	private final Logger logger;

	@Inject
	InsertDefaultData(ManualLocationsDAO manualLocationsDAO, Logger logger) {
		this.manualLocationsDAO = manualLocationsDAO;
		this.logger = logger;
	}


	@Override
	public void run() {
		if (!manualLocationsDAO.defaultItemExists()) {
			logger.log("Inserting default ManualLocationItem");
			ManualLocationItem item = new ManualLocationItem();
			item.setId(ManualLocationsDAO.DEFAULT_ID);
			item.setLatitude(0);
			item.setLongitude(0);
			item.setNight(true);
			item.setTime(ManualLocationItem.DATE_FORMAT.format(ZonedDateTime.of(2020, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC"))));
			item.setAccuracy(25f);
			item.setTitle("title");
			manualLocationsDAO.saveDefaultItem(item);
		}
	}
}
